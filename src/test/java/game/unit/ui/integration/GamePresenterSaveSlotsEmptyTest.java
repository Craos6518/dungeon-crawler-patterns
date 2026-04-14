package game.unit.ui.integration;

import game.application.state.GameSessionFactory;
import game.infrastructure.persistence.memento.GameCaretaker;
import game.ui.integration.GamePresenter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePresenterSaveSlotsEmptyTest {

    @Test
    void savesScreenShowsAllSlotsEmptyWhenNoFilesExist() {
        var session = GameSessionFactory.createDemoSession();
        ((GameCaretaker) session.caretaker()).eliminarGuardado("Slot_1");
        ((GameCaretaker) session.caretaker()).eliminarGuardado("Slot_2");
        ((GameCaretaker) session.caretaker()).eliminarGuardado("Slot_3");
        session.setActiveScreen("saves");

        var vm = new GamePresenter().present(session);

        assertNotNull(vm.saveSlotsInfo);
        assertNotNull(vm.saveSlotsInfo.slots);
        assertTrue(vm.saveSlotsInfo.slots.stream().allMatch(slot -> slot.empty));
    }
}