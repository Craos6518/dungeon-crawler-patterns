/**
 * Observer Pattern - Sistema de eventos del juego
 * 
 * Implementa el patrón Observer para notificar a múltiples objetos
 * cuando ocurren eventos en el juego.
 * 
 * Componentes:
 * - {@link game.application.ports.events.GameObserver} - Interfaz Observer
 * - {@link game.infrastructure.events.observer.EventManager} - Subject que gestiona notificaciones
 * - {@link game.application.ports.events.GameEvent} - Encapsula información del evento
 * - {@link game.application.ports.events.EventType} - Tipos de eventos posibles
 * - {@link game.infrastructure.events.observer.CombatLogger} - Observer que registra combates
 * - {@link game.infrastructure.events.observer.StatisticsTracker} - Observer que recopila estadísticas
 * - {@link game.infrastructure.events.observer.UINotifier} - Observer que notifica a la UI
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
 * @see game.application.ports.events.GameObserver
 * @see game.infrastructure.events.observer.EventManager
 */
package game.application.ports.events;
