package game.application.observer;

import game.application.state.GameSession;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;
import game.application.ports.events.GameObserver;

/**
 * Observer productivo que refleja eventos al estado de sesión para la UI.
 */
public final class SessionEventFeedObserver implements GameObserver {

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

        String summary = summarize(evento);
        if (summary == null || summary.isBlank()) {
            return;
        }

        if (isCombatChannel(evento.getTipo())) {
            target.appendCombat("[EVT] " + summary);
        } else {
            target.appendEvent("[EVT] " + summary);
        }
    }

    @Override
    public String getNombre() {
        return "SessionEventFeedObserver";
    }

    private static boolean isCombatChannel(EventType type) {
        return type == EventType.COMBATE_INICIADO
            || type == EventType.ATAQUE_REALIZADO
            || type == EventType.COMBATE_FINALIZADO
            || type == EventType.EFECTO_APLICADO
            || type == EventType.ACCION_REALIZADA;
    }

    private static String summarize(GameEvent event) {
        return switch (event.getTipo()) {
            case JUEGO_INICIADO -> "Juego iniciado: "
                + stringValue(event, "heroe", "?")
                + " ("
                + stringValue(event, "tema", "?")
                + ")";
            case COMBATE_INICIADO -> "Combate iniciado: "
                + stringValue(event, "heroe", "?")
                + " vs "
                + stringValue(event, "enemigo", "?");
            case ATAQUE_REALIZADO -> stringValue(event, "atacante", "?")
                + " inflige " + stringValue(event, "danio", "0")
                + " a " + stringValue(event, "defensor", "?");
            case ACCION_REALIZADA -> stringValue(event, "personaje", "?")
                + " ejecuta " + stringValue(event, "accion", "accion");
            case EFECTO_APLICADO -> "Efecto " + stringValue(event, "efecto", "?")
                + " aplicado a " + stringValue(event, "personaje", "?");
            case COMBATE_FINALIZADO -> "Combate finalizado. Ganador: "
                + stringValue(event, "ganador", "?");
            case ITEM_RECOGIDO -> "Item recogido: " + stringValue(event, "item", "?");
            case ITEM_USADO -> stringValue(event, "usuario", "?")
                + " usa " + stringValue(event, "item", "?");
            case TESORO_ENCONTRADO -> "Tesoro encontrado: " + stringValue(event, "item", "?");
            case JUEGO_GUARDADO -> "Partida guardada en " + stringValue(event, "archivo", "?");
            case JUEGO_CARGADO -> "Partida cargada desde " + stringValue(event, "archivo", "?");
            case SALA_ENTRAR -> "Entrada a sala " + stringValue(event, "sala", "?")
                + ": " + stringValue(event, "nombre", "?");
            case SALA_COMPLETADA -> "Sala completada: " + stringValue(event, "resultado", "?");
            case JUEGO_TERMINADO -> "Juego terminado: " + stringValue(event, "resultado", "?");
            default -> null;
        };
    }

    private static String stringValue(GameEvent event, String key, String fallback) {
        Object value = event.getDato(key);
        if (value == null) {
            return fallback;
        }
        String text = String.valueOf(value);
        return text.isBlank() ? fallback : text;
    }
}
