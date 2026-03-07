/**
 * State Pattern - Sistema de estados del juego
 * 
 * Implementa el patrón State para gestionar los diferentes estados del juego
 * sin usar condicionales complejos.
 * 
 * Componentes:
 * - {@link game.state.game.GameState} - Interfaz State
 * - {@link game.state.game.GameStateContext} - Context que gestiona transiciones
 * - {@link game.state.game.MenuState} - Estado del menú principal
 * - {@link game.state.game.ExplorationState} - Estado de exploración
 * - {@link game.state.game.CombatState} - Estado de combate
 * - {@link game.state.game.InventoryState} - Estado de inventario
 * - {@link game.state.game.GameOverState} - Estado de fin de juego
 * 
 * Beneficios:
 * - Elimina grandes bloques if/switch para gestionar estados
 * - Cada estado encapsula su propio comportamiento
 * - Facilita agregar nuevos estados sin modificar código existente
 * - Las transiciones de estado son explícitas y controladas
 * - Cumple con Single Responsibility Principle
 * 
 * Ejemplo de uso:
 * <pre>
 * // Crear contexto con estado inicial
 * GameStateContext game = new GameStateContext(new MenuState(game));
 * 
 * // El contexto delega al estado actual
 * game.render();
 * game.procesarEntrada("1");
 * game.actualizar();
 * 
 * // Los estados pueden cambiar internamente
 * // por ejemplo: MenuState -> ExplorationState -> CombatState
 * </pre>
 * 
 * Diagrama de transiciones:
 * <pre>
 * MenuState
 *   ↓
 * ExplorationState ←→ InventoryState
 *   ↓
 * CombatState
 *   ↓
 * GameOverState → MenuState
 * </pre>
 * 
 * @see game.state.game.GameState
 * @see game.state.game.GameStateContext
 */
package game.state.game;
