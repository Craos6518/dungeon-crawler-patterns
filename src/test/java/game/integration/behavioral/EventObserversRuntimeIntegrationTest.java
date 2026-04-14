package game.integration.behavioral;

import game.application.state.GameSessionFactory;
import game.application.usecase.ForceCombatUseCase;
import game.application.ports.events.EventType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventObserversRuntimeIntegrationTest {

    @Test
    void sessionFactoryRegistersObserversBeforeInitialEventEmission() {
        var session = GameSessionFactory.createSessionForTheme("fire", "guerrero");

        assertEquals(1, session.observedEventCount(EventType.JUEGO_INICIADO));
        assertEquals(EventType.JUEGO_INICIADO.name(), session.lastObservedEventType());
        assertTrue(session.eventLog().stream().anyMatch(line -> line.contains("[EVT]")));
    }

    @Test
    void useCaseEventIsObservedAndUpdatesSessionState() {
        var session = GameSessionFactory.createSessionForTheme("poison", "arquero");
        int observedBefore = session.observedEventCount(EventType.COMBATE_INICIADO);

        new ForceCombatUseCase(session).execute();

        assertEquals(observedBefore + 1, session.observedEventCount(EventType.COMBATE_INICIADO));
        assertTrue(session.combatLog().stream().anyMatch(line -> line.contains("[EVT] Combate iniciado")));
    }

    @Test
    void testSessionIsolation() {
        // Reiniciar manager para asegurar aislamiento limpio
        game.infrastructure.events.observer.EventManager.getInstance().limpiar();

        var sessionA = GameSessionFactory.createSessionForTheme("fire", "guerrero");
        var sessionB = GameSessionFactory.createSessionForTheme("ice", "mago");

        // Emitir evento en sesión A
        new ForceCombatUseCase(sessionA).execute();
        
        // Verificar que A lo recibió pero B no
        assertTrue(sessionA.observedEventCount(EventType.COMBATE_INICIADO) >= 1);
        assertEquals(0, sessionB.observedEventCount(EventType.COMBATE_INICIADO), 
            "La sesión B no debería recibir eventos de la sesión A");
        
        assertTrue(sessionA.combatLog().stream().anyMatch(l -> l.contains("vs Salamandra")), 
            "Log de A mal formado o ausente");
        assertTrue(sessionB.combatLog().stream().noneMatch(l -> l.contains("vs Salamandra")), 
            "Sesión B filtró logs de sesión A");
    }

    @Test
    void testDuplicateSubscriptionPrevention() {
        var manager = game.infrastructure.events.observer.EventManager.getInstance();
        manager.limpiar();
        
        var counter = new int[]{0};
        game.application.ports.events.GameObserver obs = new game.application.ports.events.GameObserver() {
            @Override public void onEvent(game.application.ports.events.GameEvent e) { counter[0]++; }
            @Override public String getNombre() { return "Mock"; }
        };

        manager.suscribir(obs);
        manager.suscribir(obs); // Segunda suscripción accidental

        manager.notificar(new game.application.ports.events.GameEvent(game.application.ports.events.EventType.ACCION_REALIZADA)
                .agregarDato("personaje", "Test")
                .agregarDato("accion", "Test"));

        assertEquals(1, counter[0], "El observer no debería ser notificado dos veces por el mismo evento");
    }
}
