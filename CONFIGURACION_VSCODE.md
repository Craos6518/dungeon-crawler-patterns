# Configuración de Visual Studio Code - Java 17

## ✅ Configuración Completada

Tu proyecto está ahora **completamente configurado** para usar Java 17 en Visual Studio Code.

---

## 📋 Resumen de Cambios

### Archivos Creados

1. **`.vscode/settings.json`**
   - Configura Java 17 como runtime por defecto
   - Configura JAVA_HOME para Maven
   - Habilita auto-importaciones y compilación automática

2. **`.vscode/launch.json`**
   - Configuración para ejecutar `game.Main`
   - Configuración para ejecutar `game.demo.PatronesEstructuralesDemo`
   - Configuración para debug de tests

3. **`.vscode/tasks.json`**
   - Tarea de compilación (Ctrl+Shift+B)
   - Tarea de tests
   - Tareas de Maven (clean, package, etc.)
   - Tareas de ejecución directa

4. **`.vscode/extensions.json`**
   - Lista de extensiones recomendadas
   - VSCode te sugerirá instalarlas automáticamente

5. **`.vscode/README.md`**
   - Guía completa de uso
   - Atajos de teclado
   - Solución de problemas

6. **`verificar-config-vscode.sh`**
   - Script de verificación automática
   - Prueba compilación y tests

---

## 🚀 Cómo Empezar

### 1. Abre el Proyecto en VSCode

```bash
cd "/run/media/craos6518/Externo/back/Proyecto Patrones de diseño/dungeon-crawler-patterns"
code .
```

### 2. Instala las Extensiones

Cuando abras VSCode, verás una notificación sugiriendo instalar las extensiones recomendadas.

**Haz clic en "Install All"** (Instalar todas)

Las extensiones principales son:
- ✅ **Java Extension Pack** - Herramientas esenciales de Java
- ✅ **Maven for Java** - Integración de Maven
- ✅ **Test Runner for Java** - Ejecutar tests con un clic

### 3. Recarga VSCode

Después de instalar las extensiones:
- Presiona `Ctrl+Shift+P`
- Escribe "Reload Window"
- Presiona Enter

### 4. Verifica la Configuración

En la terminal de VSCode (Ctrl+Shift+ñ), ejecuta:

```bash
./verificar-config-vscode.sh
```

Deberías ver:
```
✓ Java 17 Temurin JDK instalado
✓ Maven usando Java 17
✓ Compilación exitosa
✓ Tests ejecutados correctamente
```

---

## 🎯 Funcionalidades Disponibles

### Compilar el Proyecto
- **Atajo**: `Ctrl+Shift+B`
- **Menú**: Terminal → Run Build Task

### Ejecutar el Programa
1. Presiona `F5` o
2. Ve a "Run and Debug" (Ctrl+Shift+D)
3. Selecciona "Ejecutar Main"
4. Presiona el botón verde ▶

### Ejecutar Tests
1. Abre la pestaña **"Testing"** en la barra lateral (icono de probeta 🧪)
2. Verás todos tus tests organizados:
   ```
   ├─ unit
   │  ├─ creational (15 tests)
   │  ├─ structural (25 tests)
   │  ├─ combat (2 tests)
   │  └─ domain (1 test)
   └─ integration (1 test)
   ```
3. Haz clic en ▶ para ejecutar cualquier test

### Debug
1. Abre un archivo `.java`
2. Haz clic en el margen izquierdo para agregar breakpoints (⭕)
3. Presiona `F5`
4. El programa se detendrá en los breakpoints

### Auto-completado
- Escribe el inicio de una clase y presiona `Ctrl+Space`
- VSCode sugerirá clases y auto-importará

### Refactoring
- Haz clic derecho en un símbolo → "Refactor"
- O presiona `Ctrl+Shift+R`

---

## 📊 Explorador de Maven

VSCode incluye un explorador visual de Maven:

1. Haz clic en el icono de **"M"** en la barra lateral
2. Verás el proyecto con todas las fases:
   - clean
   - validate
   - compile
   - test
   - package
   - verify
   - install

Puedes hacer clic derecho en cualquier fase y ejecutarla.

---

## 🧪 Testing Mejorado

### Visualización de Tests
Los tests se muestran con iconos:
- ✅ Test pasó (verde)
- ❌ Test falló (rojo)
- ⏭️ Test omitido (amarillo)
- ⏱️ Test en ejecución

### Ejecutar Tests Específicos
- **Un test**: Haz clic en ▶ junto al test
- **Una clase**: Haz clic en ▶ junto al archivo
- **Un paquete**: Haz clic en ▶ junto al paquete
- **Todos**: Haz clic en ▶▶ en la parte superior

### Ver Resultados
Después de ejecutar un test:
- Haz clic en el test para ver el output
- Haz clic en "Show Output" para ver logs detallados
- Los errores se muestran con stack traces

---

## 🔧 Configuración Técnica

### Java Runtime Detection

VSCode ahora usa automáticamente:
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/usr/lib/jvm/java-17-temurin-jdk",
      "default": true
    }
  ]
}
```

### Maven Integration

Todas las tareas de Maven usan:
```bash
JAVA_HOME=/usr/lib/jvm/java-17-temurin-jdk
```

### Test Configuration

JUnit 5 está configurado con:
- Assertions habilitadas (`-ea`)
- JAVA_HOME configurado
- Working directory correcto

---

## 📚 Atajos de Teclado Útiles

| Acción | Atajo |
|--------|-------|
| **Compilar** | `Ctrl+Shift+B` |
| **Ejecutar/Debug** | `F5` |
| **Detener Debug** | `Shift+F5` |
| **Paleta de comandos** | `Ctrl+Shift+P` |
| **Terminal** | `Ctrl+Shift+ñ` |
| **Explorador de archivos** | `Ctrl+Shift+E` |
| **Buscar** | `Ctrl+Shift+F` |
| **Git** | `Ctrl+Shift+G` |
| **Testing** | Click en icono 🧪 |
| **Maven** | Click en icono M |
| **Go to Definition** | `F12` |
| **Find References** | `Shift+F12` |
| **Rename Symbol** | `F2` |
| **Format Document** | `Shift+Alt+F` |

---

## ⚠️ Solución de Problemas

### Problema: VSCode no detecta Java 17

**Solución**:
1. `Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace"
2. `Ctrl+Shift+P` → "Reload Window"
3. Espera a que VSCode reindexe el proyecto

### Problema: Los tests no aparecen

**Solución**:
1. Asegúrate de que las extensiones de Java estén instaladas
2. Compila el proyecto: `Ctrl+Shift+B`
3. Recarga la ventana: `Ctrl+Shift+P` → "Reload Window"

### Problema: "JAVA_HOME not set"

**Solución**:
1. Cierra VSCode completamente
2. Abre VSCode de nuevo desde la terminal:
   ```bash
   cd "/run/media/craos6518/Externo/back/Proyecto Patrones de diseño/dungeon-crawler-patterns"
   code .
   ```
3. La configuración se aplicará automáticamente

### Problema: Maven usa Java incorrecto

**Solución**:
- La configuración de tasks.json incluye `JAVA_HOME`
- Esto se aplica automáticamente a todas las tareas de Maven en VSCode
- Si ejecutas Maven desde fuera de VSCode, debes configurar JAVA_HOME manualmente

---

## 🎓 Recursos Adicionales

### Documentación
- [`.vscode/README.md`](.vscode/README.md) - Guía completa de uso
- [Java en VSCode](https://code.visualstudio.com/docs/languages/java)
- [Testing en VSCode](https://code.visualstudio.com/docs/java/java-testing)

### Scripts
- `./verificar-config-vscode.sh` - Verifica que todo funcione

### Ayuda
Si tienes problemas:
1. Lee [`.vscode/README.md`](.vscode/README.md)
2. Ejecuta `./verificar-config-vscode.sh`
3. Revisa los logs de VSCode: View → Output → Java

---

## ✨ Conclusión

Tu proyecto ahora tiene:

- ✅ **Java 17** configurado automáticamente
- ✅ **Maven** integrado con Java 17
- ✅ **Tests** ejecutables con un clic
- ✅ **Debug** completamente funcional
- ✅ **Auto-completado** e Intellisense
- ✅ **Refactoring** automático
- ✅ **Formateo** de código
- ✅ **Git** integrado

**¡Todo listo para programar!** 🚀

Abre VSCode con:
```bash
code .
```

Y empieza a construir patrones de diseño. 🎮
