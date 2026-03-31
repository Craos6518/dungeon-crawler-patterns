#!/usr/bin/env bash
# Script de inicio rapido para la interfaz grafica (JavaFX)
# Uso: ./play-gui.sh

set -euo pipefail

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

echo "Iniciando interfaz grafica..."
mvn -B javafx:run
