package game.integration.behavioral;

import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.ai.strategy.AggressiveStrategy;
import game.ai.strategy.DefensiveStrategy;
import game.command.actions.AttackCommand;
import game.command.actions.Command;
import game.command.actions.CommandInvoker;
import game.command.actions.DefendCommand;
import game.combat.model.ResultadoAtaque;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.EnemigoBasicoFactory;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.personaje.factory.PersonajeFactory;
import game.events.observer.CombatLogger;
import game.events.observer.EventManager;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.events.observer.StatisticsTracker;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración que demuestra cómo interactúan los patrones de comportamiento
 * Command + Strategy + Observer + Memento en un escenario de combate completo
 */
public class BehavioralPatternsIntegrationTest {
    
    private EventManager eventManager;
    private CombatLogger logger;
    private StatisticsTracker stats;
    private CommandInvoker invoker;
    private GameOriginator juego;
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
        
        // Memento: Configurar guardado
        juego = new GameOriginator("Héroe de Integración");
        caretaker = new GameCaretaker("./test-saves/");
    }
    
    @Test
    public void testCommandWithObserverIntegration() {
        // Este test demuestra cómo Command y Observer trabajan juntos
        PersonajeFactory guerreroFactory = new GuerreroFactory(100, 25);
        PersonajeFactory enemigoFactory = new EnemigoBasicoFactory(60, 15);
        Personaje heroe = guerreroFactory.crearPersonaje("Héroe");
        Personaje enemigo = enemigoFactory.crearPersonaje("Enemigo");
        
        // Notificar inicio de combate
        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO));
        
        // Ejecutar comando de ataque
        Command ataque = new AttackCommand(heroe, enemigo);
        invoker.ejecutarComando(ataque);
        
        ResultadoAtaque resultado = heroe.atacar(enemigo);
        
        // Notificar el ataque
        eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("danio", resultado.danio()));
        
        // Verificar que el observer capturó el evento
        assertEquals(1, stats.getAtaquesTotales());
        assertEquals(1, invoker.getCantidadComandos());
        assertFalse(logger.getLog().isEmpty());
    }
    
    @Test
    public void testStrategyWithCommandIntegration() {
        // Este test demuestra cómo Strategy genera Commands
        PersonajeFactory enemigoFactory = new EnemigoBasicoFactory(80, 15);
        PersonajeFactory guerreroFactory = new GuerreroFactory(100, 20);
        Personaje enemigo = enemigoFactory.crearPersonaje("IA");
        Personaje heroe = guerreroFactory.crearPersonaje("Jugador");
        
        // Configurar IA con estrategia agresiva
        AIStrategy estrategia = new AggressiveStrategy();
        AIController ia = new AIController(enemigo, estrategia);
        
        // La IA decide su acción
        Command comando = ia.decidirAccion(List.of(heroe));
        
        assertNotNull(comando);
        assertTrue(comando instanceof AttackCommand);
        
        // Ejecutar el comando
        invoker.ejecutarComando(comando);
        
        assertEquals(1, invoker.getCantidadComandos());
    }
    
    @Test
    public void testMementoWithGameStateIntegration() {
        // Este test demuestra el guardado y restauración de estado
        juego.progresar();
        int salaInicial = juego.getSalaActual();
        
        // Guardar estado
        GameMemento checkpoint = juego.guardar();
        caretaker.guardarEnMemoria(checkpoint);
        
        // Progresar más
        juego.progresar();
        juego.progresar();
        
        int salaFinal = juego.getSalaActual();
        assertTrue(salaFinal > salaInicial);
        
        // Restaurar checkpoint
        juego.restaurar(checkpoint);
        
        assertEquals(salaInicial, juego.getSalaActual());
    }
    
    @Test
    public void testFullIntegrationScenario() {
        // Test completo que usa todos los patrones juntos
        PersonajeFactory guerreroFactory = new GuerreroFactory(100, 30);
        PersonajeFactory enemigoFactory = new EnemigoBasicoFactory(70, 18);
        Personaje heroe = guerreroFactory.crearPersonaje("Héroe");
        Personaje enemigo = enemigoFactory.crearPersonaje("Jefe");
        
        // Memento: Guardar antes del combate
        GameMemento antesDelCombate = juego.guardar();
        caretaker.guardarEnMemoria(antesDelCombate);
        
        // Observer: Combate inicia
        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO));
        
        // Strategy: IA decide atacar
        AIController ia = new AIController(enemigo, new AggressiveStrategy());
        Command comandoIA = ia.decidirAccion(List.of(heroe));
        
        // Command: Ejecutar ataque
        invoker.ejecutarComando(comandoIA);
        
        ResultadoAtaque resultado1 = enemigo.atacar(heroe);
        
        // Observer: Notificar ataque
        eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("danio", resultado1.danio()));
        
        // Command: Héroe contraataca
        Command contraataque = new AttackCommand(heroe, enemigo);
        invoker.ejecutarComando(contraataque);
        
        ResultadoAtaque resultado2 = heroe.atacar(enemigo);
        
        eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("danio", resultado2.danio()));
        
        // Verificaciones
        assertEquals(2, invoker.getCantidadComandos());
        assertEquals(2, stats.getAtaquesTotales());
        assertFalse(logger.getLog().isEmpty());
        assertEquals(1, caretaker.getCantidadMementos());
        
        // Observer: Combate termina
        eventManager.notificar(new GameEvent(EventType.COMBATE_FINALIZADO));
        assertEquals(1, stats.getCombatesRealizados());
    }
    
    @Test
    public void testStrategyChange() {
        // Test que demuestra cambio dinámico de estrategia
        PersonajeFactory enemigoFactory = new EnemigoBasicoFactory(100, 15);
        PersonajeFactory guerreroFactory = new GuerreroFactory(120, 25);
        Personaje enemigo = enemigoFactory.crearPersonaje("Enemigo Adaptable");
        Personaje heroe = guerreroFactory.crearPersonaje("Héroe");
        
        AIController ia = new AIController(enemigo, new AggressiveStrategy());
        
        // Con vida alta, ataca
        Command cmd1 = ia.decidirAccion(List.of(heroe));
        assertTrue(cmd1 instanceof AttackCommand);
        
        // Reducir vida
        enemigo.recibirDanio(75);
        
        // Cambiar estrategia a defensiva
        ia.setEstrategia(new DefensiveStrategy());
        
        // Con vida baja, defiende
        Command cmd2 = ia.decidirAccion(List.of(heroe));
        assertTrue(cmd2 instanceof DefendCommand);
    }
}
