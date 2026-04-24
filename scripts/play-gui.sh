#!/usr/bin/env bash
# Script de inicio rapido para la interfaz grafica (JavaFX)
# Uso: ./play-gui.sh

set -euo pipefail

USE_XVFB=0
if [[ "${1:-}" == "--xvfb" ]]; then
  USE_XVFB=1
fi

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

if [[ ! -f "pom.xml" ]]; then
  echo "Error: ejecuta este script desde la raiz del proyecto dungeon-crawler-patterns."
  exit 1
fi

JAVA17_CANDIDATES=(
  "/usr/lib/jvm/java-17-temurin-jdk"
  "/usr/lib/jvm/java-17-openjdk"
  "/usr/lib/jvm/temurin-17-jdk-amd64"
  "/opt/java/openjdk"
)

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

if ! JAVA_HOME_SELECTED="$(select_java17_home)"; then
  echo "Error: no se encontro Java 17."
  echo "Instala Java 17 o exporta JAVA_HOME apuntando a un JDK 17 valido."
  exit 1
fi

export JAVA_HOME="$JAVA_HOME_SELECTED"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Usando JAVA_HOME=$JAVA_HOME"
"$JAVA_HOME/bin/java" -version 2>&1 | head -n 1

can_open_x11_display() {
  if [[ -z "${DISPLAY:-}" ]]; then
    return 1
  fi

  if command -v xdpyinfo >/dev/null 2>&1; then
    if xdpyinfo -display "${DISPLAY}" >/dev/null 2>&1; then
      return 0
    fi
    return 1
  fi

  if command -v xset >/dev/null 2>&1; then
    if xset q >/dev/null 2>&1; then
      return 0
    fi
    return 1
  fi

  # Si no hay herramientas de verificacion, asumimos DISPLAY usable.
  return 0
}

if [[ "$USE_XVFB" -eq 1 ]]; then
  if ! command -v xvfb-run >/dev/null 2>&1; then
    echo "Error: --xvfb requiere xvfb-run, pero no esta instalado."
    echo "Instala el paquete Xvfb y vuelve a intentar."
    exit 1
  fi

  echo "Iniciando interfaz grafica sobre Xvfb..."
  xvfb-run -a mvn -B exec:java -Dexec.mainClass=game.ui.GameWebApplication
  exit $?
fi

if [[ -z "${DISPLAY:-}" ]]; then
  echo "Error: DISPLAY no esta definido."
  echo "En Linux, esta version de JavaFX necesita un DISPLAY X11 accesible."
  if [[ -n "${WAYLAND_DISPLAY:-}" ]]; then
    echo "Detectado WAYLAND_DISPLAY=${WAYLAND_DISPLAY}, pero sin DISPLAY no se puede abrir JavaFX en este entorno."
  fi
  echo
  echo "Opciones:"
  echo "  1) Abre una terminal desde tu escritorio y verifica: echo \$DISPLAY"
  echo "  2) Inicia sesion en Xorg (o habilita XWayland) y vuelve a abrir la terminal."
  echo "  3) Si usas SSH, conecta con reenvio X11: ssh -X o ssh -Y"
  if command -v xvfb-run >/dev/null 2>&1; then
    echo "  4) Para smoke tests sin UI visible: ./play-gui.sh --xvfb"
  fi
  exit 1
fi

if ! can_open_x11_display; then
  echo "Error: DISPLAY esta definido (${DISPLAY}) pero no se puede abrir."
  echo "Esto suele pasar al ejecutar fuera de la sesion grafica o por permisos X11."
  echo
  echo "Opciones:"
  echo "  1) Ejecuta desde una terminal dentro de tu escritorio grafico."
  echo "  2) Si usas SSH, entra con reenvio X11 (ssh -X o ssh -Y)."
  echo "  3) Verifica acceso: xdpyinfo -display ${DISPLAY}"
  if command -v xvfb-run >/dev/null 2>&1; then
    echo "  4) Para smoke tests sin ventana visible: ./play-gui.sh --xvfb"
  fi
  exit 1
fi

echo "Iniciando interfaz grafica..."
mvn -B exec:java -Dexec.mainClass=game.ui.GameWebApplication
