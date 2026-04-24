#!/usr/bin/env bash
# Empaquetado nativo Linux con runtime incluido (JRE + JavaFX)
#
# Uso:
#   ./scripts/package-linux.sh
#   ./scripts/package-linux.sh --type deb
#   ./scripts/package-linux.sh --type rpm
#   ./scripts/package-linux.sh --type app-image
#   ./scripts/package-linux.sh --skip-build

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "$PROJECT_DIR"

PACKAGE_TYPE="all"  # all | deb | rpm | app-image
MAIN_CLASS="game.ui.GameWebApplication"
APP_NAME="dungeon-crawler-patterns"
VENDOR="Dungeon Crawler Patterns"
APP_VERSION_OVERRIDE=""
SKIP_BUILD=0

JAVA17_CANDIDATES=(
  "/usr/lib/jvm/java-17-temurin-jdk"
  "/usr/lib/jvm/java-17-openjdk"
  "/usr/lib/jvm/temurin-17-jdk-amd64"
  "/opt/java/openjdk"
)

usage() {
  cat <<'EOF'
Empaquetado nativo Linux con jpackage.

Opciones:
  --type <all|deb|rpm|app-image>   Tipo de salida (por defecto: all => deb + rpm)
  --main-class <fqcn>              Main class (por defecto: game.ui.GameWebApplication)
  --name <nombre>                  Nombre de la app/paquete (por defecto: dungeon-crawler-patterns)
  --app-version <x.y.z>            Version numerica para jpackage (por defecto: derivada de pom.xml)
  --skip-build                     Omite Maven package/copy-dependencies
  -h, --help                       Muestra esta ayuda
EOF
}

require_cmd() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Error: comando requerido no encontrado: $1"
    exit 1
  fi
}

select_java17_home() {
  local home

  if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
    if "${JAVA_HOME}/bin/java" -version 2>&1 | grep -qE 'version "17(\.|")'; then
      echo "$JAVA_HOME"
      return 0
    fi
  fi

  for home in "${JAVA17_CANDIDATES[@]}"; do
    if [[ -x "${home}/bin/java" ]]; then
      if "${home}/bin/java" -version 2>&1 | grep -qE 'version "17(\.|")'; then
        echo "$home"
        return 0
      fi
    fi
  done

  return 1
}

sanitize_version() {
  local raw="$1"
  local cleaned

  cleaned="$(echo "$raw" | sed -E 's/[^0-9.].*$//')"
  if [[ -z "$cleaned" ]]; then
    cleaned="1.0.0"
  fi

  if [[ ! "$cleaned" =~ ^[0-9]+(\.[0-9]+)*$ ]]; then
    cleaned="1.0.0"
  fi

  echo "$cleaned"
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --type)
      PACKAGE_TYPE="${2:-}"
      shift 2
      ;;
    --main-class)
      MAIN_CLASS="${2:-}"
      shift 2
      ;;
    --name)
      APP_NAME="${2:-}"
      shift 2
      ;;
    --app-version)
      APP_VERSION_OVERRIDE="${2:-}"
      shift 2
      ;;
    --skip-build)
      SKIP_BUILD=1
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Error: opcion desconocida: $1"
      usage
      exit 1
      ;;
  esac
done

case "$PACKAGE_TYPE" in
  all|deb|rpm|app-image)
    ;;
  *)
    echo "Error: --type invalido: $PACKAGE_TYPE"
    usage
    exit 1
    ;;
esac

if [[ ! -f "pom.xml" ]]; then
  echo "Error: no se encontro pom.xml."
  exit 1
fi

if ! JAVA_HOME_SELECTED="$(select_java17_home)"; then
  echo "Error: no se encontro Java 17."
  echo "Instala Java 17 o exporta JAVA_HOME apuntando a un JDK 17 valido."
  exit 1
fi

export JAVA_HOME="$JAVA_HOME_SELECTED"
export PATH="$JAVA_HOME/bin:$PATH"

require_cmd mvn
require_cmd jlink
require_cmd jpackage

if [[ "$PACKAGE_TYPE" == "deb" || "$PACKAGE_TYPE" == "all" ]]; then
  require_cmd dpkg-deb
fi
if [[ "$PACKAGE_TYPE" == "rpm" || "$PACKAGE_TYPE" == "all" ]]; then
  require_cmd rpmbuild
fi

if [[ "$SKIP_BUILD" -eq 0 ]]; then
  echo "[1/4] Compilando proyecto y copiando dependencias runtime..."
  mvn -B -DskipTests clean package dependency:copy-dependencies \
    -DincludeScope=runtime \
    -DoutputDirectory=target/dependency
else
  echo "[1/4] Build omitido por --skip-build"
fi

MAIN_JAR_PATH="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' | head -n 1)"
if [[ -z "$MAIN_JAR_PATH" ]]; then
  echo "Error: no se encontro el JAR principal en target/."
  exit 1
fi
MAIN_JAR_FILE="$(basename "$MAIN_JAR_PATH")"

JPACKAGE_INPUT="target/jpackage-input"
rm -rf "$JPACKAGE_INPUT"
mkdir -p "$JPACKAGE_INPUT"
cp "$MAIN_JAR_PATH" "$JPACKAGE_INPUT/"
cp target/dependency/*.jar "$JPACKAGE_INPUT/"

RAW_VERSION="$(sed -n '0,/<version>/{s:.*<version>\(.*\)</version>.*:\1:p}' pom.xml | head -n 1)"
if [[ -n "$APP_VERSION_OVERRIDE" ]]; then
  APP_VERSION="$(sanitize_version "$APP_VERSION_OVERRIDE")"
else
  APP_VERSION="$(sanitize_version "$RAW_VERSION")"
fi

mapfile -t JAVAFX_JARS < <(find target/dependency -maxdepth 1 -type f -name 'javafx-*.jar' | sort)
if [[ "${#JAVAFX_JARS[@]}" -eq 0 ]]; then
  echo "Error: no se encontraron JARs de JavaFX en target/dependency."
  echo "Ejecuta primero Maven con dependency:copy-dependencies."
  exit 1
fi

JAVAFX_MODULE_PATH="$(IFS=:; echo "${JAVAFX_JARS[*]}")"
RUNTIME_IMAGE="target/runtime-image"
DEST_DIR="target/packages"

JLINK_MODULES="java.base,java.desktop,java.logging,java.xml,java.scripting,jdk.jsobject,jdk.unsupported,java.net.http,java.sql,java.naming,javafx.controls,javafx.web,javafx.media"

echo "[2/4] Generando runtime con jlink..."
rm -rf "$RUNTIME_IMAGE"
jlink \
  --module-path "$JAVA_HOME/jmods:$JAVAFX_MODULE_PATH" \
  --add-modules "$JLINK_MODULES" \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --compress=2 \
  --output "$RUNTIME_IMAGE"

mkdir -p "$DEST_DIR"

run_jpackage() {
  local pkg_type="$1"
  local -a args

  args=(
    --type "$pkg_type"
    --name "$APP_NAME"
    --dest "$DEST_DIR"
    --input "$JPACKAGE_INPUT"
    --main-jar "$MAIN_JAR_FILE"
    --main-class "$MAIN_CLASS"
    --runtime-image "$RUNTIME_IMAGE"
    --vendor "$VENDOR"
    --app-version "$APP_VERSION"
  )

  if [[ "$pkg_type" == "deb" || "$pkg_type" == "rpm" ]]; then
    args+=(--linux-shortcut --linux-menu-group "Games")
  fi

  echo "[3/4] Generando paquete $pkg_type..."
  jpackage "${args[@]}"
}

if [[ "$PACKAGE_TYPE" == "all" ]]; then
  run_jpackage deb
  run_jpackage rpm
elif [[ "$PACKAGE_TYPE" == "deb" ]]; then
  run_jpackage deb
elif [[ "$PACKAGE_TYPE" == "rpm" ]]; then
  run_jpackage rpm
else
  run_jpackage app-image
fi

echo "[4/4] Artefactos generados en: $DEST_DIR"
find "$DEST_DIR" -maxdepth 1 \( -type f \( -name '*.deb' -o -name '*.rpm' -o -name '*.exe' \) -o -type d -name "$APP_NAME*" \) -print || true
