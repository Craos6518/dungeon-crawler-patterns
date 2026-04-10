/**
 * Command Pattern - Sistema de comandos de acción
 * 
 * Implementa el patrón Command para encapsular acciones del juego como objetos.
 * 
 * Componentes:
 * - {@link game.patterns.command.actions.Command} - Interfaz base del comando
 * - {@link game.patterns.command.actions.AttackCommand} - Comando de ataque
 * - {@link game.patterns.command.actions.DefendCommand} - Comando de defensa
 * - {@link game.patterns.command.actions.UseItemCommand} - Comando de usar item
 * - {@link game.patterns.command.actions.SkillCommand} - Comando de habilidad especial
 * - {@link game.patterns.command.actions.CommandInvoker} - Invocador que ejecuta y registra comandos
 * 
 * Beneficios:
 * - Desacopla el objeto que invoca la operación del que la ejecuta
 * - Permite parametrizar objetos con operaciones
 * - Soporta historial de comandos
 * - Permite operaciones reversibles (undo)
 * - Facilita el logging y auditoría
 * 
 * Ejemplo de uso:
 * <pre>
 * Command ataque = new AttackCommand(guerrero, enemigo);
 * CommandInvoker invoker = new CommandInvoker();
 * invoker.ejecutarComando(ataque);
 * // Deshacer si es necesario
 * invoker.undoLastCommand();
 * </pre>
 * 
 * @see game.patterns.command.actions.Command
 * @see game.patterns.command.actions.CommandInvoker
 */
package game.patterns.command.actions;
