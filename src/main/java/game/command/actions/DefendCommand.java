package game.command.actions;

import game.domain.personaje.Personaje;

/**
 * Command concreto para postura defensiva
 * Reduce el daño recibido en el próximo turno
 */
public class DefendCommand implements Command {
    private final Personaje personaje;
    private boolean ejecutado;
    
    public DefendCommand(Personaje personaje) {
        this.personaje = personaje;
        this.ejecutado = false;
    }
    
    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException(
                "No se puede ejecutar DefendCommand: personaje muerto o ya ejecutado"
            );
        }
        
        // Nota: En un sistema completo, esto modificaría el estado del personaje
        // para reducir el daño recibido en el siguiente turno
        ejecutado = true;
    }
    
    @Override
    public void undo() {
        if (!ejecutado) {
            throw new IllegalStateException("El comando no ha sido ejecutado");
        }
        ejecutado = false;
    }
    
    @Override
    public boolean canExecute() {
        return personaje != null && 
               personaje.estaVivo() && 
               !ejecutado;
    }
    
    @Override
    public String getDescription() {
        return String.format("%s toma postura defensiva", personaje.getNombre());
    }
    
    public Personaje getPersonaje() {
        return personaje;
    }
    
    public boolean isEjecutado() {
        return ejecutado;
    }
}
