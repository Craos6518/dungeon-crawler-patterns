package game.command.actions;

import game.domain.personaje.Personaje;

/**
 * Command concreto para ejecutar habilidades especiales
 */
public class SkillCommand implements Command {
    private final Personaje usuario;
    private final String nombreHabilidad;
    private final Personaje objetivo;
    private boolean ejecutado;
    
    public SkillCommand(Personaje usuario, String nombreHabilidad, Personaje objetivo) {
        this.usuario = usuario;
        this.nombreHabilidad = nombreHabilidad;
        this.objetivo = objetivo;
        this.ejecutado = false;
    }
    
    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException("No se puede ejecutar la habilidad");
        }
        
        // Lógica simplificada: ejecutar habilidad especial
        // En un sistema completo, cada clase de personaje tendría sus propias habilidades
        ejecutado = true;
    }
    
    @Override
    public boolean canExecute() {
        return usuario != null && 
               usuario.estaVivo() &&
               nombreHabilidad != null && 
               !nombreHabilidad.isEmpty() &&
               objetivo != null &&
               objetivo.estaVivo() &&
               !ejecutado;
    }
    
    @Override
    public String getDescription() {
        return String.format("%s usa %s contra %s", 
            usuario.getNombre(), 
            nombreHabilidad,
            objetivo.getNombre());
    }
    
    public Personaje getUsuario() {
        return usuario;
    }
    
    public String getNombreHabilidad() {
        return nombreHabilidad;
    }
    
    public Personaje getObjetivo() {
        return objetivo;
    }
    
    public boolean isEjecutado() {
        return ejecutado;
    }
}
