/**
 * Memento Pattern - Sistema de guardado y carga de partidas
 * 
 * Implementa el patrón Memento para capturar y restaurar el estado del juego
 * sin violar el encapsulamiento.
 * 
 * Componentes Actuales (Productivos):
 * - {@link game.application.state.GameMemento} - Memento (estado inmutable, serializable)
 * - {@link game.application.state.GameSessionMementoMapper} - Originator (crea y restaura mementos desde GameSession)
 * - {@link game.infrastructure.persistence.memento.GameCaretaker} - Caretaker (gestiona persistencia en disco y memoria)
 * 
 * Roles:
 * - **Memento**: Almacena el estado inmutable del juego en un momento específico.
 *   Incluye validación de schemaVersion para detectar incompatibilidades de formato.
 * - **Originator**: GameSessionMementoMapper en el contexto productivo real.
 *   Transforma GameSession compleja en GameMemento serializable y viceversa.
 * - **Caretaker**: GameCaretaker gestiona almacenamiento físico (disco) y carga de mementos.
 *   Implementa la interfaz SessionSnapshotStore del contrato de persistencia.
 * 
 * Beneficios:
 * - Preserva el encapsulamiento del estado interno
 * - Simplifica el Originator (no necesita gestionar versiones de su estado)
 * - Permite guardar/cargar partidas completas garantizando integridad
 * - El Caretaker no necesita conocer la estructura interna del estado
 * - Validación de esquema previene corrupción de datos entre versiones
 * 
 * Ejemplo de uso en Runtime Productivo:
 * <pre>
 * // Desde GameRuntime, comando saveToSlot invoca RuntimeSaveSlotManager
 * // que usa SaveGameUseCase:
 * SaveGameUseCase saveUC = new SaveGameUseCase(session);
 * saveUC.execute(1); // Slot_1
 * 
 * // Internamente:
 * GameMemento memento = GameSessionMementoMapper.toMemento(session);
 * // memento.schemaVersion = "1.0"
 * caretaker.guardarEnDisco(memento, "Slot_1");
 * 
 * // Para cargar:
 * LoadGameUseCase loadUC = new LoadGameUseCase(targetSession);
 * loadUC.execute(1);
 * 
 * // Internamente:
 * GameMemento loaded = caretaker.cargarDesdeDisco("Slot_1");
 * if (!"1.0".equals(loaded.getSchemaVersion())) {
 *     throw new SaveDataCorruptionException("Incompatible version");
 * }
 * GameSessionMementoMapper.restoreStrict(targetSession, loaded);
 * </pre>
 * 
 * Transactional Support:
 * - UseCaseTransactionSupport utiliza Memento para rollback en caso de errores
 * - Snapshot automático antes de ejecutar use case
 * - Restauración automática si ocurre excepción
 * 
 * Persistencia:
 * - Mementos en memoria: Gestión de transacciones
 * - Mementos en disco: Persistencia en slots (Slot_1, Slot_2, Slot_3)
 * 
 * @see game.application.state.GameMemento
 * @see game.application.state.GameSessionMementoMapper
 * @see game.infrastructure.persistence.memento.GameCaretaker
 * @see game.application.usecase.SaveGameUseCase
 * @see game.application.usecase.LoadGameUseCase
 * @see game.application.runtime.RuntimeSaveSlotManager
 */
package game.infrastructure.persistence.memento;
