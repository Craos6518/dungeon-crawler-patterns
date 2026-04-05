package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.ui.GameViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameRuntimeSaveSlotSelectionTest {

    @Test
    void selectedSaveSlotIsRuntimeStateAndDrivesSaveAndLoad() {
        GameRuntime runtime = new GameRuntime();

        send(runtime, "heroNewGame", payload("heroType", "guerrero", "heroName", "Alaric", "theme", "poison"));
        send(runtime, "openSaves", new JsonObject());

        GameViewModel savesScreen = runtime.presentViewModel();
        assertEquals("saves", savesScreen.screen);
        assertNotNull(savesScreen.saveSlotsInfo);
        assertEquals(1, savesScreen.saveSlotsInfo.selectedSlot);

        send(runtime, "selectSaveSlot", payload("slot", 3));
        assertEquals(3, runtime.presentViewModel().saveSlotsInfo.selectedSlot);

        send(runtime, "openInventory", new JsonObject());
        send(runtime, "openSaves", new JsonObject());
        assertEquals(3, runtime.presentViewModel().saveSlotsInfo.selectedSlot);

        send(runtime, "saveToSlot", new JsonObject());

        send(runtime, "heroNewGame", payload("heroType", "mago", "heroName", "Borin", "theme", "poison"));
        send(runtime, "saveToSlot", payload("slot", 1));

        send(runtime, "openSaves", new JsonObject());
        send(runtime, "selectSaveSlot", payload("slot", 3));
        send(runtime, "loadFromSlot", new JsonObject());

        GameViewModel restored = runtime.presentViewModel();
        assertEquals("saves", restored.screen);
        assertEquals("Alaric", restored.heroName);
    }

    private static JsonObject payload(String key, int value) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key, value);
        return payload;
    }

    private static JsonObject payload(
        String key1,
        String val1,
        String key2,
        String val2,
        String key3,
        String val3
    ) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key1, val1);
        payload.addProperty(key2, val2);
        payload.addProperty(key3, val3);
        return payload;
    }

    private static void send(GameRuntime runtime, String action, JsonObject payload) {
        UiCommand command = new UiCommand();
        command.action = action;
        command.payload = payload;
        runtime.handleCommand(command);
    }
}
