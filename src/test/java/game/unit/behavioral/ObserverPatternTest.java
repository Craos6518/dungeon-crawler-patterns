package game.unit.behavioral;

import game.events.observer.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para el patrón Observer
 */
public class ObserverPatternTest {
    
    private EventManager manager;
    
    @BeforeEach
    public void setUp() {
        manager = EventManager.getInstance();
        manager.limpiar(); // Limpiar estado previo
    }
    
    @AfterEach
    public void tearDown() {
        manager.limpiar();
    }
    
    @Test
    public void testEventManagerIsSingleton() {
        EventManager manager1 = EventManager.getInstance();
        EventManager manager2 = EventManager.getInstance();
        
        assertSame(manager1, manager2, "EventManager debería ser Singleton");
    }
    
    @Test
    public void testObserverReceivesEvents() {
        CombatLogger logger = new CombatLogger(false);
        manager.suscribir(logger);
        
        GameEvent evento = new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Orco");
        
        manager.notificar(evento);
        
        assertFalse(logger.getLog().isEmpty(), "El logger debería haber recibido el evento");
        assertEquals(1, logger.getLog().size());
    }
    
    @Test
    public void testMultipleObserversReceiveEvents() {
        CombatLogger logger = new CombatLogger(false);
        StatisticsTracker stats = new StatisticsTracker();
        
        manager.suscribir(logger);
        manager.suscribir(stats);
        
        assertEquals(2, manager.getCantidadObservers());
        
        GameEvent evento = new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Héroe")
            .agregarDato("defensor", "Enemigo")
            .agregarDato("danio", 25);
        
        manager.notificar(evento);
        
        // Verificar que ambos observers recibieron el evento
        assertEquals(1, logger.getLog().size());
        assertEquals(1, stats.getAtaquesTotales());
        assertEquals(25, stats.getDanioTotalCausado());
    }
    
    @Test
    public void testObserverCanUnsubscribe() {
        CombatLogger logger = new CombatLogger(false);
        manager.suscribir(logger);
        
        assertEquals(1, manager.getCantidadObservers());
        
        manager.desuscribir(logger);
        
        assertEquals(0, manager.getCantidadObservers());
        
        GameEvent evento = new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", "Guerrero")
            .agregarDato("enemigo", "Orco");
        manager.notificar(evento);
        
        assertTrue(logger.getLog().isEmpty(), "El logger no debería recibir eventos después de desuscribirse");
    }
    
    @Test
    public void testEventTypeSpecificSubscription() {
        CombatLogger logger = new CombatLogger(false);
        
        // Suscribirse solo a eventos de ataque
        manager.suscribir(EventType.ATAQUE_REALIZADO, logger);
        
        GameEvent eventoAtaque = new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Orco")
            .agregarDato("danio", 20);
        
        GameEvent eventoItem = new GameEvent(EventType.ITEM_RECOGIDO)
            .agregarDato("item", "Poción");
        
        manager.notificar(eventoAtaque);
        manager.notificar(eventoItem);
        
        // Solo debería haber recibido el evento de ataque
        assertEquals(1, logger.getLog().size());
    }
    
    @Test
    public void testStatisticsTrackerCountsCorrectly() {
        StatisticsTracker stats = new StatisticsTracker();
        manager.suscribir(stats);
        
        // Simular varios eventos
        manager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", "Guerrero")
            .agregarDato("enemigo", "Orco"));
        manager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Orco")
            .agregarDato("danio", 30));
        manager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Orco")
            .agregarDato("danio", 45));
        manager.notificar(new GameEvent(EventType.PERSONAJE_MUERTO));
        manager.notificar(new GameEvent(EventType.COMBATE_FINALIZADO)
            .agregarDato("ganador", "Guerrero"));
        
        assertEquals(1, stats.getCombatesRealizados());
        assertEquals(2, stats.getAtaquesTotales());
        assertEquals(75, stats.getDanioTotalCausado());
        assertEquals(1, stats.getPersonajesDerrotados());
    }
    
    @Test
    public void testEventHistoryIsKept() {
        manager.limpiarHistorial();
        
        GameEvent evento1 = new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", "Guerrero")
            .agregarDato("enemigo", "Orco");
        GameEvent evento2 = new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Orco")
            .agregarDato("danio", 15);
        
        manager.notificar(evento1);
        manager.notificar(evento2);
        
        var historial = manager.getHistorial();
        
        assertEquals(2, historial.size());
        assertEquals(EventType.COMBATE_INICIADO, historial.get(0).getTipo());
        assertEquals(EventType.ATAQUE_REALIZADO, historial.get(1).getTipo());
    }
    
    @Test
    public void testEventManagerCanBeDisabled() {
        CombatLogger logger = new CombatLogger(false);
        manager.suscribir(logger);
        
        manager.setHabilitado(false);
        
        GameEvent evento = new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", "Guerrero")
            .agregarDato("enemigo", "Orco");
        manager.notificar(evento);
        
        assertTrue(logger.getLog().isEmpty(), 
            "El logger no debería recibir eventos cuando el manager está deshabilitado");
        
        manager.setHabilitado(true);
        manager.notificar(evento);
        
        assertEquals(1, logger.getLog().size(), 
            "El logger debería recibir eventos cuando el manager está habilitado");
    }
    
    @Test
    public void testGameEventStoresData() {
        GameEvent evento = new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Orco")
            .agregarDato("danio", 25);
        
        assertEquals("Guerrero", evento.getDato("atacante"));
        assertEquals("Orco", evento.getDato("defensor"));
        assertEquals(25, evento.getDato("danio"));
    }
    
    @Test
    public void testCombatLoggerFormatting() {
        CombatLogger logger = new CombatLogger(false);
        manager.suscribir(logger);
        
        GameEvent evento = new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Héroe")
            .agregarDato("defensor", "Dragón")
            .agregarDato("danio", 50);
        
        manager.notificar(evento);
        
        String log = logger.getLog().get(0);
        assertTrue(log.contains("Héroe"));
        assertTrue(log.contains("Dragón"));
        assertTrue(log.contains("50"));
    }

    @Test
    public void testEventContractValidationRejectsInvalidPayload() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            manager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
                .agregarDato("danio", 99))
        );

        assertTrue(ex.getMessage().contains("EventContract"));
    }
}
