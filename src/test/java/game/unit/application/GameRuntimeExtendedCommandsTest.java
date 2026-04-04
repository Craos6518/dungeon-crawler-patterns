package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.application.state.GameSessionFactory;
import game.ui.GameViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRuntimeExtendedCommandsTest {

    @Test
    void quickSaveAndQuickLoadRestorePreferredSlotState() {
        GameRuntime runtime = new GameRuntime();

        send(runtime, "heroNewGame", payload("heroType", "mago", "theme", "fire"));
        send(runtime, "quickSave", payload("slot", 2));

        send(runtime, "heroNewGame", payload("heroType", "arquero", "theme", "dark"));
        GameViewModel mutated = runtime.presentViewModel();
        assertEquals("dark", mutated.theme);
        assertEquals(85, mutated.playerHpMax);

        send(runtime, "quickLoad", payload("slot", 2));
        GameViewModel restored = runtime.presentViewModel();

        assertEquals("fire", restored.theme);
        assertEquals(65, restored.playerHpMax);
        assertEquals("exploration", restored.screen);
    }

    @Test
    void consumeSelectedItemRemovesConsumableFromInventory() {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("fire", "guerrero"));

        send(runtime, "openInventory", new JsonObject());
        int countBefore = runtime.presentViewModel().inventory.itemCount;

        send(runtime, "consumeSelectedItem", new JsonObject());
        GameViewModel vm = runtime.presentViewModel();

        assertEquals("inventory", vm.screen);
        assertEquals(countBefore - 1, vm.inventory.itemCount);
    }

    @Test
    void inventoryAliasesMoveSelectionUpAndDown() {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("ice", "guerrero"));

        send(runtime, "openInventory", new JsonObject());
        assertEquals(0, runtime.presentViewModel().selectedItemIndex);

        send(runtime, "inventoryNext", new JsonObject());
        assertEquals(1, runtime.presentViewModel().selectedItemIndex);

        send(runtime, "inventoryPrevious", new JsonObject());
        assertEquals(0, runtime.presentViewModel().selectedItemIndex);
    }

    @Test
    void retreatCombatProducesConsistentStateAndLogs() {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("poison", "arquero"));

        send(runtime, "forceCombat", new JsonObject());

        for (int i = 0; i < 4 && "combat".equals(runtime.presentViewModel().screen); i++) {
            send(runtime, "retreatCombat", new JsonObject());
        }

        GameViewModel vm = runtime.presentViewModel();
        assertTrue("combat".equals(vm.screen) || "exploration".equals(vm.screen) || "gameover".equals(vm.screen));
        assertNotNull(vm.combatLog);
        assertTrue(vm.combatLog.stream().anyMatch(line -> line.toLowerCase().contains("retir")));
    }

    private static void send(GameRuntime runtime, String action, JsonObject payload) {
        UiCommand command = new UiCommand();
        command.action = action;
        command.payload = payload;
        runtime.handleCommand(command);
    }

    private static JsonObject payload(String key1, String val1, String key2, String val2) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key1, val1);
        payload.addProperty(key2, val2);
        return payload;
    }

    private static JsonObject payload(String key, int value) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key, value);
        return payload;
    }
}
