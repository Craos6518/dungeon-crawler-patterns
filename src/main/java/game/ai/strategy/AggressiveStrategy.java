package game.ai.strategy;

import game.patterns.command.actions.AttackCommand;
import game.patterns.command.actions.Command;
import game.domain.personaje.Personaje;

import java.util.List;

/**
 * Strategy concreta - Comportamiento agresivo
 * 
 * Siempre ataca al enemigo con más vida.
 * No considera defensa ni estrategias complejas.
 */
public class AggressiveStrategy implements AIStrategy {
    
    @Override
    public Command decidirAccion(Personaje propio, List<Personaje> enemigos) {
        if (enemigos == null || enemigos.isEmpty()) {
            throw new IllegalArgumentException("No hay enemigos disponibles");
        }
        
        // Atacar al enemigo con más vida
        Personaje objetivo = enemigos.stream()
            .filter(Personaje::estaVivo)
            .max((e1, e2) -> Integer.compare(e1.getVida(), e2.getVida()))
            .orElseThrow(() -> new IllegalStateException("No hay enemigos vivos"));
        
        return new AttackCommand(propio, objetivo);
    }
    
    @Override
    public String getNombreEstrategia() {
        return "Agresiva";
    }
    
    @Override
    public String getDescripcion() {
        return "Ataca siempre al enemigo con más vida, sin considerar defensa";
    }
}
