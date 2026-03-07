/**
 * Observer Pattern - Sistema de eventos del juego
 * 
 * Implementa el patrón Observer para notificar a múltiples objetos
 * cuando ocurren eventos en el juego.
 * 
 * Componentes:
 * - {@link game.events.observer.GameObserver} - Interfaz Observer
 * - {@link game.events.observer.EventManager} - Subject que gestiona notificaciones
 * - {@link game.events.observer.GameEvent} - Encapsula información del evento
 * - {@link game.events.observer.EventType} - Tipos de eventos posibles
 * - {@link game.events.observer.CombatLogger} - Observer que registra combates
 * - {@link game.events.observer.StatisticsTracker} - Observer que recopila estadísticas
 * - {@link game.events.observer.UINotifier} - Observer que notifica a la UI
 * 
 * Beneficios:
 * - Desacopla el emisor de eventos de los receptores
 * - Permite agregar/remover observers dinámicamente
 * - Facilita la extensibilidad sin modificar código existente
 * - Soporta múltiples observers simultáneos
 * 
 * Ejemplo de uso:
 * <pre>
 * EventManager manager = EventManager.getInstance();
 * 
 * // Crear observers
 * CombatLogger logger = new CombatLogger(true);
 * StatisticsTracker stats = new StatisticsTracker();
 * 
 * // Suscribir
 * manager.suscribir(logger);
 * manager.suscribir(stats);
 * 
 * // Notificar evento
 * GameEvent evento = new GameEvent(EventType.ATAQUE_REALIZADO)
 *     .agregarDato("atacante", "Guerrero")
 *     .agregarDato("defensor", "Orco")
 *     .agregarDato("danio", 25);
 * manager.notificar(evento);
 * </pre>
 * 
 * @see game.events.observer.GameObserver
 * @see game.events.observer.EventManager
 */
package game.events.observer;
