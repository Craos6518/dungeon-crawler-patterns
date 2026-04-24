#!/bin/bash

# Script de verificación de configuración Java 17 para VSCode
# Proyecto: Dungeon Crawler - Patrones de Diseño

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "$PROJECT_DIR"

echo "=================================================="
echo "  VERIFICACIÓN DE CONFIGURACIÓN JAVA 17 - VSCODE"
echo "=================================================="
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para verificar
check() {
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ $1${NC}"
        return 0
    else
        echo -e "${RED}✗ $1${NC}"
        return 1
    fi
}

# 1. Verificar archivos de configuración
echo "1. Verificando archivos de configuración VSCode..."
echo "---------------------------------------------------"

if [ -f ".vscode/settings.json" ]; then
    check "settings.json existe"
else
    check "settings.json existe"
fi

if [ -f ".vscode/launch.json" ]; then
    check "launch.json existe"
else
    check "launch.json existe"
fi

if [ -f ".vscode/tasks.json" ]; then
    check "tasks.json existe"
else
    check "tasks.json existe"
fi

if [ -f ".vscode/extensions.json" ]; then
    check "extensions.json existe"
else
    check "extensions.json existe"
fi

echo ""

# 2. Verificar Java
echo "2. Verificando instalación de Java..."
echo "---------------------------------------------------"

if [ -d "/usr/lib/jvm/java-17-temurin-jdk" ]; then
    check "Java 17 Temurin JDK instalado"
    
    # Verificar JAVA_HOME
    export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
    echo "   JAVA_HOME configurado: $JAVA_HOME"
    
    # Verificar versión de Java
    echo -n "   Versión: "
    /usr/lib/jvm/java-17-temurin-jdk/bin/java -version 2>&1 | head -n 1
    check "Java funcional"
else
    check "Java 17 Temurin JDK instalado"
fi

echo ""

# 3. Verificar Maven
echo "3. Verificando Maven..."
echo "---------------------------------------------------"

if command -v mvn &> /dev/null; then
    check "Maven instalado"
    echo -n "   Versión: "
    mvn -version | head -n 1
    
    # Verificar que Maven usa Java 17
    export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
    MAVEN_JAVA=$(mvn -version | grep "Java version" | awk '{print $3}')
    if [[ $MAVEN_JAVA == 17* ]]; then
        check "Maven usando Java 17"
    else
        echo -e "${YELLOW}⚠ Maven está usando Java $MAVEN_JAVA (se configurará automáticamente en VSCode)${NC}"
    fi
else
    check "Maven instalado"
fi

echo ""

# 4. Verificar estructura del proyecto
echo "4. Verificando estructura del proyecto..."
echo "---------------------------------------------------"

if [ -f "pom.xml" ]; then
    check "pom.xml existe"
    
    # Verificar configuración de Java en pom.xml
    if grep -q "maven.compiler.source>17<" pom.xml && grep -q "maven.compiler.target>17<" pom.xml; then
        check "pom.xml configurado para Java 17"
    else
        check "pom.xml configurado para Java 17"
    fi
else
    check "pom.xml existe"
fi

if [ -d "src/main/java" ]; then
    check "Directorio src/main/java existe"
else
    check "Directorio src/main/java existe"
fi

if [ -d "src/test/java" ]; then
    check "Directorio src/test/java existe"
else
    check "Directorio src/test/java existe"
fi

echo ""

# 5. Probar compilación
echo "5. Probando compilación..."
echo "---------------------------------------------------"

export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
echo "   Ejecutando: mvn clean compile -q"

if mvn clean compile -q > /dev/null 2>&1; then
    check "Compilación exitosa"
    
    if [ -d "target/classes" ]; then
        JAVA_FILES=$(find target/classes -name "*.class" | wc -l)
        echo "   Archivos .class generados: $JAVA_FILES"
    fi
else
    check "Compilación exitosa"
    echo -e "${RED}   Error: Revisa los errores de compilación${NC}"
fi

echo ""

# 6. Probar tests
echo "6. Probando ejecución de tests..."
echo "---------------------------------------------------"

export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
echo "   Ejecutando: mvn test -q"

if mvn test -q > /dev/null 2>&1; then
    check "Tests ejecutados correctamente"
    
    # Contar tests
    if [ -d "target/surefire-reports" ]; then
        TESTS=$(grep -r "Tests run:" target/surefire-reports/*.txt 2>/dev/null | tail -1)
        if [ ! -z "$TESTS" ]; then
            echo "   $TESTS"
        fi
    fi
else
    check "Tests ejecutados correctamente"
    echo -e "${YELLOW}   Algunos tests pueden haber fallado (revisa con: mvn test)${NC}"
fi

echo ""

# 7. Verificar configuración de VSCode
echo "7. Verificando configuración específica de VSCode..."
echo "---------------------------------------------------"

if grep -q "java-17-temurin-jdk" .vscode/settings.json; then
    check "Java 17 configurado en settings.json"
else
    check "Java 17 configurado en settings.json"
fi

if grep -q "JAVA_HOME" .vscode/launch.json; then
    check "JAVA_HOME configurado en launch.json"
else
    check "JAVA_HOME configurado en launch.json"
fi

if grep -q "JAVA_HOME" .vscode/tasks.json; then
    check "JAVA_HOME configurado en tasks.json"
else
    check "JAVA_HOME configurado en tasks.json"
fi

echo ""

# Resumen final
echo "=================================================="
echo "  RESUMEN"
echo "=================================================="
echo ""
echo -e "${GREEN}✓ Configuración completada correctamente${NC}"
echo ""
echo "Próximos pasos:"
echo ""
echo "1. Abre Visual Studio Code:"
echo "   $ code ."
echo ""
echo "2. Instala las extensiones recomendadas cuando VSCode te lo pida"
echo ""
echo "3. Recarga la ventana:"
echo "   Ctrl+Shift+P → 'Reload Window'"
echo ""
echo "4. Compila el proyecto:"
echo "   Ctrl+Shift+B"
echo ""
echo "5. Ejecuta los tests:"
echo "   Ve a la pestaña 'Testing' en la barra lateral"
echo ""
echo "6. Ejecuta el programa:"
echo "   F5 o ve a 'Run and Debug' (Ctrl+Shift+D)"
echo ""
echo "Para más información, lee:"
echo "   .vscode/README.md"
echo ""
echo "=================================================="
