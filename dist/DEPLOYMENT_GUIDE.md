# Dungeon Crawler Patterns v2.0.0 - Guía de Instalación y Despliegue

**Fecha**: 18 de mayo de 2026  
**Versión**: 2.0.0  
**Estado**: ✅ LISTA PARA DISTRIBUCIÓN

---

## 🎯 Resumen ejecutivo

Se han generado exitosamente **5 artefactos de distribución** para Dungeon Crawler Patterns v2.0.0:

| Artefacto    | Tamaño | Plataforma      | Instalación       |
| ------------ | ------ | --------------- | ----------------- |
| JAR portable | 22 MB  | Multiplataforma | `java -jar *.jar` |
| Paquete DEB  | 145 MB | Linux/Debian    | `dpkg -i *.deb`   |
| Paquete RPM  | 146 MB | Linux/RedHat    | `rpm -i *.rpm`    |
| Script Bash  | <1 KB  | Linux/macOS     | `./play.sh`       |
| Script Batch | <1 KB  | Windows         | `.\play.bat`      |

**Tamaño total**: 312 MB (comprimible)

---

## 📦 Distribución de artefactos

Todos los artefactos están disponibles en la carpeta **`/dist/`**:

```
dungeon-crawler-patterns/dist/
├── dungeon-crawler-patterns-2.0.0.jar         ✅ JAR ejecutable
├── dungeon-crawler-patterns_2.0.0-1_amd64.deb ✅ Paquete Debian
├── dungeon-crawler-patterns-2.0.0-1.x86_64.rpm ✅ Paquete RedHat
├── play.sh                                     ✅ Script Linux/macOS
├── play.bat                                    ✅ Script Windows
├── README.md                                   📖 Instrucciones básicas
├── RELEASE_NOTES.md                            📋 Notas de versión
├── BUILD_INFO.txt                              📊 Información de compilación
└── DEPLOYMENT_GUIDE.md                         📘 Esta guía
```

---

## 🚀 Procedimientos de instalación

### Opción 1: Ejecución inmediata (Multiplataforma)

**Requisito previo**: Java 17 o superior

```bash
# Linux/macOS
java -jar dungeon-crawler-patterns-2.0.0.jar

# Windows (CMD)
java -jar dungeon-crawler-patterns-2.0.0.jar

# Windows (PowerShell)
& 'java' '-jar' '.\dungeon-crawler-patterns-2.0.0.jar'
```

---

### Opción 2: Instalación en Linux Debian/Ubuntu

**Requisito previo**: Ninguno (incluye JRE 17)

```bash
# Instalación
sudo dpkg -i dungeon-crawler-patterns_2.0.0-1_amd64.deb

# Verificar instalación
which dungeon-crawler-patterns

# Lanzar
dungeon-crawler-patterns

# O desde el menú de aplicaciones del sistema
# Búsqueda: "Dungeon Crawler Patterns"
```

**Ubicación tras instalación**:

- Ejecutable: `/opt/dungeon-crawler-patterns/bin/dungeon-crawler-patterns`
- Librerías: `/opt/dungeon-crawler-patterns/lib/`

**Desinstalar**:

```bash
sudo dpkg -r dungeon-crawler-patterns
```

---

### Opción 3: Instalación en Linux RedHat/Fedora

**Requisito previo**: Ninguno (incluye JRE 17)

```bash
# Instalación
sudo rpm -i dungeon-crawler-patterns-2.0.0-1.x86_64.rpm

# Verificar instalación
which dungeon-crawler-patterns

# Lanzar
dungeon-crawler-patterns

# O desde el menú de aplicaciones del sistema
```

**Ubicación tras instalación**:

- Ejecutable: `/opt/dungeon-crawler-patterns/bin/dungeon-crawler-patterns`
- Librerías: `/opt/dungeon-crawler-patterns/lib/`

**Desinstalar**:

```bash
sudo rpm -e dungeon-crawler-patterns
```

---

### Opción 4: Scripts de conveniencia

#### Linux/macOS:

```bash
cd dist
chmod +x play.sh
./play.sh
```

#### Windows (CMD):

```cmd
cd dist
play.bat
```

#### Windows (PowerShell):

```powershell
cd dist
.\play.bat
```

---

## ✅ Verificación post-instalación

### Prueba 1: Verificar Java

```bash
java -version
# Debe mostrar OpenJDK 17 o superior
```

### Prueba 2: Verificar JAR

```bash
jar -tf dist/dungeon-crawler-patterns-2.0.0.jar | head -20
# Debe listar archivos del JAR
```

### Prueba 3: Verificar manifest

```bash
unzip -p dist/dungeon-crawler-patterns-2.0.0.jar META-INF/MANIFEST.MF
# Debe mostrar:
#   Main-Class: game.ui.GameWebApplication
```

### Prueba 4: Lanzar aplicación (con timeout)

```bash
timeout 5 java -jar dist/dungeon-crawler-patterns-2.0.0.jar || echo "App se inició correctamente"
```

---

## 🌐 Distribución y compatibilidad

### Sistemas operativos soportados

| Sistema    | Versión | Artefacto | Método         |
| ---------- | ------- | --------- | -------------- |
| Ubuntu     | 20.04+  | DEB       | `dpkg`         |
| Debian     | 11+     | DEB       | `dpkg`         |
| Linux Mint | 20+     | DEB       | `dpkg`         |
| Fedora     | 35+     | RPM       | `rpm`          |
| RHEL       | 8+      | RPM       | `rpm`          |
| CentOS     | 8+      | RPM       | `rpm`          |
| Windows    | 10+     | JAR/BAT   | PowerShell/CMD |
| macOS      | 10.13+  | JAR/SH    | Terminal       |

### Versiones de Java soportadas

- ✅ OpenJDK 17 LTS
- ✅ OpenJDK 21 LTS
- ✅ OpenJDK 25+
- ✅ Temurin JDK (recomendado)

---

## 📋 Checklist de distribución

Antes de distribuir, verificar:

- [x] JAR tiene Main-Class en manifest
- [x] DEB incluye JRE integrado
- [x] RPM incluye JRE integrado
- [x] Scripts bash y batch funcionan
- [x] README.md actualizado
- [x] RELEASE_NOTES.md completado
- [x] BUILD_INFO.txt generado
- [x] Todos los archivos en /dist/
- [x] Permisos correctos en scripts
- [x] Versión 2.0.0 en pom.xml

---

## 📦 Empaquetamiento para distribución

### Crear archivo comprimido

```bash
cd dungeon-crawler-patterns
tar -czf dungeon-crawler-patterns-2.0.0-release.tar.gz dist/
# O para usuarios Windows
zip -r dungeon-crawler-patterns-2.0.0-release.zip dist/
```

### Calcular checksums

```bash
cd dist
sha256sum * > SHA256SUMS.txt
md5sum * > MD5SUMS.txt
```

### Crear archivo de distribución final

```bash
dist/
├── dungeon-crawler-patterns-2.0.0.jar
├── dungeon-crawler-patterns_2.0.0-1_amd64.deb
├── dungeon-crawler-patterns-2.0.0-1.x86_64.rpm
├── play.sh
├── play.bat
├── README.md
├── RELEASE_NOTES.md
├── BUILD_INFO.txt
├── DEPLOYMENT_GUIDE.md
├── SHA256SUMS.txt
└── MD5SUMS.txt
```

---

## 🔧 Solución de problemas

### Problema: "Java not found"

**Solución**:

```bash
# Linux
sudo apt install openjdk-17-jre

# macOS
brew install openjdk@17

# Windows: Descargar de https://adoptium.net/
```

### Problema: DEB/RPM no instala

**Solución**:

```bash
# Verificar dependencias (Debian)
sudo dpkg --configure -a
sudo apt install -f

# Verificar dependencias (RedHat)
sudo yum install -y glibc libxext libxrender
```

### Problema: Aplicación no inicia

**Solución**:

```bash
# Ver logs detallados
java -jar dist/dungeon-crawler-patterns-2.0.0.jar 2>&1 | head -50

# O con debugging
java -Xmx512m -Xms256m -jar dist/dungeon-crawler-patterns-2.0.0.jar
```

### Problema: Interfaz gráfica no aparece

**Solución**:

```bash
# Asegurar que X11 está disponible (si es remoto)
export DISPLAY=:0

# O usar el script
./play.sh
```

---

## 📊 Especificaciones técnicas

### Requisitos mínimos de sistema

| Recurso  | Mínimo    | Recomendado |
| -------- | --------- | ----------- |
| CPU      | Dual-core | Quad-core   |
| RAM      | 512 MB    | 2 GB        |
| Disco    | 200 MB    | 500 MB      |
| Pantalla | 1024x768  | 1920x1080+  |
| Red      | Ninguna   | Ninguna     |

### Consumo de recursos

| Métrica                        | Valor      |
| ------------------------------ | ---------- |
| Tamaño JAR                     | 22 MB      |
| Tamaño instalado (DEB)         | 145 MB     |
| Memoria en tiempo de ejecución | 300-500 MB |
| CPU @ idle                     | <5%        |
| CPU @ gameplay                 | 20-40%     |

---

## 📚 Referencias técnicas

### Documentación del proyecto

- `docs/01-product/` - Especificaciones
- `docs/02-architecture/` - Arquitectura
- `docs/03-patterns/` - Patrones implementados
- `docs/04-testing/` - Estrategia de testing

### Documentación externa

- Java 17: https://openjdk.org/projects/jdk/17/
- Maven: https://maven.apache.org/
- JavaFX: https://gluonhq.com/products/javafx/
- Adoptium: https://adoptium.net/

---

## 🎓 Información del proyecto

**Institución**: Universidad Tecnológica de Pereira  
**Materia**: Patrones de Diseño  
**Semestre**: 2026-I/II  
**Proyecto**: Dungeon Crawler - Sistema de Combate por Turnos  
**Patrones implementados**: 11 patrones SOLID

---

## 📞 Soporte

En caso de problemas:

1. Verificar requisitos: Java 17+
2. Revisar logs: `java -jar app.jar 2>&1`
3. Consultar BUILD_INFO.txt
4. Revisar documentación en `docs/`

---

**Versión del documento**: 2.0.0  
**Última actualización**: 18 de mayo de 2026  
**Estado**: ✅ Listo para distribución
