#!/bin/bash
# Script de inicio rápido para la interfaz grafica principal
# Uso: ./scripts/play.sh

export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "$PROJECT_DIR"

echo "🎮 Iniciando Dungeon Crawler Web..."
echo ""

if [ ! -f "pom.xml" ]; then
    echo "❌ Error: no se encontró pom.xml en la raíz del proyecto"
    exit 1
fi

# Compilar si es necesario
if [ ! -d "target/classes" ]; then
    echo "📦 Compilando proyecto..."
    mvn compile -q
    if [ $? -ne 0 ]; then
        echo "❌ Error al compilar"
        exit 1
    fi
fi

# Ejecutar el juego
echo "🚀 Lanzando juego..."
echo ""
mvn javafx:run -q

echo ""
echo "👋 ¡Gracias por jugar!"
