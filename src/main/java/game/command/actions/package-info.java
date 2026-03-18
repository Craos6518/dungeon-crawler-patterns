/**
 * Command Pattern - Sistema de comandos de acción
 * 
 * Implementa el patrón Command para encapsular acciones del juego como objetos.
 * 
 * Componentes:
 * - {@link game.command.actions.Command} - Interfaz base del comando
 * - {@link game.command.actions.AttackCommand} - Comando de ataque
 * - {@link game.command.actions.DefendCommand} - Comando de defensa
 * - {@link game.command.actions.UseItemCommand} - Comando de usar item
 * - {@link game.command.actions.SkillCommand} - Comando de habilidad especial
 * - {@link game.command.actions.CommandInvoker} - Invocador que ejecuta y registra comandos
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
 * @see game.command.actions.Command
 * @see game.command.actions.CommandInvoker
 */
package game.command.actions;
