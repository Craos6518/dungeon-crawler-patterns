package game.command.actions;

/**
 * Command Pattern - Interfaz base para comandos de acción
 * 
 * Encapsula una acción como un objeto, permitiendo:
 * - Parametrizar objetos con operaciones
 * - Encolar operaciones
 * - Registrar histórico de acciones
 * - Soportar operaciones reversibles (undo)
 */
public interface Command {
    /**
     * Ejecuta el comando
     */
    void execute();
    
    /**
     * Deshace el comando (si es posible)
     * Por defecto lanza excepción si no es reversible
     */
    default void undo() {
        throw new UnsupportedOperationException(
            "Este comando no soporta deshacer: " + this.getClass().getSimpleName()
        );
    }
    
    /**
     * Verifica si el comando puede ser ejecutado
     */
    boolean canExecute();
    
    /**
     * Obtiene una descripción del comando
     */
    String getDescription();
}
