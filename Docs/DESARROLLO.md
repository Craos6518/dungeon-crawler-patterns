# Guía de Desarrollo y Testing

## 1. Configuración del Entorno (VSCode)
Se recomienda el uso de **Visual Studio Code** con el "Extension Pack for Java".

### Instalación Rápida
1. Clonar el repositorio.
2. Abrir la carpeta `dungeon-crawler-patterns`.
3. Ejecutar `mvn clean compile` para descargar dependencias e instalar el proyecto.
4. (Opcional) Ejecutar `./verificar-config-vscode.sh` para validar el entorno.

## 2. Compilación y Ejecución
El proyecto utiliza Maven 3.6+ y Java 17.

### Comandos Maven
- **Compilar**: `mvn compile`
- **Ejecutar Demo Integrada**: `mvn exec:java -Dexec.mainClass="game.demo.IntegracionCompletaDemo"`
- **Ejecutar Juego Interactivo**: `mvn exec:java -Dexec.mainClass="game.InteractiveGame"`
- **Ver Cobertura**: `mvn test jacoco:report`

## 3. Estrategia de Testing
El proyecto cuenta con más de **107 tests** para validar la arquitectura y los patrones.

### Categorías de Tests
- **Unitarios Creacionales**: Validan Factories, Builders y temas.
- **Unitarios Estructurales**: Validan el Composite (inventario) y Decorator (efectos).
- **Unitarios de Comportamiento**: Validan Command, Strategy, Observer, State y Memento.
- **Integración**: `CombatIntegrationTest` valida la colaboración de múltiples patrones en un flujo de combate real.

### Ejecución de Tests
```bash
# Todos los tests
mvn test

# Solo tests de patrones de comportamiento
mvn test -Dtest="game.unit.behavioral.*Test"
```

## 4. Guía de Pruebas de Comportamiento
Se pone especial énfasis en los tests de comportamiento para asegurar que las decisiones de IA (`Strategy`), la persistencia (`Memento`) y las transiciones de estado (`State`) funcionen según lo esperado académica y funcionalmente.
