#!/bin/bash
# Script de configuración automática de JAVA_HOME para Dungeon Crawler Patterns
# Uso: source scripts/setup-java.sh
#
# Detecta automáticamente la versión de Java 17 instalada y la configura

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${YELLOW}║   Configurando JAVA_HOME para Dungeon Crawler Patterns    ║${NC}"
echo -e "${YELLOW}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Detectar JAVA_HOME actual
if [ -n "$JAVA_HOME" ]; then
    echo -e "${YELLOW}JAVA_HOME actual: $JAVA_HOME${NC}"
    echo ""
fi

# Buscar Java 17
echo "Buscando Java 17..."

# Intentar ubicaciones comunes de Java 17
JAVA_17_PATHS=(
    "/usr/lib/jvm/java-17-temurin-jdk"
    "/usr/lib/jvm/java-17-openjdk"
    "/usr/lib/jvm/temurin-17-jdk-amd64"
    "/opt/java/openjdk"
)

FOUND_JAVA_17=false
for path in "${JAVA_17_PATHS[@]}"; do
    if [ -d "$path" ] && [ -f "$path/bin/java" ]; then
        echo -e "${GREEN}✓ Java 17 encontrado: $path${NC}"
        export JAVA_HOME="$path"
        FOUND_JAVA_17=true
        break
    fi
done

# Si no se encuentra en ubicaciones comunes, buscar dinámicamente
if [ "$FOUND_JAVA_17" = false ]; then
    echo "Buscando dinámicamente Java 17 en /usr/lib/jvm/..."
    JAVA_17=$(find /usr/lib/jvm -name "java-17*" -type d 2>/dev/null | head -1)
    
    if [ -n "$JAVA_17" ] && [ -f "$JAVA_17/bin/java" ]; then
        echo -e "${GREEN}✓ Java 17 encontrado: $JAVA_17${NC}"
        export JAVA_HOME="$JAVA_17"
        FOUND_JAVA_17=true
    fi
fi

# Verificar si se encontró Java 17
if [ "$FOUND_JAVA_17" = true ]; then
    # Verificar la versión
    JAVA_VERSION=$("$JAVA_HOME/bin/java" -version 2>&1 | grep -i "version" | head -1)
    echo -e "${GREEN}✓ Versión: $JAVA_VERSION${NC}"
    echo ""
    
    # Exportar PATH
    if [[ ":$PATH:" != *":$JAVA_HOME/bin:"* ]]; then
        export PATH="$JAVA_HOME/bin:$PATH"
        echo -e "${GREEN}✓ PATH actualizado${NC}"
    fi
    
    echo -e "${GREEN}✓ JAVA_HOME configurada correctamente${NC}"
    echo ""
    echo -e "${GREEN}Configuración lista para compilar y ejecutar:${NC}"
    echo "  mvn clean compile"
    echo "  java -cp target/classes game.InteractiveGame"
    echo ""
else
    echo -e "${RED}✗ No se encontró Java 17${NC}"
    echo "Por favor instala Java 17 en una de estas ubicaciones:"
    for path in "${JAVA_17_PATHS[@]}"; do
        echo "  - $path"
    done
    echo ""
    echo "O ejecuta:"
    echo "  sudo apt install openjdk-17-jdk"
fi

# Mostrar información final
echo "═══════════════════════════════════════════════════════════"
echo "JAVA_HOME = $JAVA_HOME"
echo "JAVA VERSION:"
"$JAVA_HOME/bin/java" -version 2>&1 | grep -E "version|openjdk|temurin"
echo "═══════════════════════════════════════════════════════════"
