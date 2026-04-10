package game.application.observer;

import game.application.state.GameSession;
import game.application.ports.events.GameEvent;
import game.application.ports.events.GameObserver;

/**
 * Observer productivo que mantiene contadores de eventos en la sesión activa.
 */
public final class SessionEventCounterObserver implements GameObserver {

    private volatile GameSession session;

    public void bindSession(GameSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(GameEvent evento) {
        GameSession target = session;
        if (target == null || evento == null) {
            return;
        }

        target.registerObservedEvent(evento.getTipo());
    }

    @Override
    public String getNombre() {
        return "SessionEventCounterObserver";
    }
}
