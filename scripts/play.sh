#!/bin/bash
# Script de inicio rápido para la interfaz grafica principal
# Uso: ./play.sh

export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk

echo "🎮 Iniciando Dungeon Crawler Web..."
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
mvn javafx:run -q

echo ""
echo "👋 ¡Gracias por jugar!"
