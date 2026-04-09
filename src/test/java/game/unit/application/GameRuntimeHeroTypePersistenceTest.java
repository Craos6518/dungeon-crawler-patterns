package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.ui.GameViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameRuntimeHeroTypePersistenceTest {

    @Test
    void heroTypePersistsAcrossSaveAndLoad() {
        GameRuntime runtime = new GameRuntime();

        send(runtime, "heroNewGame", payload("heroType", "mago", "theme", "poison"));
        send(runtime, "saveToSlot", payload("slot", 2));

        send(runtime, "heroNewGame", payload("heroType", "arquero", "theme", "poison"));
        assertEquals("arquero", runtime.presentViewModel().heroType);

        send(runtime, "loadFromSlot", payload("slot", 2));
        GameViewModel restored = runtime.presentViewModel();

        assertEquals("exploration", restored.screen);
        assertEquals("poison", restored.theme);
        assertEquals("mago", restored.heroType);
    }

    private static JsonObject payload(String key, int value) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key, value);
        return payload;
    }

    private static JsonObject payload(String key1, String val1, String key2, String val2) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key1, val1);
        payload.addProperty(key2, val2);
        return payload;
    }

    private static void send(GameRuntime runtime, String action, JsonObject payload) {
        UiCommand command = new UiCommand();
        command.action = action;
        command.payload = payload;
        runtime.handleCommand(command);
    }
}
