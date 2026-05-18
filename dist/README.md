# Dungeon Crawler Patterns v2.0.0 - Ejecutables

**Fecha de compilación**: 18 de mayo de 2026  
**Versión**: 2.0.0  
**Java mínimo requerido**: Java 17

## Archivos incluidos

### 📦 Ejecutables

| Archivo | Plataforma | Tipo | Descripción |
|---------|-----------|------|-------------|
| `dungeon-crawler-patterns-2.0.0.jar` | Multiplataforma | JAR | Ejecutable portable con todas las dependencias |
| `dungeon-crawler-patterns_2.0.0-1_amd64.deb` | Linux/Debian | DEB | Instalador para Debian/Ubuntu/Linux Mint |
| `dungeon-crawler-patterns-2.0.0-1.x86_64.rpm` | Linux/RedHat | RPM | Instalador para Fedora/RHEL/CentOS |

### 📄 Scripts de lanzamiento

| Archivo | Plataforma | Descripción |
|---------|-----------|-------------|
| `play.sh` | Linux/macOS | Script bash para ejecutar el JAR |
| `play.bat` | Windows | Script batch para ejecutar el JAR |

---

## 🚀 Cómo ejecutar

### Opción 1: Usando el JAR directamente (Multiplataforma)

#### Linux/macOS:
```bash
chmod +x play.sh
./play.sh
```

O directamente:
```bash
java -jar dungeon-crawler-patterns-2.0.0.jar
```

#### Windows:
```cmd
play.bat
```

O directamente en PowerShell/CMD:
```cmd
java -jar dungeon-crawler-patterns-2.0.0.jar
```

### Opción 2: Instalación en Linux

#### Debian/Ubuntu/Linux Mint:
```bash
sudo dpkg -i dungeon-crawler-patterns_2.0.0-1_amd64.deb
dungeon-crawler-patterns
```

#### Fedora/RHEL/CentOS:
```bash
sudo rpm -i dungeon-crawler-patterns-2.0.0-1.x86_64.rpm
dungeon-crawler-patterns
```

---

## ⚙️ Requisitos previos

1. **Java 17 o superior instalado**
   - Linux: `sudo apt install openjdk-17-jre-headless` (Debian/Ubuntu)
   - Windows: Descargar desde [Adoptium](https://adoptium.net/)
   - macOS: `brew install openjdk@17`

2. **Verificar la instalación**:
   ```bash
   java -version
   ```
   Debe mostrar una versión de Java 17 o superior.

---

## 📋 Contenido del JAR

El JAR incluye:
- ✅ 11 Patrones de Diseño implementados
- ✅ 160 clases de dominio y aplicación
- ✅ 48 clases de test
- ✅ Sistema de combate por turnos
- ✅ Interfaz web interactiva
- ✅ Todas las dependencias necesarias (GSON, JavaFX)

---

## 🎮 Características principales (v2.0.0)

- **Motor de combate mejorado** con sistema de efectos
- **Sistema de inventario jerárquico** basado en Composite
- **11 patrones de diseño** integrados:
  - Factory Method, Builder, Abstract Factory (Creacionales)
  - Composite, Decorator, Facade (Estructurales)
  - Command, Strategy, Observer, Memento, State (Comportamiento)

---

## 🐛 Resolución de problemas

### Error: "Java not found"
- Instala Java 17: https://adoptium.net/
- O configura `JAVA_HOME` en tu sistema

### El juego no inicia en Linux
- Verifica permisos: `chmod +x play.sh`
- Ejecuta desde terminal para ver mensajes de error

### En Windows con PowerShell
Si `play.bat` no funciona, ejecuta directamente en PowerShell:
```powershell
java -jar .\dungeon-crawler-patterns-2.0.0.jar
```

---

## 📚 Documentación adicional

- **Especificación de requisitos**: Ver `docs/01-product/`
- **Arquitectura**: Ver `docs/02-architecture/`
- **Patrones implementados**: Ver `docs/03-patterns/`
- **Estrategia de testing**: Ver `docs/04-testing/`

---

**Desarrollado como proyecto académico de Patrones de Diseño**  
Universidad Tecnológica de Pereira
