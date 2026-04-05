package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.ui.GameViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameRuntimeHeroNamePersistenceTest {

    @Test
    void heroNamePersistsAcrossSaveAndLoad() {
        GameRuntime runtime = new GameRuntime();

        send(runtime, "heroNewGame", payload("heroType", "mago", "heroName", "Elyra", "theme", "poison"));
        send(runtime, "saveToSlot", payload("slot", 2));

        send(runtime, "heroNewGame", payload("heroType", "arquero", "heroName", "Rook", "theme", "poison"));
        assertEquals("Rook", runtime.presentViewModel().heroName);

        send(runtime, "loadFromSlot", payload("slot", 2));
        GameViewModel restored = runtime.presentViewModel();

        assertEquals("exploration", restored.screen);
        assertEquals("poison", restored.theme);
        assertEquals("Elyra", restored.heroName);
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
