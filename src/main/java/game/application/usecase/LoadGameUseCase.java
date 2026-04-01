package game.application.usecase;

import game.application.state.GameSession;
import game.application.state.GameSessionMementoMapper;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.persistence.memento.GameMemento;

/**
 * Caso de uso: restaurar estado completo de partida desde disco.
 */
public class LoadGameUseCase {

    private final GameSession session;

    public LoadGameUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(Integer requestedSlot) {
        int slot = requestedSlot == null ? 1 : requestedSlot;
        slot = Math.max(1, Math.min(3, slot));

        String fileName = "Slot_" + slot;
        GameMemento memento = session.caretaker().cargarDesdeDisco(fileName);

        restoreFromMemento(fileName, memento);
    }

    public void restoreFromMemento(String fileName, GameMemento memento) {
        UseCaseTransactionSupport.runAtomically(session, () -> {
            GameSessionMementoMapper.restoreStrict(session, memento);

            session.appendEvent("Partida cargada desde " + fileName + ".save");
            session.eventManager().notificar(new GameEvent(EventType.JUEGO_CARGADO)
                .agregarDato("archivo", fileName)
                .agregarDato("sala", session.dungeon().currentRoomIndex() + 1));
        });
    }
}
