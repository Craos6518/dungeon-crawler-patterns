# 🎮 Ejecutables Linux Generados - Dungeon Crawler Patterns

> 🪟 **Para Windows**: Ver [WINDOWS_EXE_GUIDE.md](WINDOWS_EXE_GUIDE.md) o [WINDOWS_OPTIONS.md](WINDOWS_OPTIONS.md)  
> 📦 **Todas las opciones**: Ver [WINDOWS_OPTIONS.md](WINDOWS_OPTIONS.md) para comparativa

## Paquetes Creados ✅

### 📦 Paquete Debian/Ubuntu (DEB)

- **Nombre**: `dungeon-crawler-patterns_1.0-1_amd64.deb`
- **Tamaño**: 137 MB
- **Ubicación**: `target/packages/dungeon-crawler-patterns_1.0-1_amd64.deb`
- **Instalación**:
  ```bash
  sudo apt install ./target/packages/dungeon-crawler-patterns_1.0-1_amd64.deb
  dungeon-crawler-patterns  # Para ejecutar después de instalar
  ```

### 📦 Paquete RPM (Fedora/RedHat/CentOS)

- **Nombre**: `dungeon-crawler-patterns-1.0-1.x86_64.rpm`
- **Tamaño**: 137 MB
- **Ubicación**: `target/packages/dungeon-crawler-patterns-1.0-1.x86_64.rpm`
- **Instalación**:

  ```bash
  sudo dnf install ./target/packages/dungeon-crawler-patterns-1.0-1.x86_64.rpm
  # o
  sudo rpm -i ./target/packages/dungeon-crawler-patterns-1.0-1.x86_64.rpm

  dungeon-crawler-patterns  # Para ejecutar después de instalar
  ```

## Características de los Paquetes

✅ **Runtime Java 17 incluido** - No requiere Java adicional  
✅ **JavaFX Web integrado** - UI completamente funcional  
✅ **Juego completo** - 221 tests pasados, 11 patrones implementados  
✅ **Menú de aplicaciones** - Integración con el sistema  
✅ **Ejecutable directo** - `dungeon-crawler-patterns` disponible en PATH

## Uso Inmediato

Después de instalar, ejecuta directamente desde la terminal:

```bash
dungeon-crawler-patterns
```

O busca "Dungeon Crawler Patterns" en el menú de aplicaciones de tu escritorio.

## Detalles Técnicos

- **Arquitectura**: x86_64 (64-bit)
- **Sistemas soportados**:
  - DEB: Debian 11+, Ubuntu 20.04+, Linux Mint 21+
  - RPM: Fedora 37+, RHEL 8+, CentOS 8+, openSUSE 15+
- **Java**: OpenJDK 17 (embebido en el paquete)
- **Dependencias**: Ninguna - completamente standalone
- **Tamaño tras instalación**: ~150-200 MB (incluye runtime completo)

## Regenerar los Paquetes

Si necesitas regenerar los paquetes, usa el script de empaquetamiento:

```bash
# Opción 1: Generar ambos (DEB y RPM)
./package-linux.sh --type all

# Opción 2: Solo DEB
./package-linux.sh --type deb

# Opción 3: Solo RPM
./package-linux.sh --type rpm

# Opción 4: App-Image (ejecutable sin instalar)
./package-linux.sh --type app-image

# Opción 5: Saltarse compilación (reutiliza artefactos)
./package-linux.sh --type all --skip-build
```

## Contenido de los Paquetes

Cada paquete incluye:

- **Runtime Java 17 optimizado** (via jlink)
- **Todas las dependencias** (GSON, JavaFX, etc.)
- **Aplicación compilada** (dungeon-crawler-patterns-1.0-SNAPSHOT.jar)
- **Recursos** (UI HTML/CSS/JS, archivos de configuración)
- **Scripts de inicio** (integrados en el PATH)
- **Atajos de menú** (Games category)

## Ubicación de Instalación

Una vez instalado el paquete:

- **Ejecutable**: `/opt/dungeon-crawler-patterns/bin/dungeon-crawler-patterns`
- **Datos**: `~/.local/share/dungeon-crawler-patterns/` (guardos del juego)
- **Menú**: Categoría "Games" en el menú de aplicaciones

## Notas Importantes

1. **Primer lanzamiento**: La primera ejecución puede tomar unos segundos mientras se initializa JavaFX.
2. **Permisos**: La instalación requiere `sudo` para escribir en `/opt/`
3. **Desinstalación**:
   - **DEB**: `sudo apt remove dungeon-crawler-patterns`
   - **RPM**: `sudo dnf remove dungeon-crawler-patterns`

## Generación de Paquetes Técnica

El proceso de empaquetamiento realiza automáticamente:

1. **Compilación Maven** - Compila 160 clases Java
2. **Copia de dependencias** - Extrae JARs runtime
3. **Creación de runtime** - `jlink` genera JRE mínimo (solo módulos necesarios)
4. **Empaquetamiento nativo** - `jpackage` crea instaladores del sistema
5. **Integración del sistema** - Menús, atajos, PATH

---

**Generado**: 22 de abril de 2026  
**Versión**: 1.0-SNAPSHOT  
**Estado**: ✅ Listo para distribución y producción
