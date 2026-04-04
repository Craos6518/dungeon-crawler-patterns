package game.integration.behavioral;

import game.application.state.GameSessionFactory;
import game.application.usecase.ForceCombatUseCase;
import game.events.observer.EventType;
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
}
