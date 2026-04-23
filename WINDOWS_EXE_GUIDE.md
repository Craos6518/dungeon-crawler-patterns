# 🎮 Ejecutable Windows (.EXE) - Dungeon Crawler Patterns

## ⚠️ Generación en Windows Requerida

Debido a que `jpackage` en Windows requiere **WiX Toolset** para crear instaladores nativos, el archivo .exe **debe generarse en una máquina Windows**.

## Opción 1: Generar .EXE en Windows (Recomendado)

### Requisitos Previos en Windows

1. **Windows 10 o superior** (x64)
2. **PowerShell 5.0+** (incluido en Windows 10+)
3. **Java 17 JDK**
   - Descargar desde: https://adoptium.net/ o https://www.microsoft.com/openjdk
   - Configurar `JAVA_HOME` apuntando al JDK 17
4. **Maven** (`mvn` en PATH)

- Si Maven solicita un toolchain, crea `~/.m2/toolchains.xml` apuntando al JDK 17

5. **WiX Toolset** (necesario para generar .exe)
   - Descargar: https://wixtoolset.org/releases/
   - O instalar vía Chocolatey: `choco install wixtoolset`

### Procedimiento en Windows

#### Paso 1: Clonar o Descargar el Proyecto

```powershell
cd C:\Users\YourUser\Desktop
git clone https://github.com/Craos6518/dungeon-crawler-patterns.git
cd dungeon-crawler-patterns
```

#### Paso 2: Ejecutar el Script de Empaquetamiento

Si PowerShell bloquea la ejecución de scripts no firmados, usa una de estas opciones:

```powershell
# Opción puntual para esta ejecución
powershell -ExecutionPolicy Bypass -File .\package-windows.ps1

# Opción persistente para el usuario actual
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

```powershell
# Generar .exe estándar
powershell -ExecutionPolicy Bypass -File .\package-windows.ps1

# O con opciones específicas
powershell -ExecutionPolicy Bypass -File .\package-windows.ps1 -Type exe
powershell -ExecutionPolicy Bypass -File .\package-windows.ps1 -Type app-image  # Alternativa: App-Image portable

# Reutilizar artefactos existentes (no recompilar)
powershell -ExecutionPolicy Bypass -File .\package-windows.ps1 -SkipBuild
```

#### Paso 3: Localizar los Artefactos

Los ejecutables se generarán en:

```
target/packages/
├── dungeon-crawler-patterns-1.0.0.exe    (Instalador)
└── dungeon-crawler-patterns-1.0.0.msi    (Instalador alternativo)
```

### Instalación del .EXE

Una vez generado, ejecuta el instalador:

```powershell
.\target\packages\dungeon-crawler-patterns-1.0.0.exe
```

El instalador:

- Copia la aplicación a `C:\Program Files\dungeon-crawler-patterns\`
- Crea accesos directos en el Escritorio y Menú Inicio
- Registra la aplicación en "Agregar/Quitar programas"

Después de instalar, busca **"Dungeon Crawler Patterns"** en el Menú Inicio.

---

## Opción 2: Usar App-Image Portable (Sin Instalador)

Si generas un **app-image** en Windows, obtendrás una carpeta autocontenida que puedes ejecutar directamente:

```powershell
.\package-windows.ps1 -Type app-image
```

Resultado:

```
target/packages/dungeon-crawler-patterns/
├── bin/
│   └── dungeon-crawler-patterns.bat  (Ejecutar esta)
├── runtime/                          (JRE embebido)
└── lib/                              (JARs de la aplicación)
```

**Ventajas**:

- ✅ No requiere instalación
- ✅ Portable (puedes mover la carpeta a cualquier lugar)
- ✅ Se ejecuta desde: `.\target\packages\dungeon-crawler-patterns\bin\dungeon-crawler-patterns.bat`

---

## Opción 3: Usar JAR Ejecutable (Multiplataforma)

Si prefieres algo más simple que funcione en cualquier plataforma con Java:

```powershell
# En Windows:
java -cp target/dungeon-crawler-patterns-1.0-SNAPSHOT.jar;target/dependency/* `
     game.ui.GameWebApplication

# O simplemente usar Maven:
mvn javafx:run
```

---

## Comparativa de Opciones

| Opción        | Instalador | Portabilidad | Requisitos    | Tamaño  |
| ------------- | ---------- | ------------ | ------------- | ------- |
| **.EXE**      | ✅ Sí      | ⭐⭐ Media   | WiX + Java 17 | ~140 MB |
| **App-Image** | ❌ No      | ⭐⭐⭐ Alta  | Java 17       | ~140 MB |
| **JAR**       | ❌ No      | ⭐⭐⭐ Alta  | Java 17       | ~30 MB  |

---

## Instrucciones Detalladas: Instalación de Requisitos en Windows

### 1. Instalar Java 17

**Opción A: Adoptium (Recomendado)**

```powershell
# Descargar desde: https://adoptium.net/
# Ejecutar el instalador .msi
# Marcar: "Add to PATH"
```

**Opción B: Chocolatey**

```powershell
choco install temurin17jdk
```

**Verificar instalación:**

```powershell
java -version
```

### 2. Instalar Maven

**Opción A: Chocolatey**

```powershell
choco install maven
```

**Opción B: Descargar manualmente**

- Descargar: https://maven.apache.org/download.cgi
- Extraer a `C:\Maven` (o carpeta de tu preferencia)
- Agregar `C:\Maven\bin` a PATH

**Verificar instalación:**

```powershell
mvn -v
```

### 3. Instalar WiX Toolset

**Opción A: Chocolatey (Recomendado)**

```powershell
choco install wixtoolset
```

**Opción B: Descargar manualmente**

- Ir a: https://wixtoolset.org/releases/
- Descargar y ejecutar el instalador
- Reiniciar Windows después

**Verificar instalación:**

```powershell
light --version
```

### 4. Configurar JAVA_HOME

**En PowerShell:**

```powershell
# Temporal (solo para este terminal)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.x"

# Permanente (recomendado):
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-17.0.x", "User")

# Verificar:
echo $env:JAVA_HOME
```

---

## Solución de Problemas

### Error: "jpackage no encontrado"

**Solución**: Asegúrate de que JAVA_HOME apunta al JDK (no JRE).

```powershell
ls "$env:JAVA_HOME\bin\jpackage.exe"  # Debe existir
```

### Error: "light.exe no encontrado" (WiX no instalado)

**Solución**: Instala WiX Toolset desde https://wixtoolset.org/

### Error: "mvn no encontrado"

**Solución**: Instala Maven y asegúrate de que está en PATH:

```powershell
mvn -v  # Debe mostrar versión
```

### La compilación falla

**Solución**: Limpia y compila nuevamente:

```powershell
mvn clean compile
```

---

## Distribución del .EXE

Una vez generado el `.exe`, puedes distribuirlo:

1. **Directamente**: El archivo `.exe` es un instalador standalone
2. **Junto con el `.msi`**: Ambos son equivalentes
3. **Con el `app-image`**: Para usuarios que prefieren portabilidad
4. **En un repositorio**: GitHub Releases, SourceForge, etc.

**Recomendación**: Usar el `.exe` para distribución estándar (usuarios normales) y el `app-image` para usuarios técnicos.

---

## Generación Automatizada (CI/CD en Windows)

Si configuras CI/CD (GitHub Actions, Azure Pipelines, etc.), puedes automatizar la generación:

```yaml
# Ejemplo para GitHub Actions en Windows
runs-on: windows-latest
steps:
  - uses: actions/setup-java@v3
    with:
      java-version: "17"
      distribution: "temurin"
  - run: choco install wixtoolset
  - run: .\package-windows.ps1
  - uses: actions/upload-artifact@v3
    with:
      path: target/packages/*.exe
```

---

## Notas Importantes

⚠️ **No es posible generar .exe desde Linux**  
`jpackage` no soporta cross-compilation de ejecutables Windows desde Linux. Requiere estar ejecutándose en Windows.

✅ **Alternativa: App-Image**  
Si necesitas algo sin instalador, genera un `app-image` en Windows - es completamente portátil.

✅ **JAR Ejecutable**  
El JAR generado funciona en cualquier plataforma con Java 17.

---

**Generado**: 22 de abril de 2026  
**Plataforma**: Windows 10+ x64  
**Java**: 17 (OpenJDK/Temurin)  
**WiX**: 3.11+
