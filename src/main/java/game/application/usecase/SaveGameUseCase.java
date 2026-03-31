package game.application.usecase;

import game.application.state.GameSession;
import game.application.state.GameSessionMementoMapper;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.persistence.memento.GameMemento;

/**
 * Caso de uso: persistir estado de partida.
 */
public class SaveGameUseCase {

    private final GameSession session;

    public SaveGameUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(Integer requestedSlot) {
        int slot = requestedSlot == null ? 1 : requestedSlot;
        slot = Math.max(1, Math.min(3, slot));

        session.assertStableForSave();

        String fileName = "Slot_" + slot;
        GameMemento memento = GameSessionMementoMapper.toMemento(session);

        session.caretaker().guardarEnDisco(memento, fileName);
        session.caretaker().guardarEnMemoria(memento);

        session.appendEvent("Partida guardada en " + fileName + ".save");
        session.eventManager().notificar(new GameEvent(EventType.JUEGO_GUARDADO)
            .agregarDato("archivo", fileName)
            .agregarDato("sala", session.dungeon().currentRoomIndex() + 1));
    }
}
