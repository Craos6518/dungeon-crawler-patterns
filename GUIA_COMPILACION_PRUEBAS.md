# Guía de Compilación y Pruebas - Dungeon Crawler Patterns

**Última actualización:** 18 de marzo de 2026

## Requisitos

- **Java:** OpenJDK 17 o Temurin JDK 17
- **Maven:** 3.6.0 o superior
- **Sistema Operativo:** Linux, macOS, o Windows (con Git Bash)

## Instalación Rápida

### 1. Instalar Java 17

**Fedora/RHEL:**
```bash
sudo dnf install java-17-openjdk java-17-openjdk-devel
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk maven
```

**macOS (con Homebrew):**
```bash
brew install openjdk@17
```

### 2. Verificar Instalación

```bash
java -version
# Debería mostrar: openjdk version "17.x.x" o similar

mvn -v
# Debería mostrar: Apache Maven 3.x.x
```

---

## Configuración de JAVA_HOME

### Opción 1: Automática con setup-java.sh (Recomendado)

```bash
cd dungeon-crawler-patterns
source setup-java.sh
```

Este script:
- ✓ Detecta automáticamente Java 17
- ✓ Configura JAVA_HOME en la sessión actual
- ✓ Verifica que la versión sea correcta

### Opción 2: Automática con direnv (Permanente)

Si tienes `direnv` instalado:

```bash
# Instalar direnv (si no lo tienes)
# https://direnv.net/docs/installation.html

# En el directorio del proyecto
direnv allow .envrc

# Ahora JAVA_HOME se configurará automáticamente cada vez que entres
```

### Opción 3: Manual

```bash
# Encontrar tu instalación de Java 17
find /usr/lib/jvm -name "java-17*" -type d

# Configurar en la terminal actual
export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
export PATH=$JAVA_HOME/bin:$PATH

# Verificar
java -version
```

### Opción 4: Permanente en ~/.bashrc o ~/.zshrc

Agregar estas líneas al final de tu archivo de shell (`~/.bashrc` o `~/.zshrc`):

```bash
# Java 17 configuration for Dungeon Crawler
export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
export PATH=$JAVA_HOME/bin:$PATH
```

Luego recarga:
```bash
source ~/.bashrc  # o source ~/.zshrc
```

---

## Compilación

### Compilación Limpia

```bash
mvn clean compile
```

Esto:
- ✓ Limpia compilaciones previas
- ✓ Compila todo el código fuente
- ✓ Valida la sintaxis de Java 17

### Compilación Silenciosa (Sin Output)

```bash
mvn clean compile -q
```

### Compilación con Verbose (Detail)

```bash
mvn clean compile -X  # Extremadamente detallado
```

---

## Ejecución

### Juego Interactivo Completo

```bash
# Con RuntimeStates (Implementación de Producción)
mvn exec:java@run-interactive-game

# O directamente desde la clase compilada
java -cp target/classes game.InteractiveGame
```

### Demo Académica del Patrón State

```bash
# Legacy States (Demostración educativa del patrón State)
java -cp target/classes game.demo.LegacyStatePatternDemo
```

### Otras Demostraciones

```bash
# Patrones Creacionales
java -cp target/classes game.demo.PatronesCreacionalesDemo

# Patrones Estructurales
java -cp target/classes game.demo.PatronesEstructuralesDemo

# Patrones de Comportamiento
java -cp target/classes game.demo.PatronesComportamientoDemo

# Integración Completa
java -cp target/classes game.demo.IntegracionCompletaDemo
```

---

## Pruebas Automatizadas

### Ejecutar Todas las Pruebas

```bash
mvn test
```

### Ejecutar Pruebas de una Clase Específica

```bash
mvn test -Dtest=BehavioralPatternsIntegrationTest
mvn test -Dtest=CombatIntegrationTest
```

### Ejecutar Pruebas y Mostrar Reportes

```bash
mvn clean test
# Los reportes se generan en: target/surefire-reports/
```

### Ver Reportes de Pruebas

```bash
# Los archivos se encuentran en:
ls target/surefire-reports/

# Para ver un reporte específico:
cat target/surefire-reports/game.integration.combat.CombatIntegrationTest.txt
```

---

## Workflow Completo (Recomendado)

```bash
# 1. Entrar al directorio del proyecto
cd dungeon-crawler-patterns

# 2. Configurar JAVA_HOME (si no está configurado)
source setup-java.sh

# 3. Compilación limpia
mvn clean compile

# 4. Ejecutar pruebas (opcional, pero recomendado)
mvn test

# 5. Ejecutar el juego
java -cp target/classes game.InteractiveGame

# O ejecutar una demo específica
java -cp target/classes game.demo.LegacyStatePatternDemo
```

---

## Solución de Problemas

### Error: "JAVA_HOME environment variable is not defined correctly"

**Solución:**
```bash
# 1. Encontrar Java 17
find /usr/lib/jvm -name "java-17*" -type d

# 2. Configurar manualmente
export JAVA_HOME=/ruta/encontrada

# 3. Ejecutar setup-java.sh
source setup-java.sh
```

### Error: "The JAVA_HOME environment variable... is not defined"

**Solución:**
Este es un error específico de Maven, no de Java. Asegúrate de que:
1. `JAVA_HOME` está exportado (no solo configurado)
2. La ruta es válida: `ls $JAVA_HOME/bin/java`

```bash
# Verificar configuración actual
echo $JAVA_HOME
ls -la $JAVA_HOME/bin/java

# Si no funciona, usa setup-java.sh
source setup-java.sh
```

### Error: "Class not found: game.InteractiveGame"

**Solución:**
Asegúrate de haber compilado primero:
```bash
mvn clean compile
java -cp target/classes game.InteractiveGame
```

### Maven No Encuentra Java

**Solución:**
```bash
# Ver qué Java Maven está usando
mvn -version

# Si no es Java 17, configura JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
mvn clean compile
```

---

## Integración con VS Code

1. **Instala la extensión "Extension Pack for Java"**
   - Autor: Microsoft
   - ID: vscjava.vscode-java-pack

2. **Configura el JDK en VS Code:**
   - Abre la Paleta de Comandos: `Ctrl+Shift+P`
   - Escribe: `Java: Configure Java Runtime`
   - Selecciona el JDK 17 de tu sistema

3. **VS Code debería compilar y ejecutar automáticamente**

---

## Integración con IntelliJ IDEA

1. **File → Project Structure → Project**
2. **SDK:** Selecciona o agrega Java 17
3. **Language Level:** 17
4. **Click OK**

IDEA debería detectar automáticamente el proyecto Maven.

---

## Estadísticas de Compilación

```
Proyecto: Dungeon Crawler Patterns
Total de clases: ~150+
Líneas de código: ~15,000+
Patrones implementados: 10
Estado de compilación: ✓ LIMPIA
JDK requerido: 17
Maven version: 3.6.0+
```

---

## Checklist de Configuración

- ✓ Java 17 instalado
- ✓ Maven instalado
- ✓ JAVA_HOME configurado
- ✓ PATH contiene $JAVA_HOME/bin
- ✓ `mvn --version` muestra Java 17
- ✓ `mvn clean compile` no tiene errores
- ✓ `java -cp target/classes game.InteractiveGame` ejecuta el juego

Si todos los puntos están marcados, ¡estás listo para desarrollar! 🚀

---

## Referencias

- **Apache Maven:** https://maven.apache.org/
- **OpenJDK 17:** https://openjdk.java.net/projects/jdk/17/
- **direnv:** https://direnv.net/
- **Java Temurin:** https://adoptium.net/

