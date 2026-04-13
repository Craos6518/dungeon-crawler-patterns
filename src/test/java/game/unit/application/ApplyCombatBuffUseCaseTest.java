package game.unit.application;

import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.application.usecase.ApplyCombatBuffUseCase;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;
import game.application.ports.events.GameObserver;
import game.application.ports.events.EventPublisher;
import game.domain.character.Enemy;
import game.domain.personaje.EnemigoBasico;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para el patrón Decorator en contexto de buffs de combate.
 * Valida que los buffs (que son decoradores) se aplican correctamente
 * y emiten eventos cuando se aplican exitosamente.
 */
class ApplyCombatBuffUseCaseTest {

    @Test
    void applyBuffSuccessfullyEmitsEFECTO_APLICADOEvent() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "guerrero");
        session.combat().start(new Enemy(new EnemigoBasico("Goblin", 50, 5), 5, 0, 5), false);

        // Capturador de eventos
        EventCapture capture = new EventCapture();
        session.eventManager().suscribir(capture);

        // Ejecutar use case
        ApplyCombatBuffUseCase applyCombatBuff = new ApplyCombatBuffUseCase(session);
        applyCombatBuff.execute("power");

        // Verificar que el evento fue emitido
        List<GameEvent> efectoEvents = capture.getEventsByType(EventType.EFECTO_APLICADO);
        assertEquals(1, efectoEvents.size(), "Debe emitir exactamente un evento EFECTO_APLICADO");

        GameEvent event = efectoEvents.get(0);
        assertEquals("Guerrero", event.getDato("personaje"));
        assertEquals("poder", event.getDato("efecto"));
        assertEquals(1, event.getDato("acumulaciones"));
    }

    @Test
    void applyBuffWithInsufficientResourceDoesNotEmitEvent() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "mago");
        session.combat().start(new Enemy(new EnemigoBasico("Goblin", 50, 5), 5, 0, 5), false);

        // Consumir todo el mana del mago
        while (session.player().resource() > 0) {
            session.player().spendResource(1);
        }

        EventCapture capture = new EventCapture();
        session.eventManager().suscribir(capture);

        ApplyCombatBuffUseCase applyCombatBuff = new ApplyCombatBuffUseCase(session);
        applyCombatBuff.execute("power");

        // No debe haber evento si el buff falló por recurso insuficiente
        List<GameEvent> efectoEvents = capture.getEventsByType(EventType.EFECTO_APLICADO);
        assertEquals(0, efectoEvents.size(), "No debe emitir evento si falló por recurso insuficiente");
    }

    @Test
    void applyGuardBuffEmitsEventWithCorrectBuffType() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "guerrero");
        session.combat().start(new Enemy(new EnemigoBasico("Goblin", 50, 5), 5, 0, 5), false);

        EventCapture capture = new EventCapture();
        session.eventManager().suscribir(capture);

        ApplyCombatBuffUseCase applyCombatBuff = new ApplyCombatBuffUseCase(session);
        applyCombatBuff.execute("guard");

        List<GameEvent> efectoEvents = capture.getEventsByType(EventType.EFECTO_APLICADO);
        assertEquals(1, efectoEvents.size());
        assertEquals("guardia", efectoEvents.get(0).getDato("efecto"));
    }

    /**
     * Capturador simple de eventos para testing
     */
    private static class EventCapture implements GameObserver {
        private final List<GameEvent> events = new ArrayList<>();

        @Override
        public void onEvent(GameEvent event) {
            events.add(event);
        }

        @Override
        public String getNombre() {
            return "EventCapture";
        }

        List<GameEvent> getEventsByType(EventType type) {
            return events.stream()
                    .filter(e -> e.getTipo() == type)
                    .toList();
        }
    }
}
