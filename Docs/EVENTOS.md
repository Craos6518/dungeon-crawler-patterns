# Sistema de Eventos y Notificaciones

## 1. Arquitectura del Sistema (Observer)
El sistema de eventos utiliza el patrón **Observer** para permitir la comunicación desacoplada entre los componentes del juego.

### Componentes Clave
- **EventManager**: Singleton que gestiona la suscripción y notificación de suscriptores.
- **GameEvent**: Objeto que transporta información del evento (Tipo + Diccionario de datos).
- **EventType**: Enumeración de todos los sucesos posibles en el sistema.
- **GameObserver**: Interfaz que deben implementar los suscriptores (`CombatLogger`, `StatisticsTracker`).

## 2. Contrato de Referencia de Eventos
Para garantizar la consistencia, los eventos deben seguir un contrato de datos:

| EventType | Claves Requeridas | Propósito |
|-----------|-------------------|-----------|
| `COMBATE_INICIADO` | `heroe`, `enemigo`, `vidaHeroe`, `vidaEnemigo` | Notificar inicio de combate. |
| `ATAQUE_REALIZADO` | `atacante`, `defensor`, `danio`, `vidaRestante` | Notificar resultado de un ataque. |
| `ACCION_REALIZADA` | `actor`, `accion`, `nombre` | Notificar una acción genérica. |
| `EFECTO_APLICADO` | `personaje`, `efecto`, `duracion` | Notificar aplicación de estado alterado. |
| `JUEGO_GUARDADO` | `tipo`, `archivo` | Notificar éxito de persistencia. |

## 3. Análisis de Inconsistencias
Se han detectado variaciones en el uso de eventos entre `IntegratedCombatEngine.java` e `InteractiveGame.java`:
- **Claves Variables**: Uso de `personaje` vs `actor` vs `usuario`.
- **Faltante de Datos**: Algunos eventos `ATAQUE_REALIZADO` carecen de `vidaRestante`.
- **Nulidad**: Riesgo de `null` en valores de `estrategia` o `descripcion`.

## 4. Plan de Hardening (Robusteza)
1. **Validación en AgregarDato**: Lanzar excepción si se intenta agregar un valor `null`.
2. **Unificación de Claves**: Usar siempre `actor` para quien ejecuta y `objetivo` para quien recibe (si aplica).
3. **Estandarización de Efectos**: Diferenciar claramente entre el evento de aplicación inicial y el evento de ejecución de daño por efecto.

## 5. Resumen de Eventos Críticos
El sistema maneja aproximadamente 19 tipos de eventos, de los cuales 13 son críticos para la trazabilidad y el logging del combate interactivo.
