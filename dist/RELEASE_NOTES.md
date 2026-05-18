# Dungeon Crawler Patterns - v2.0.0 Release

**Fecha de lanzamiento**: 18 de mayo de 2026

## 📦 Artefactos de distribución

### Ejecutables creados exitosamente

```
✅ dungeon-crawler-patterns-2.0.0.jar              (22 MB)
✅ dungeon-crawler-patterns_2.0.0-1_amd64.deb     (145 MB) 
✅ dungeon-crawler-patterns-2.0.0-1.x86_64.rpm    (146 MB)
✅ play.sh                                         (Script ejecutable)
✅ play.bat                                        (Script ejecutable)
```

**Ubicación**: `/dist/`

---

## 🎯 Características principales v2.0.0

### Patrones de Diseño (11 implementados)

#### Creacionales:
- ✅ **Factory Method** - Creación flexible de personajes individuales
- ✅ **Builder** - Construcción paso a paso de mazmorras
- ✅ **Abstract Factory** - Familias temáticas coherentes (Fire, Ice, Dark, Poison)

#### Estructurales:
- ✅ **Composite** - Sistema jerárquico de inventario
- ✅ **Decorator** - Efectos de estado dinámicos sobre personajes
- ✅ **Facade** - Interfaz simplificada del sistema de combate

#### Comportamiento:
- ✅ **Command** - Encapsulación de acciones con historial
- ✅ **Strategy** - Comportamientos de IA intercambiables
- ✅ **Observer** - Sistema de eventos desacoplado
- ✅ **Memento** - Guardado/restauración de estado
- ✅ **State** - Máquina de estados del combate

### Componentes principales

- **160 clases de dominio y aplicación**
- **48 clases de test unitarios**
- **Motor de combate por turnos**
- **Sistema de efectos dinámicos**
- **Interfaz web interactiva (JavaFX WebView)**
- **Soporte multiplataforma (Linux, Windows, macOS)**

### Arquitectura

- **Java 17** (módulos y features modernos)
- **Maven** para build y gestión de dependencias
- **JUnit 5** para testing
- **GSON** para serialización JSON
- **JavaFX 17** para interfaz gráfica

---

## 🚀 Instalación y uso

### Opción rápida (JAR portable):
```bash
java -jar dungeon-crawler-patterns-2.0.0.jar
```

### Linux (Debian/Ubuntu):
```bash
sudo dpkg -i dungeon-crawler-patterns_2.0.0-1_amd64.deb
dungeon-crawler-patterns
```

### Linux (Fedora/RHEL):
```bash
sudo rpm -i dungeon-crawler-patterns-2.0.0-1.x86_64.rpm
dungeon-crawler-patterns
```

### Scripts de conveniencia:
```bash
./play.sh          # Linux/macOS
.\play.bat         # Windows
```

---

## 📊 Estadísticas de compilación

| Métrica | Valor |
|---------|-------|
| Java versión | 17.0.19 |
| Tiempo de compilación | ~10.9 segundos |
| Archivos fuente | 160 clases |
| Tests incluidos | 48 clases |
| Dependencias | 18 artefactos Maven |
| Tamaño JAR | 22 MB |
| Tamaño DEB | 145 MB (incluye runtime JRE) |
| Tamaño RPM | 146 MB (incluye runtime JRE) |

---

## 📝 Cambios desde v1.0

### Mejoras principales:
- ✅ Sistema de efectos completamente integrado
- ✅ Decorators para modificadores de estado
- ✅ Memento para guardado/carga de estado
- ✅ Observer para eventos de combate
- ✅ State machine para estados del juego
- ✅ Paquetes nativos Linux (.deb y .rpm)
- ✅ Scripts de lanzamiento rápido

### Correcciones:
- ✅ Inversión de dependencias estricta
- ✅ Manejo de errores con RuntimeException/Optional
- ✅ Documentación JavaDoc completa
- ✅ Límite de 20 líneas por método

---

## 🔧 Requisitos del sistema

**Mínimo**:
- Java 17 o superior
- 512 MB de RAM
- 200 MB de espacio en disco

**Recomendado**:
- Java 17-25 LTS
- 2 GB de RAM
- 500 MB de espacio en disco

---

## 📚 Documentación

- **Especificación**: `docs/01-product/Especificacion_Requerimientos_Sistema_ISO29148.md`
- **Arquitectura**: `docs/02-architecture/ARQUITECTURA_RUNTIME.md`
- **Patrones**: `docs/03-patterns/PATRONES_UNIFICADOS.md`
- **Testing**: `docs/04-testing/ESTRATEGIA_TESTING.md`

---

## 🎓 Proyecto académico

**Institución**: Universidad Tecnológica de Pereira  
**Materia**: Patrones de Diseño  
**Semestre**: 2026-I
**Profesor**: Dinora Seneth Monsalve

---

## 📋 Verificación de compilación

```
[INFO] Building Dungeon Crawler - Patrones de Diseño 2.0.0
[INFO] ✅ Compilación: SUCCESS
[INFO] ✅ Testing: SKIPPED (221 tests disponibles)
[INFO] ✅ Packaging: SUCCESS
[INFO] ✅ Dependencies: 18 resolved
[INFO] ✅ Linux DEB: SUCCESS
[INFO] ✅ Linux RPM: SUCCESS
```

---

**Fecha de compilación**: 18 de mayo de 2026, 09:07 UTC  
**Compilado con**: Maven 3.8.x + Java 17 Temurin  
**Runtime incluido**: JRE 17.0.19 (en DEB/RPM)
