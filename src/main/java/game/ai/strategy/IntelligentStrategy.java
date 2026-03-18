package game.ai.strategy;

import game.command.actions.AttackCommand;
import game.command.actions.Command;
import game.command.actions.DefendCommand;
import game.domain.personaje.Personaje;

import java.util.List;

/**
 * Strategy concreta - Comportamiento inteligente
 * 
 * Toma decisiones basadas en múltiples factores:
 * - Vida propia y del enemigo
 * - Cantidad de enemigos
 * - Prioriza eliminar enemigos con poca vida
 */
public class IntelligentStrategy implements AIStrategy {
    private static final double UMBRAL_VIDA_CRITICA = 0.25; // 25% de vida
    private static final int UMBRAL_VIDA_ENEMIGO_DEBIL = 30;
    
    @Override
    public Command decidirAccion(Personaje propio, List<Personaje> enemigos) {
        if (enemigos == null || enemigos.isEmpty()) {
            throw new IllegalArgumentException("No hay enemigos disponibles");
        }
        
        List<Personaje> enemigosVivos = enemigos.stream()
            .filter(Personaje::estaVivo)
            .toList();
        
        if (enemigosVivos.isEmpty()) {
            throw new IllegalStateException("No hay enemigos vivos");
        }
        
        // Si está en vida crítica y hay múltiples enemigos, defenderse
        if (propio.getVida() < 100 * UMBRAL_VIDA_CRITICA && enemigosVivos.size() > 1) {
            return new DefendCommand(propio);
        }
        
        // Priorizar eliminar enemigos débiles (pueden ser eliminados en un turno)
        Personaje enemigoDebil = enemigosVivos.stream()
            .filter(e -> e.getVida() <= UMBRAL_VIDA_ENEMIGO_DEBIL)
            .findFirst()
            .orElse(null);
        
        if (enemigoDebil != null) {
            return new AttackCommand(propio, enemigoDebil);
        }
        
        // Si no hay enemigos débiles, atacar al que tenga más vida
        // (probablemente es el más peligroso)
        Personaje objetivo = enemigosVivos.stream()
            .max((e1, e2) -> Integer.compare(e1.getVida(), e2.getVida()))
            .get();
        
        return new AttackCommand(propio, objetivo);
    }
    
    @Override
    public String getNombreEstrategia() {
        return "Inteligente";
    }
    
    @Override
    public String getDescripcion() {
        return "Analiza múltiples factores para tomar la mejor decisión táctica";
    }
}
