# 🎮 Guía de Ejecutables - Dungeon Crawler Patterns

**Estado**: ✅ Completamente funcional en todas las plataformas

---

## 📋 Resumen Rápido

| Plataforma       | Tipo         | Disponible | Estado           | Guía                                         |
| ---------------- | ------------ | ---------- | ---------------- | -------------------------------------------- |
| **🐧 Linux**     | DEB          | ✅ Sí      | Listo            | [LINUX_EXECUTABLES.md](LINUX_EXECUTABLES.md) |
| **🐧 Linux**     | RPM          | ✅ Sí      | Listo            | [LINUX_EXECUTABLES.md](LINUX_EXECUTABLES.md) |
| **🪟 Windows**   | .EXE         | ⏳ No\*    | Requiere Windows | [WINDOWS_EXE_GUIDE.md](WINDOWS_EXE_GUIDE.md) |
| **🪟 Windows**   | App-Image    | ⏳ No\*    | Requiere Windows | [WINDOWS_OPTIONS.md](WINDOWS_OPTIONS.md)     |
| **🪟 Windows**   | JAR + Script | ✅ Sí      | Listo            | [play.bat](play.bat)                         |
| **🍎 macOS**     | JAR          | ✅ Sí      | Listo            | `java -jar ...`                              |
| **🔧 Universal** | JAR + Maven  | ✅ Sí      | Listo            | `mvn javafx:run`                             |

> \* Puede generarse en una máquina Windows ejecutando el script `package-windows.ps1`

---

## 🚀 Inicio Rápido por Plataforma

### Linux - Debian/Ubuntu

```bash
# Instalar
sudo apt install ./target/packages/dungeon-crawler-patterns_1.0-1_amd64.deb

# Ejecutar
dungeon-crawler-patterns
```

**[Ver detalles →](LINUX_EXECUTABLES.md)**

---

### Linux - Fedora/RedHat/CentOS

```bash
# Instalar
sudo dnf install ./target/packages/dungeon-crawler-patterns-1.0-1.x86_64.rpm

# Ejecutar
dungeon-crawler-patterns
```

**[Ver detalles →](LINUX_EXECUTABLES.md)**

---

### Windows - Opción 1: JAR + Script (Sin instalación)

```batch
REM 1. Desde la carpeta del proyecto:
play.bat

REM 2. O manualmente:
java -cp target\dungeon-crawler-patterns-1.0-SNAPSHOT.jar;target\dependency\* ^
     game.ui.GameWebApplication
```

**Requiere**: Java 17 instalado

---

### Windows - Opción 2: .EXE (Instalador profesional)

```batch
REM 1. Generar (solo en Windows con WiX Toolset):
.\package-windows.ps1

REM 2. Instalar:
.\target\packages\dungeon-crawler-patterns-1.0.0.exe

REM 3. Ejecutar:
Busca "Dungeon Crawler Patterns" en el Menú Inicio
```

**[Ver guía completa →](WINDOWS_EXE_GUIDE.md)**

---

### macOS

```bash
# Opción 1: JAR directo
java -jar target/dungeon-crawler-patterns-1.0-SNAPSHOT.jar

# Opción 2: Maven
mvn javafx:run

# Opción 3: Crear app-image en macOS
./package-macos.sh  # (si está disponible)
```

---

### Cualquier plataforma con Maven

```bash
mvn clean javafx:run
```

---

## 📦 Archivos Disponibles Actualmente

```
✅ GENERADOS (En este repositorio)
├── target/packages/
│   ├── dungeon-crawler-patterns_1.0-1_amd64.deb           (137 MB) ← Linux DEB
│   └── dungeon-crawler-patterns-1.0-1.x86_64.rpm          (137 MB) ← Linux RPM
├── target/dungeon-crawler-patterns-1.0-SNAPSHOT.jar       (~30 MB) ← JAR universal
├── target/dependency/                                      (libs)
├── play.bat                                                ← Windows
├── play.sh                                                 ← Linux/Mac
└── play-gui.sh                                             ← Linux/Mac GUI

⏳ POR GENERAR (Requiere ejecutar en esa plataforma)
├── target/packages/dungeon-crawler-patterns-1.0.0.exe     ← Windows
└── target/packages/dungeon-crawler-patterns-1.0.0.msi     ← Windows
```

---

## 📊 Comparativa Completa

| Característica     | .exe         | App-Image    | JAR             | Script       |
| ------------------ | ------------ | ------------ | --------------- | ------------ |
| **Plataforma**     | Windows      | Windows      | Todas           | Windows      |
| **Instalador**     | ✅ Sí        | ❌ No        | ❌ No           | ❌ No        |
| **Java embebido**  | ✅ Sí        | ✅ Sí        | ❌ No           | ❌ No        |
| **Tamaño**         | 140 MB       | 140 MB       | 30 MB           | <1 KB        |
| **Portabilidad**   | ❌ No        | ✅ Sí        | ✅ Sí           | ✅ Sí        |
| **Generación**     | Solo Windows | Solo Windows | Multiplataforma | Manual       |
| **Java requerido** | No           | No           | **Sí (v17)**    | **Sí (v17)** |
| **Experiencia UX** | ⭐⭐⭐⭐⭐   | ⭐⭐⭐       | ⭐⭐            | ⭐           |

---

## 📥 Descargas Pre-generadas

### Ejecutables listos para usar (Linux)

**Opción 1: Instalar como paquete**

- DEB: `target/packages/dungeon-crawler-patterns_1.0-1_amd64.deb`
- RPM: `target/packages/dungeon-crawler-patterns-1.0-1.x86_64.rpm`

**Opción 2: Usar JAR universal**

- JAR: `target/dungeon-crawler-patterns-1.0-SNAPSHOT.jar`
- Dependencias: `target/dependency/`

---

## 🔧 Generar Nuevos Ejecutables

### Linux (desde Linux/Windows/Mac)

```bash
# Ambos (DEB + RPM)
./package-linux.sh --type all

# Solo DEB
./package-linux.sh --type deb

# Solo RPM
./package-linux.sh --type rpm

# App-Image (portable)
./package-linux.sh --type app-image

# Sin recompilar (reutilizar artefactos)
./package-linux.sh --type all --skip-build
```

### Windows (desde Windows únicamente)

```powershell
# Instalador .exe (requiere WiX Toolset)
.\package-windows.ps1

# App-Image portable (sin WiX)
.\package-windows.ps1 -Type app-image

# Sin recompilar
.\package-windows.ps1 -SkipBuild
```

---

## 🎯 Recomendaciones de Distribución

### Para usuarios finales (máxima facilidad)

→ **Windows .EXE** + **Linux DEB/RPM**

- Instalación estándar tipo "Siguiente → Siguiente"
- Integración con sistema operativo
- Menú Inicio / Menú de aplicaciones

### Para usuarios técnicos / portabilidad

→ **App-Image portable** (Windows/Linux)

- Copia y ejecuta
- No requiere instalación
- Perfecta para USB

### Para máxima compatibilidad (todas plataformas)

→ **JAR ejecutable**

- Un único archivo para Windows/Linux/Mac
- Requiere Java 17

### Para scripts / automatización

→ **Script de inicio** (`play.bat`, `play.sh`)

- Detección automática de Java
- Ejecución simple

---

## 📋 Requisitos Mínimos por Plataforma

| Plataforma | Opción    | Requisitos             |
| ---------- | --------- | ---------------------- |
| Linux      | DEB       | Debian 11+             |
| Linux      | RPM       | Fedora 37+, RHEL 8+    |
| Windows    | .EXE      | Windows 10+            |
| Windows    | App-Image | Windows 10+            |
| Windows    | JAR       | Windows 10+ + Java 17  |
| macOS      | JAR       | macOS 10.15+ + Java 17 |

---

## 🐛 Solución de Problemas

### "Java no encontrado"

```bash
# Linux
sudo apt install openjdk-17-jdk

# Windows (Chocolatey)
choco install temurin17jdk

# macOS (Homebrew)
brew install openjdk@17
```

### "No se encuentra el JAR"

```bash
# Recompila el proyecto
mvn clean package
mvn dependency:copy-dependencies -DincludeScope=runtime
```

### "El script .sh no tiene permisos"

```bash
chmod +x play.sh
chmod +x package-linux.sh
```

### "El archivo .exe no se genera"

- Asegúrate de estar en **Windows**
- Instala **WiX Toolset**: https://wixtoolset.org/
- Configura `JAVA_HOME` hacia un **JDK 17** (no JRE)

---

## 🎯 Próximos Pasos

### Opción 1: Usar ejecutables Linux actuales

```bash
# Ver guía detallada
cat LINUX_EXECUTABLES.md

# Instalar DEB
sudo apt install ./target/packages/dungeon-crawler-patterns_1.0-1_amd64.deb
```

### Opción 2: Generar .exe en Windows

1. Clonar proyecto en Windows
2. Seguir: [WINDOWS_EXE_GUIDE.md](WINDOWS_EXE_GUIDE.md)
3. Ejecutar: `.\package-windows.ps1`

### Opción 3: Usar JAR universal ahora

```bash
java -jar target/dungeon-crawler-patterns-1.0-SNAPSHOT.jar
```

### Opción 4: Ejecutar desde script

```bash
# Linux/Mac
./play.sh

# Windows
play.bat
```

---

## 📞 Referencias Rápidas

- **[Linux Executables](LINUX_EXECUTABLES.md)** - DEB y RPM
- **[Windows EXE Guide](WINDOWS_EXE_GUIDE.md)** - Generación de .exe
- **[Windows Options](WINDOWS_OPTIONS.md)** - Comparativa de alternativas
- **[play.bat](play.bat)** - Script Windows
- **[play.sh](play.sh)** - Script Linux/Mac

---

## 📊 Estado del Proyecto

```
✅ Compilación:     Exitosa (160 clases Java)
✅ Tests:           221/221 pasados
✅ Patrones:        10/10 implementados
✅ Linux DEB:       Generado (137 MB)
✅ Linux RPM:       Generado (137 MB)
✅ JAR Universal:   Generado (~30 MB)
✅ Scripts:         Listos (play.bat, play.sh)
⏳ Windows .EXE:    Pendiente (generable en Windows)
⏳ macOS DMG:       Futuro (generable en macOS)
```

---

**Última actualización**: 22 de abril de 2026  
**Versión**: 1.0-SNAPSHOT  
**Desarrollador**: Universidad Tecnológica de Pereira - Patrones de Diseño
