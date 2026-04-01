package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.application.state.GameSessionFactory;
import game.application.usecase.SaveGameUseCase;
import game.ui.GameViewModel;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameRuntimeLoadGameTest {

    @Test
    void loadGameRebuildsSessionUsingSavedThemeBeforeRestore() {
        var source = GameSessionFactory.createSessionForTheme("ice");
        source.dungeon().restoreProgress(
            7,
            Set.of(0, 1, 2, 3, 4, 5, 6, 7),
            Set.of(1, 2, 3, 4, 5, 6, 7)
        );
        source.setActiveScreen("exploration");
        new SaveGameUseCase(source).execute(3);

        GameRuntime runtime = new GameRuntime();
        UiCommand loadCommand = new UiCommand();
        loadCommand.action = "loadGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("slot", 3);
        loadCommand.payload = payload;

        runtime.handleCommand(loadCommand);
        GameViewModel vm = runtime.presentViewModel();

        assertNotNull(vm);
        assertEquals("exploration", vm.screen);
        assertEquals("ice", vm.theme);
        assertEquals(8, vm.room);
    }
}
