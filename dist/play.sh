#!/bin/bash
# Script para ejecutar Dungeon Crawler Patterns v2.0.0 en Linux/macOS

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="${SCRIPT_DIR}/dungeon-crawler-patterns-2.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
  echo "Error: JAR file not found at $JAR_FILE"
  exit 1
fi

# Buscar Java 17
JAVA_CMD="java"

if command -v java &> /dev/null; then
  JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")[0-9]+' | head -1)
  if [ "$JAVA_VERSION" -ge 17 ]; then
    JAVA_CMD="java"
  fi
fi

if [ -z "$JAVA_HOME" ]; then
  echo "Warning: JAVA_HOME not set. Attempting to use 'java' from PATH..."
else
  JAVA_CMD="$JAVA_HOME/bin/java"
fi

if ! command -v "$JAVA_CMD" &> /dev/null; then
  echo "Error: Java 17 or higher is required. Please install Java 17 or set JAVA_HOME."
  exit 1
fi

echo "Starting Dungeon Crawler Patterns v2.0.0..."
echo "Java: $JAVA_CMD"
"$JAVA_CMD" -jar "$JAR_FILE"
