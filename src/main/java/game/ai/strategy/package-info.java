/**
 * Strategy Pattern - Sistema de estrategias de IA
 * 
 * Implementa el patrón Strategy para definir comportamientos intercambiables de enemigos.
 * 
 * Componentes:
 * - {@link game.ai.strategy.AIStrategy} - Interfaz de estrategia
 * - {@link game.ai.strategy.AggressiveStrategy} - Ataca al enemigo con más vida
 * - {@link game.ai.strategy.DefensiveStrategy} - Prioriza supervivencia
 * - {@link game.ai.strategy.IntelligentStrategy} - Analiza múltiples factores
 * - {@link game.ai.strategy.RandomStrategy} - Comportamiento aleatorio
 * - {@link game.ai.strategy.AIController} - Contexto que usa las estrategias
 * 
 * Beneficios:
 * - Elimina condicionales complejos (if/switch para cada comportamiento)
 * - Permite cambiar comportamiento en tiempo de ejecución
 * - Facilita agregar nuevas estrategias sin modificar código existente
 * - Cada estrategia es independiente y testeable
 * 
 * Ejemplo de uso:
 * <pre>
 * AIStrategy estrategia = new AggressiveStrategy();
 * AIController controller = new AIController(enemigo, estrategia);
 * Command accion = controller.decidirAccion(listaEnemigos);
 * 
 * // Cambiar estrategia en tiempo de ejecución
 * controller.setEstrategia(new DefensiveStrategy());
 * </pre>
 * 
 * @see game.ai.strategy.AIStrategy
 * @see game.ai.strategy.AIController
 */
package game.ai.strategy;
