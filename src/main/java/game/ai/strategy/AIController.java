package game.ai.strategy;

import game.domain.personaje.Personaje;

/**
 * Contexto del patrón Strategy
 * 
 * Representa un personaje controlado por IA con una estrategia intercambiable.
 * Permite cambiar el comportamiento del personaje en tiempo de ejecución.
 */
public class AIController {
    private final Personaje personaje;
    private AIStrategy estrategia;
    
    public AIController(Personaje personaje, AIStrategy estrategia) {
        if (personaje == null) {
            throw new IllegalArgumentException("El personaje no puede ser null");
        }
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser null");
        }
        
        this.personaje = personaje;
        this.estrategia = estrategia;
    }
    
    /**
     * Cambia la estrategia del personaje en tiempo de ejecución
     */
    public void setEstrategia(AIStrategy nuevaEstrategia) {
        if (nuevaEstrategia == null) {
            throw new IllegalArgumentException("La estrategia no puede ser null");
        }
        this.estrategia = nuevaEstrategia;
    }
    
    /**
     * Obtiene la estrategia actual
     */
    public AIStrategy getEstrategia() {
        return estrategia;
    }
    
    /**
     * Obtiene el personaje controlado
     */
    public Personaje getPersonaje() {
        return personaje;
    }
    
    /**
     * Delega la decisión a la estrategia actual
     */
    public game.patterns.command.actions.Command decidirAccion(java.util.List<Personaje> enemigos) {
        return estrategia.decidirAccion(personaje, enemigos);
    }
    
    @Override
    public String toString() {
        return String.format("AIController[%s, Estrategia: %s]", 
            personaje.getNombre(), 
            estrategia.getNombreEstrategia());
    }
}
