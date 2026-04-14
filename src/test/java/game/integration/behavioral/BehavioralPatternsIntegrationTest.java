package game.integration.behavioral;

import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.ai.strategy.AggressiveStrategy;
import game.ai.strategy.DefensiveStrategy;
import game.patterns.command.actions.AttackCommand;
import game.patterns.command.actions.Command;
import game.patterns.command.actions.CommandInvoker;
import game.patterns.command.actions.DefendCommand;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.EnemigoBasicoFactory;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.personaje.factory.PersonajeFactory;
import game.infrastructure.events.observer.CombatLogger;
import game.infrastructure.events.observer.EventManager;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;
import game.infrastructure.events.observer.StatisticsTracker;
import game.infrastructure.persistence.memento.GameCaretaker;
import game.application.state.GameMemento;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración que demuestra cómo interactúan los patrones de comportamiento
 * Command + Strategy + Observer + Memento en un escenario de combate completo.
 * Adaptado para usar GameSession (flujo productivo).
 */
public class BehavioralPatternsIntegrationTest {
    
    private EventManager eventManager;
    private CombatLogger logger;
    private StatisticsTracker stats;
    private CommandInvoker invoker;
    private GameSession session;
    private GameCaretaker caretaker;
    
    @BeforeEach
    public void setUp() {
        // Observer: Configurar sistema de eventos
        eventManager = EventManager.getInstance();
        eventManager.limpiar();
        
        logger = new CombatLogger(false);
        stats = new StatisticsTracker();
        
        eventManager.suscribir(logger);
        eventManager.suscribir(stats);
        
        // Command: Configurar invocador
        invoker = new CommandInvoker();
        
        // Memento: Configurar sesión productiva
        session = GameSessionFactory.createSessionForTheme("fire");
        caretaker = new GameCaretaker("./test-saves/");
    }
    
    @Test
    public void testCommandWithObserverIntegration() {
        PersonajeFactory guerreroFactory = new GuerreroFactory(100, 25);
        PersonajeFactory enemigoFactory = new EnemigoBasicoFactory(60, 15);
        Personaje heroe = guerreroFactory.crearPersonaje("Héroe");
        Personaje enemigo = enemigoFactory.crearPersonaje("Enemigo");
        
        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("enemigo", enemigo.getNombre()));
        
        Command ataque = new AttackCommand(heroe, enemigo);
        invoker.ejecutarComando(ataque);
        int damage = ((AttackCommand) ataque).getDanioAplicado();
        
        eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", heroe.getNombre())
            .agregarDato("defensor", enemigo.getNombre())
            .agregarDato("danio", damage));
        
        assertEquals(1, stats.getAtaquesTotales());
        assertEquals(1, invoker.getCantidadComandos());
        assertFalse(logger.getLog().isEmpty());
    }
    
    @Test
    public void testStrategyWithCommandIntegration() {
        PersonajeFactory enemigoFactory = new EnemigoBasicoFactory(80, 15);
        PersonajeFactory guerreroFactory = new GuerreroFactory(100, 20);
        Personaje enemigo = enemigoFactory.crearPersonaje("IA");
        Personaje heroe = guerreroFactory.crearPersonaje("Jugador");
        
        AIStrategy estrategia = new AggressiveStrategy();
        AIController ia = new AIController(enemigo, estrategia);
        
        Command comando = ia.decidirAccion(List.of(heroe));
        
        assertNotNull(comando);
        assertTrue(comando instanceof AttackCommand);
        
        invoker.ejecutarComando(comando);
        assertEquals(1, invoker.getCantidadComandos());
    }
    
    @Test
    public void testMementoWithGameStateIntegration() {
        int nivelInicial = session.player().level();
        
        // Guardar estado
        GameMemento checkpoint = session.createSnapshot();
        caretaker.guardarEnMemoria(checkpoint);
        
        // Progresar: subir nivel
        session.player().restoreProgress(5, 0, 100, 0, 0, 100);
        
        int nivelFinal = session.player().level();
        assertTrue(nivelFinal > nivelInicial);
        
        // Restaurar checkpoint
        session.restoreSnapshot(checkpoint);
        
        assertEquals(nivelInicial, session.player().level());
    }
    
    @Test
    public void testFullIntegrationScenario() {
        PersonajeFactory guerreroFactory = new GuerreroFactory(100, 30);
        PersonajeFactory enemigoFactory = new EnemigoBasicoFactory(70, 18);
        Personaje heroe = guerreroFactory.crearPersonaje("Héroe");
        Personaje enemigo = enemigoFactory.crearPersonaje("Jefe");
        
        GameMemento antesDelCombate = session.createSnapshot();
        caretaker.guardarEnMemoria(antesDelCombate);
        
        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("enemigo", enemigo.getNombre()));
        
        AIController ia = new AIController(enemigo, new AggressiveStrategy());
        Command comandoIA = ia.decidirAccion(List.of(heroe));
        
        invoker.ejecutarComando(comandoIA);
        int damageIA = ((AttackCommand) comandoIA).getDanioAplicado();
        
        eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", enemigo.getNombre())
            .agregarDato("defensor", heroe.getNombre())
            .agregarDato("danio", damageIA));
        
        Command contraataque = new AttackCommand(heroe, enemigo);
        invoker.ejecutarComando(contraataque);
        int damageHeroe = ((AttackCommand) contraataque).getDanioAplicado();
        
        eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", heroe.getNombre())
            .agregarDato("defensor", enemigo.getNombre())
            .agregarDato("danio", damageHeroe));
        
        assertEquals(2, invoker.getCantidadComandos());
        assertEquals(2, stats.getAtaquesTotales());
        assertFalse(logger.getLog().isEmpty());
        assertEquals(1, caretaker.getCantidadMementos());
    }
}
