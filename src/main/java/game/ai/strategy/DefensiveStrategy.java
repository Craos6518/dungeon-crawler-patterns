package game.ai.strategy;

import game.command.actions.AttackCommand;
import game.command.actions.Command;
import game.command.actions.DefendCommand;
import game.domain.personaje.Personaje;

import java.util.List;

/**
 * Strategy concreta - Comportamiento defensivo
 * 
 * Prioriza la supervivencia sobre el daño.
 * Se defiende cuando tiene poca vida, ataca cuando está seguro.
 */
public class DefensiveStrategy implements AIStrategy {
    private static final double UMBRAL_VIDA_BAJA = 0.3; // 30% de vida
    
    @Override
    public Command decidirAccion(Personaje propio, List<Personaje> enemigos) {
        if (enemigos == null || enemigos.isEmpty()) {
            throw new IllegalArgumentException("No hay enemigos disponibles");
        }
        
        // Si tiene poca vida relativa, defenderse.
        int vidaMaxima = Math.max(1, propio.getVidaMaxima());
        double porcentajeVida = (double) propio.getVida() / vidaMaxima;
        if (porcentajeVida < UMBRAL_VIDA_BAJA) {
            return new DefendCommand(propio);
        }
        
        // Si tiene suficiente vida, atacar al enemigo más débil
        Personaje objetivo = enemigos.stream()
            .filter(Personaje::estaVivo)
            .min((e1, e2) -> Integer.compare(e1.getVida(), e2.getVida()))
            .orElseThrow(() -> new IllegalStateException("No hay enemigos vivos"));
        
        return new AttackCommand(propio, objetivo);
    }
    
    @Override
    public String getNombreEstrategia() {
        return "Defensiva";
    }
    
    @Override
    public String getDescripcion() {
        return "Se defiende cuando tiene poca vida, ataca al más débil cuando está seguro";
    }
}
