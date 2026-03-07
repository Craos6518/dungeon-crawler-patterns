/**
 * Memento Pattern - Sistema de guardado y carga de partidas
 * 
 * Implementa el patrón Memento para capturar y restaurar el estado del juego
 * sin violar el encapsulamiento.
 * 
 * Componentes:
 * - {@link game.persistence.memento.GameMemento} - Memento (estado inmutable)
 * - {@link game.persistence.memento.GameOriginator} - Originator (crea y restaura mementos)
 * - {@link game.persistence.memento.GameCaretaker} - Caretaker (gestiona mementos)
 * 
 * Roles:
 * - **Memento**: Almacena el estado interno del Originator. Es inmutable y solo
 *   el Originator puede acceder a su contenido completo.
 * - **Originator**: El objeto cuyo estado debe ser guardado. Crea mementos conteniendo
 *   su estado actual y puede restaurarse desde un memento.
 * - **Caretaker**: Responsable de guardar mementos. Nunca examina o modifica el
 *   contenido de un memento. Maneja persistencia en memoria y disco.
 * 
 * Beneficios:
 * - Preserva el encapsulamiento del estado interno
 * - Simplifica el Originator (no necesita gestionar versiones de su estado)
 * - Permite deshacer operaciones y guardar/cargar partidas
 * - El Caretaker no necesita conocer la estructura interna del estado
 * 
 * Ejemplo de uso:
 * <pre>
 * // Crear juego y caretaker
 * GameOriginator juego = new GameOriginator("Héroe");
 * GameCaretaker caretaker = new GameCaretaker();
 * 
 * // Jugar y progresar
 * juego.progresar();
 * juego.progresar();
 * 
 * // Guardar estado
 * GameMemento guardado = juego.guardar();
 * caretaker.guardarEnDisco(guardado, "partida1");
 * 
 * // Más tarde, cargar estado
 * GameMemento cargado = caretaker.cargarDesdeDisco("partida1");
 * juego.restaurar(cargado);
 * </pre>
 * 
 * Persistencia:
 * - Mementos en memoria: Rápido, temporal, útil para undo/redo
 * - Mementos en disco: Persistente, permite guardar/cargar partidas
 * 
 * @see game.persistence.memento.GameMemento
 * @see game.persistence.memento.GameOriginator
 * @see game.persistence.memento.GameCaretaker
 */
package game.persistence.memento;
