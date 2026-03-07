package game.ai.strategy;

import game.command.actions.AttackCommand;
import game.command.actions.Command;
import game.domain.personaje.Personaje;

import java.util.List;
import java.util.Random;

/**
 * Strategy concreta - Comportamiento aleatorio
 * 
 * Ataca a un enemigo al azar.
 * Útil para comportamientos impredecibles o enemigos de bajo nivel.
 */
public class RandomStrategy implements AIStrategy {
    private final Random random;
    
    public RandomStrategy() {
        this.random = new Random();
    }
    
    public RandomStrategy(long seed) {
        this.random = new Random(seed);
    }
    
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
        
        // Seleccionar un enemigo al azar
        int indiceAleatorio = random.nextInt(enemigosVivos.size());
        Personaje objetivo = enemigosVivos.get(indiceAleatorio);
        
        return new AttackCommand(propio, objetivo);
    }
    
    @Override
    public String getNombreEstrategia() {
        return "Aleatoria";
    }
    
    @Override
    public String getDescripcion() {
        return "Ataca a un enemigo al azar, comportamiento impredecible";
    }
}
