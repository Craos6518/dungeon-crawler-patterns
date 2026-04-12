# Configuración de Visual Studio Code para Java 17

## ✅ Configuración Completada

Este proyecto está configurado para usar **Java 17 (Temurin JDK)** automáticamente en VSCode.

---

## 📁 Archivos de Configuración

### `settings.json`
Configuración principal del proyecto:
- **Java Runtime**: Java 17 Temurin JDK
- **JAVA_HOME**: `/usr/lib/jvm/java-17-temurin-jdk`
- **Maven**: Configurado para usar Java 17
- **Tests**: JUnit 5 con Java 17
- **Auto-importaciones**: Habilitadas

### `launch.json`
Configuraciones de ejecución y debug:
1. **Ejecutar Main** - Ejecuta `game.Main`
2. **Ejecutar Demo Patrones Estructurales** - Ejecuta la demo
3. **Debug Tests** - Debug de tests unitarios

### `tasks.json`
Tareas de Maven predefinidas:
- **Maven: Compilar** (Ctrl+Shift+B)
- **Maven: Ejecutar Tests**
- **Maven: Limpiar**
- **Maven: Package**
- **Ejecutar Main**
- **Ejecutar Demo Patrones Estructurales**

### `extensions.json`
Extensiones recomendadas para el proyecto.

---

## 🚀 Cómo Usar

### 1. Instalar Extensiones Recomendadas

Cuando abras el proyecto, VSCode te sugerirá instalar las extensiones recomendadas. **Instálalas todas**.

Alternativamente, ve a la pestaña de extensiones y busca:
- **Java Extension Pack** (vscjava.vscode-java-pack)
- **Maven for Java** (vscjava.vscode-maven)
- **PlantUML** (jebbs.plantuml) para visualizar/editar fuentes de diagramas

### 2. Recargar VSCode

Después de instalar las extensiones:
1. Presiona `Ctrl+Shift+P`
2. Escribe "Reload Window"
3. Presiona Enter

### 3. Ejecutar el Proyecto

#### Opción A: Desde el menú Run
1. Ve a la pestaña **Run and Debug** (Ctrl+Shift+D)
2. Selecciona "Ejecutar Main" o "Ejecutar Demo Patrones Estructurales"
3. Presiona F5 o el botón verde de play

#### Opción B: Desde tasks
1. Presiona `Ctrl+Shift+P`
2. Escribe "Tasks: Run Task"
3. Selecciona la tarea que quieras ejecutar

#### Opción C: Desde el código
1. Abre `Main.java` o `PatronesEstructuralesDemo.java`
2. Haz clic derecho en el editor
3. Selecciona "Run Java"

### 4. Ejecutar Tests

#### Opción A: Desde el explorador de tests
1. Abre la pestaña **Testing** en la barra lateral
2. Verás todos los tests organizados por paquetes
3. Haz clic en el botón de play junto al test que quieras ejecutar

#### Opción B: Desde el código
1. Abre cualquier archivo de test
2. Verás un botón "Run Test" encima de cada método `@Test`
3. Haz clic para ejecutar ese test específico

#### Opción C: Desde tasks
1. Presiona `Ctrl+Shift+B` → te compilará el proyecto
2. Presiona `Ctrl+Shift+P` → "Tasks: Run Task" → "Maven: Ejecutar Tests"

### 5. Compilar el Proyecto

- **Atajo rápido**: Presiona `Ctrl+Shift+B`
- **Manualmente**: `Ctrl+Shift+P` → "Tasks: Run Build Task"

---

## 🔧 Comandos Útiles

| Acción | Atajo |
|--------|-------|
| Compilar (Build) | `Ctrl+Shift+B` |
| Run/Debug | `F5` |
| Detener Debug | `Shift+F5` |
| Abrir Paleta de Comandos | `Ctrl+Shift+P` |
| Ejecutar Task | `Ctrl+Shift+P` → "Tasks: Run Task" |
| Ver Tests | Icono de probeta en barra lateral |

---

## 🐛 Debug

### Debuggear la Aplicación
1. Abre `Main.java` o el archivo que quieras debuggear
2. Haz clic en el margen izquierdo para agregar breakpoints (puntos rojos)
3. Presiona `F5` o ve a Run → "Ejecutar Main"
4. El programa se detendrá en los breakpoints

### Debuggear Tests
1. Abre el archivo de test
2. Agrega breakpoints
3. Haz clic derecho en el test → "Debug Test"

---

## 🧪 Extensiones de Test

VSCode mostrará:
- ✅ Tests que pasaron (verde)
- ❌ Tests que fallaron (rojo)
- ⏭️ Tests omitidos (amarillo)

Puedes:
- Ejecutar todos los tests de una clase
- Ejecutar un test individual
- Debuggear tests
- Ver el output de cada test

---

## 📊 Maven en VSCode

### Explorador de Maven
1. Abre la vista de Maven (icono de "M" en la barra lateral)
2. Verás el proyecto con todas las fases de Maven:
   - clean
   - compile
   - test
   - package
   - install

### Ejecutar Goals de Maven
1. Abre el explorador de Maven
2. Despliega "Lifecycle"
3. Haz clic derecho en cualquier goal → "Run"

---

## ⚠️ Solución de Problemas

### VSCode no detecta Java 17

1. **Reinicia el Language Server**:
   - `Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace"
   - Reinicia VSCode

2. **Verifica la configuración**:
   - Abre `settings.json`
   - Verifica que `java.home` apunte a `/usr/lib/jvm/java-17-temurin-jdk`

3. **Recarga el proyecto**:
   - `Ctrl+Shift+P` → "Java: Force Java Compilation"
   - `Ctrl+Shift+P` → "Reload Window"

### Los tests no se ejecutan

1. **Limpia y recompila**:
   ```bash
   Ctrl+Shift+P → "Tasks: Run Task" → "Maven: Limpiar"
   Ctrl+Shift+B (compilar)
   ```

2. **Verifica JUnit**:
   - Asegúrate de que las extensiones de Java estén instaladas
   - Recarga la ventana

### Maven usa una versión incorrecta de Java

1. **Verifica JAVA_HOME**:
   - Abre una terminal en VSCode
   - Ejecuta: `echo $JAVA_HOME`
   - Debería mostrar: `/usr/lib/jvm/java-17-temurin-jdk`

2. **Si no funciona**:
   - Cierra VSCode completamente
   - Ábrelo de nuevo
   - La configuración se aplicará automáticamente

---

## 🎯 Verificación de la Configuración

Para verificar que todo está correctamente configurado:

1. **Abre una terminal en VSCode** (Ctrl+Shift+ñ)

2. **Ejecuta**:
   ```bash
   echo $JAVA_HOME
   java -version
   mvn -version
   ```

3. **Deberías ver**:
   ```
   JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
   openjdk version "17.x.x"
   Apache Maven 3.x.x
   ```

4. **Compila el proyecto**:
   ```bash
   mvn clean compile
   ```

5. **Ejecuta los tests**:
   ```bash
   mvn test
   ```

Si todo funciona sin errores, ¡la configuración está correcta! ✅

---

## 📚 Estructura del Proyecto

```
dungeon-crawler-patterns/
├── .vscode/
│   ├── settings.json      ← Configuración de Java 17
│   ├── launch.json        ← Configuraciones de ejecución
│   ├── tasks.json         ← Tareas de Maven
│   ├── extensions.json    ← Extensiones recomendadas
│   └── README.md          ← Este archivo
├── src/
│   ├── main/java/         ← Código fuente
│   └── test/java/         ← Tests
├── target/                ← Archivos compilados
├── pom.xml               ← Configuración de Maven
└── README.md             ← Documentación del proyecto
```

---

## 🎓 Consejos

1. **Usa los atajos de teclado** - son mucho más rápidos
2. **Instala todas las extensiones recomendadas** - mejoran la experiencia
3. **Usa el explorador de tests** - es más visual que la terminal
4. **Aprovecha el debug** - te ahorrará mucho tiempo
5. **Compila frecuentemente** - `Ctrl+Shift+B` después de cambios importantes

---

## ✨ Características Habilitadas

- ✅ Java 17 detectado automáticamente
- ✅ Auto-importaciones de clases
- ✅ Intellisense y autocompletado
- ✅ Refactoring automático
- ✅ Debug integrado
- ✅ Ejecución de tests con un clic
- ✅ Maven integrado
- ✅ Detección de errores en tiempo real
- ✅ Formato de código automático

---

¡Listo para programar! 🚀
