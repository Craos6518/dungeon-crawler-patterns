package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.items.model.SimpleItem;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRuntimeDungeonTransitionResetTest {

    @Test
    void lockedCampaignStartsNextDungeonWithInheritedProgressAndFullHeal() throws Exception {
        GameSession completedSession = GameSessionFactory.createSessionForTheme("poison", "mago");
        completedSession.player().restoreProgress(3, 40, 73, 900, 12);
        completedSession.inventory().add(new SimpleItem(
            "Elixir Arcano",
            "Recupera maná y vigor",
            "Consumible",
            120,
            1
        ));
        completedSession.setHeroSelectionLocked(true);
        completedSession.markThemeCompleted("poison");
        completedSession.setActiveScreen("hero");

        GameRuntime runtime = new GameRuntime(completedSession);

        UiCommand command = new UiCommand();
        command.action = "heroNewGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "mago");
        payload.addProperty("heroName", "Mago");
        payload.addProperty("theme", "ice");
        command.payload = payload;

        runtime.handleCommand(command);

        GameSession startedSession = extractSession(runtime);

        assertEquals(3, startedSession.player().level());
        assertEquals(40, startedSession.player().experience());
        assertEquals(105, startedSession.player().maxHp());

        // Al entrar a una nueva mazmorra de campaña, el héroe conserva progreso y se cura completo.
        assertEquals(105, startedSession.player().hp());
        assertEquals(900, startedSession.player().gold());
        assertEquals(12, startedSession.player().defeatedEnemies());
        assertEquals(4, startedSession.inventory().size());
        assertTrue(startedSession.inventory().items().stream().anyMatch(i -> "Elixir Arcano".equals(i.getName())));

        assertTrue(startedSession.isThemeCompleted("poison"));
        assertTrue(startedSession.isHeroSelectionLocked());
        assertEquals("ice", startedSession.dungeon().themeKey());
        assertEquals("exploration", startedSession.activeScreen());
    }

    private static GameSession extractSession(GameRuntime runtime) throws Exception {
        Field field = GameRuntime.class.getDeclaredField("session");
        field.setAccessible(true);
        return (GameSession) field.get(runtime);
    }
}