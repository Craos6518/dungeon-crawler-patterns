# 🪟 Opciones de Ejecución en Windows

## Resumen de Alternativas

Para ejecutar **Dungeon Crawler Patterns** en Windows, tienes varias opciones:

---

## Opción 1: Instalador .EXE (Recomendado para usuarios finales)

### ✅ Ventajas

- Instalación estándar tipo "Siguiente → Siguiente → Finalizar"
- Menú Inicio integrado
- Programa registrado en "Agregar/Quitar programas"
- Atajos de escritorio automáticos

### ❌ Desventajas

- Solo generable en Windows
- Requiere WiX Toolset
- ~140 MB de instalación

### 📦 Cómo obtenerlo

1. En una máquina Windows, ejecuta:
   ```powershell
   .\package-windows.ps1
   ```
2. El .exe estará en: `target/packages/dungeon-crawler-patterns-1.0.0.exe`
3. Distribuye este archivo a usuarios

**[Ver guía completa →](WINDOWS_EXE_GUIDE.md)**

---

## Opción 2: App-Image Portable (Mejor para distribución portable)

### ✅ Ventajas

- Completamente portátil (copia y ejecuta)
- No requiere instalación
- Puede estar en USB
- Funciona desde cualquier carpeta
- ~140 MB (sin instalación)

### ❌ Desventajas

- Sin integración con Menú Inicio
- Requiere ejecutar `.bat` manualmente
- No se registra en "Agregar/Quitar programas"

### 📦 Cómo obtenerlo

**Generarlo (requiere Windows):**

```powershell
.\package-windows.ps1 -Type app-image
```

**Usar directamente:**

```cmd
cd target\packages\dungeon-crawler-patterns
bin\dungeon-crawler-patterns.bat
```

**Distribuir:**

- Comprime la carpeta `target/packages/dungeon-crawler-patterns`
- Comparte el ZIP
- Usuarios extraen y ejecutan `bin\dungeon-crawler-patterns.bat`

---

## Opción 3: JAR Ejecutable (Más simple, multiplataforma)

### ✅ Ventajas

- Funciona en cualquier plataforma (Windows, Linux, Mac)
- Pequeño (~30 MB + dependencias)
- Fácil de distribuir
- Sin herramientas especiales

### ❌ Desventajas

- Requiere Java 17 instalado en el sistema
- Menos "profesional" (no es un instalador)
- Ejecución desde línea de comandos

### 📦 Cómo obtenerlo

**En Linux, ya está generado:**

```bash
# El JAR está en:
target/dungeon-crawler-patterns-1.0-SNAPSHOT.jar
```

**Ejecutar en Windows (requiere Java 17):**

```cmd
# Opción 1: Ejecutable JAR directo
java -jar dungeon-crawler-patterns-1.0-SNAPSHOT.jar

# Opción 2: Classpath explícito
java -cp dungeon-crawler-patterns-1.0-SNAPSHOT.jar;dependency/* ^
     game.ui.GameWebApplication

# Opción 3: Usando Maven (en la carpeta del proyecto)
mvn javafx:run
```

**Crear un archivo `.bat` para facilitar ejecución:**

```batch
@echo off
setlocal enabledelayedexpansion
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17
set PATH=!JAVA_HOME!\bin;%PATH%
java -jar dungeon-crawler-patterns-1.0-SNAPSHOT.jar
pause
```

---

## Opción 4: Ejecutable desde Script Batch (Mínimo)

### ✅ Ventajas

- Súper simple
- No requiere instalación
- Directo al punto

### ❌ Desventajas

- Requiere Java 17 en el sistema
- Ventana de consola visible
- Menos profesional

### 📦 Script de ejemplo

**archivo: `play.bat`**

```batch
@echo off
echo.
echo ========================================
echo   Dungeon Crawler Patterns
echo ========================================
echo.

REM Detectar Java 17
if defined JAVA_HOME (
    set "JAVA_CMD=!JAVA_HOME!\bin\java.exe"
) else (
    set "JAVA_CMD=java.exe"
)

REM Verificar que java exista
!JAVA_CMD! -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java no encontrado. Instala Java 17 desde:
    echo https://adoptium.net/
    pause
    exit /b 1
)

REM Mostrar versión de Java
echo Java encontrado:
!JAVA_CMD! -version

echo.
echo Iniciando juego...
echo.

REM Ejecutar
!JAVA_CMD! -cp dungeon-crawler-patterns-1.0-SNAPSHOT.jar;dependency/* ^
    game.ui.GameWebApplication

pause
```

---

## Opción 5: Ejecutable Nativo (App Completo - Futuro)

Para versiones futuras, puedría investigar:

- **GraalVM Native Image** - Compila Java → Binario nativo
- **Jexe** - Wrapper simple que incluye JRE
- **Launch4j** - Crea .exe wrapper alrededor del JAR

---

## Comparativa Completa

| Aspecto                 | .EXE             | App-Image         | JAR             | Script Batch |
| ----------------------- | ---------------- | ----------------- | --------------- | ------------ |
| **Instalador**          | ✅ Sí            | ❌ No             | ❌ No           | ❌ No        |
| **Portátil**            | ❌ No            | ✅ Sí             | ✅ Sí           | ✅ Sí        |
| **Menú Inicio**         | ✅ Sí            | ❌ No             | ❌ No           | ❌ No        |
| **Requiere Java**       | ❌ No (embebido) | ❌ No (embebido)  | ✅ Sí           | ✅ Sí        |
| **Tamaño**              | ~140 MB          | ~140 MB           | ~30 MB          | ~5 KB        |
| **Generación**          | Solo Windows     | Solo Windows      | Multiplataforma | Manual       |
| **Distribución**        | .exe directo     | ZIP carpeta       | JAR + libs      | ZIP          |
| **Experiencia usuario** | ⭐⭐⭐⭐⭐ Pro   | ⭐⭐⭐ Intermedia | ⭐⭐ Básica     | ⭐ Mínima    |

---

## Recomendaciones

### Para distribución general (usuarios no técnicos)

→ **Usar .EXE** - Lo más profesional y fácil de instalar

### Para usuarios técnicos / portabilidad

→ **Usar App-Image** - Portable, flexible, no requiere instalación

### Para máxima compatibilidad (Windows + Mac + Linux)

→ **Usar JAR** - Un único artefacto para todas las plataformas

### Para máxima simplicidad

→ **Usar Script Batch** - Directo y descomplicado

---

## Archivos Disponibles Ahora (Linux)

Generados en: `target/packages/`

```
✅ dungeon-crawler-patterns_1.0-1_amd64.deb     (137 MB) - Debian/Ubuntu
✅ dungeon-crawler-patterns-1.0-1.x86_64.rpm    (137 MB) - Fedora/RedHat
📦 target/dungeon-crawler-patterns-1.0-SNAPSHOT.jar     (~30 MB) - JAR universal
📦 target/dependency/                                   - Librerías
```

---

## Próximos Pasos

### Para generar Windows en una máquina Windows:

1. **Clonar el proyecto**

   ```powershell
   git clone https://github.com/Craos6518/dungeon-crawler-patterns.git
   cd dungeon-crawler-patterns
   ```

2. **Instalar requisitos**
   - Java 17 JDK
   - Maven
   - WiX Toolset (solo para .exe)

3. **Generar**

   ```powershell
   # .EXE
   .\package-windows.ps1

   # O App-Image (sin WiX)
   .\package-windows.ps1 -Type app-image
   ```

4. **Distribuyir**
   - `target/packages/*.exe` - Para usuarios
   - `target/packages/dungeon-crawler-patterns/` - Para portabilidad

---

**Estado**: ✅ Linux ejecutables listos  
**Pendiente**: ⏳ Windows .EXE (requiere Windows)  
**Generado**: 22 de abril de 2026

[← Volver a LINUX_EXECUTABLES.md](LINUX_EXECUTABLES.md)
