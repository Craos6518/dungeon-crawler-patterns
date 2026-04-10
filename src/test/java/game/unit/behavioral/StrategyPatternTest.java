package game.unit.behavioral;

import game.ai.strategy.*;
import game.patterns.command.actions.AttackCommand;
import game.patterns.command.actions.Command;
import game.patterns.command.actions.DefendCommand;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para el patrón Strategy
 */
public class StrategyPatternTest {
    
    private Personaje enemigo;
    private List<Personaje> heroes;
    
    @BeforeEach
    public void setUp() {
        enemigo = new EnemigoBasico("Goblín", 60, 12);
        heroes = new ArrayList<>();
        heroes.add(new Guerrero("Héroe 1", 100, 20));
        heroes.add(new Guerrero("Héroe 2", 50, 15));
        heroes.add(new Guerrero("Héroe 3", 80, 18));
    }
    
    @Test
    public void testAggressiveStrategyAttacksMostHealth() {
        AIStrategy estrategia = new AggressiveStrategy();
        
        assertEquals("Agresiva", estrategia.getNombreEstrategia());
        assertNotNull(estrategia.getDescripcion());
        
        Command comando = estrategia.decidirAccion(enemigo, heroes);
        
        assertNotNull(comando);
        assertTrue(comando instanceof AttackCommand);
        
        AttackCommand ataque = (AttackCommand) comando;
        // La estrategia agresiva debería atacar al que tiene más vida (Héroe 1 con 100 HP)
        assertEquals("Héroe 1", ataque.getDefensor().getNombre());
    }
    
    @Test
    public void testDefensiveStrategyDefendsWhenLowHealth() {
        // Reducir vida del enemigo a menos del 30%
        enemigo.recibirDanio(50); // Queda con 10 HP (menos del 30% de ~100)
        
        AIStrategy estrategia = new DefensiveStrategy();
        Command comando = estrategia.decidirAccion(enemigo, heroes);
        
        // Con poca vida, debería defenderse
        assertTrue(comando instanceof DefendCommand, 
            "Con poca vida, la estrategia defensiva debería defenderse");
    }
    
    @Test
    public void testDefensiveStrategyAttacksWeakestWhenHealthy() {
        AIStrategy estrategia = new DefensiveStrategy();
        Command comando = estrategia.decidirAccion(enemigo, heroes);
        
        assertNotNull(comando);
        assertTrue(comando instanceof AttackCommand);
        
        AttackCommand ataque = (AttackCommand) comando;
        // Debería atacar al más débil (Héroe 2 con 50 HP)
        assertEquals("Héroe 2", ataque.getDefensor().getNombre());
    }

    @Test
    public void testDefensiveStrategyDoesNotPermaDefendLowMaxHpEnemies() {
        Personaje salamandra = new EnemigoBasico("Salamandra", 30, 6);
        salamandra.recibirDanio(1); // 29/30: aún no está en vida crítica relativa

        AIStrategy estrategia = new DefensiveStrategy();
        Command comando = estrategia.decidirAccion(salamandra, heroes);

        assertTrue(comando instanceof AttackCommand,
            "Un enemigo en 29/30 HP no debe entrar en defensa permanente");
    }
    
    @Test
    public void testIntelligentStrategyPrioritizesWeakEnemies() {
        // Reducir la vida de uno de los héroes para hacerlo débil
        heroes.get(1).recibirDanio(30); // Héroe 2 ahora tiene 20 HP
        
        AIStrategy estrategia = new IntelligentStrategy();
        Command comando = estrategia.decidirAccion(enemigo, heroes);
        
        assertTrue(comando instanceof AttackCommand);
        AttackCommand ataque = (AttackCommand) comando;
        
        // Debería priorizar al héroe débil para eliminarlo
        assertEquals("Héroe 2", ataque.getDefensor().getNombre());
    }
    
    @Test
    public void testRandomStrategySelectsAnyEnemy() {
        AIStrategy estrategia = new RandomStrategy(42); // Seed fija para reproducibilidad
        
        assertEquals("Aleatoria", estrategia.getNombreEstrategia());
        
        Command comando = estrategia.decidirAccion(enemigo, heroes);
        
        assertNotNull(comando);
        assertTrue(comando instanceof AttackCommand);
        
        AttackCommand ataque = (AttackCommand) comando;
        // Debería atacar a alguno de los héroes (cualquiera es válido)
        assertTrue(heroes.stream().anyMatch(h -> h.getNombre().equals(ataque.getDefensor().getNombre())));
    }
    
    @Test
    public void testAIControllerChangeStrategy() {
        AIStrategy estrategia1 = new AggressiveStrategy();
        AIController controller = new AIController(enemigo, estrategia1);
        
        assertEquals(estrategia1, controller.getEstrategia());
        assertEquals(enemigo, controller.getPersonaje());
        
        AIStrategy estrategia2 = new DefensiveStrategy();
        controller.setEstrategia(estrategia2);
        
        assertEquals(estrategia2, controller.getEstrategia(), 
            "La estrategia debería haber cambiado");
    }
    
    @Test
    public void testAIControllerDelegatesDecision() {
        AIStrategy estrategia = new AggressiveStrategy();
        AIController controller = new AIController(enemigo, estrategia);
        
        Command comando = controller.decidirAccion(heroes);
        
        assertNotNull(comando);
        assertTrue(comando instanceof AttackCommand);
    }
    
    @Test
    public void testStrategyThrowsExceptionWithEmptyEnemyList() {
        AIStrategy estrategia = new AggressiveStrategy();
        List<Personaje> listaVacia = new ArrayList<>();
        
        assertThrows(IllegalArgumentException.class, () -> {
            estrategia.decidirAccion(enemigo, listaVacia);
        });
    }
    
    @Test
    public void testStrategyThrowsExceptionWithNullEnemyList() {
        AIStrategy estrategia = new AggressiveStrategy();
        
        assertThrows(IllegalArgumentException.class, () -> {
            estrategia.decidirAccion(enemigo, null);
        });
    }
    
    @Test
    public void testAIControllerRequiresNonNullStrategy() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AIController(enemigo, null);
        });
    }
}
