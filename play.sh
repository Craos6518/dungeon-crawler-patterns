#!/bin/bash
# Script de inicio rápido para el juego interactivo
# Uso: ./play.sh

echo "🎮 Iniciando Dungeon Crawler Interactivo..."
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: Ejecuta este script desde la raíz del proyecto (dungeon-crawler-patterns/)"
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
mvn exec:java -Dexec.mainClass="game.InteractiveGame" -q

echo ""
echo "👋 ¡Gracias por jugar!"
