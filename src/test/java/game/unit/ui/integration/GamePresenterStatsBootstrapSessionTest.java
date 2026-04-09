package game.unit.ui.integration;

import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.ui.integration.GamePresenter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GamePresenterStatsBootstrapSessionTest {

    @Test
    void statsScreenShowsEmptyStateWhenBootstrapSessionHasNoSaves() {
        GameSession session = GameSessionFactory.createInitialMenuSession();
        clearSlots(session);
        session.setActiveScreen("stats");

        var vm = new GamePresenter().present(session);

        assertNotNull(vm.stats);
        assertEquals("sin_partida", vm.stats.heroType);
        assertEquals(0, vm.stats.heroHp);
        assertEquals(0, vm.stats.heroHpMax);
        assertEquals(0, vm.stats.roomsExplored);
        assertEquals(0, vm.stats.itemsCollected);
        assertNotNull(vm.buttons);
        assertEquals("disabled", vm.buttons.get("btn-save-slot"));
    }

    @Test
    void statsScreenUsesLatestValidSaveWhenBootstrapSessionHasSavedData() {
        GameSession source = GameSessionFactory.createSessionForTheme("fire", "mago");
        clearSlots(source);

        source.player().receiveDamage(20);
        source.player().addGold(77);
        source.player().registerDefeatedEnemy();
        source.caretaker().guardarEnDisco(source.createSnapshot(), "Slot_2");

        GameSession bootstrap = GameSessionFactory.createInitialMenuSession();
        bootstrap.setActiveScreen("stats");

        var vm = new GamePresenter().present(bootstrap);

        assertNotNull(vm.stats);
        assertEquals("mago", vm.stats.heroType);
        assertEquals(source.player().hp(), vm.stats.heroHp);
        assertEquals(source.player().gold(), vm.stats.goldTotal);
        assertEquals(source.player().defeatedEnemies(), vm.stats.enemiesDefeated);

        clearSlots(bootstrap);
    }

    private static void clearSlots(GameSession session) {
        session.caretaker().eliminarGuardado("Slot_1");
        session.caretaker().eliminarGuardado("Slot_2");
        session.caretaker().eliminarGuardado("Slot_3");
    }
}
