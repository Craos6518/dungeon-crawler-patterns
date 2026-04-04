package game.unit.ui.integration;

import game.application.state.GameSessionFactory;
import game.ui.integration.GamePresenter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePresenterSaveSlotsEmptyTest {

    @Test
    void savesScreenShowsAllSlotsEmptyWhenNoFilesExist() {
        var session = GameSessionFactory.createDemoSession();
        session.caretaker().eliminarGuardado("Slot_1");
        session.caretaker().eliminarGuardado("Slot_2");
        session.caretaker().eliminarGuardado("Slot_3");
        session.setActiveScreen("saves");

        var vm = new GamePresenter().present(session);

        assertNotNull(vm.saveSlotsInfo);
        assertNotNull(vm.saveSlotsInfo.slots);
        assertTrue(vm.saveSlotsInfo.slots.stream().allMatch(slot -> slot.empty));
    }
}