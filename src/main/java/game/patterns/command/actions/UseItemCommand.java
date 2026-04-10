package game.patterns.command.actions;

import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;

/**
 * Command concreto para usar un item sobre un personaje
 */
public class UseItemCommand implements Command {
    private final Personaje usuario;
    private final SimpleItem item;
    private final Personaje objetivo;
    private boolean usado;
    
    public UseItemCommand(Personaje usuario, SimpleItem item, Personaje objetivo) {
        this.usuario = usuario;
        this.item = item;
        this.objetivo = objetivo;
        this.usado = false;
    }
    
    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException("No se puede usar el item");
        }
        
        // Lógica simplificada: los items podrían curar, dañar, etc.
        // En un sistema completo, cada item tendría su propia lógica
        usado = true;
    }
    
    @Override
    public void undo() {
        if (!usado) {
            throw new IllegalStateException("El comando no ha sido ejecutado");
        }
        // Revertir el uso del item
        usado = false;
    }
    
    @Override
    public boolean canExecute() {
        return usuario != null && 
               usuario.estaVivo() &&
               item != null && 
               objetivo != null &&
               objetivo.estaVivo() &&
               !usado;
    }
    
    @Override
    public String getDescription() {
        return String.format("%s usa %s en %s", 
            usuario.getNombre(), 
            item.getNombre(),
            objetivo.getNombre());
    }
    
    public Personaje getUsuario() {
        return usuario;
    }
    
    public SimpleItem getItem() {
        return item;
    }
    
    public Personaje getObjetivo() {
        return objetivo;
    }
}
