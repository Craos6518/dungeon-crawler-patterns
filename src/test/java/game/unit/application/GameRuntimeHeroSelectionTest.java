package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.InvalidRuntimeCommandException;
import game.application.runtime.GameRuntime;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.ui.GameViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRuntimeHeroSelectionTest {

    @Test
    void heroNewGameUsesSelectedMageStats() {
        GameRuntime runtime = new GameRuntime();

        UiCommand command = new UiCommand();
        command.action = "heroNewGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "mago");
        payload.addProperty("theme", "poison");
        command.payload = payload;

        runtime.handleCommand(command);
        GameViewModel vm = runtime.presentViewModel();

        assertEquals("exploration", vm.screen);
        assertEquals(65, vm.playerHpMax);
        assertEquals(65, vm.playerHp);
        assertEquals("mago", vm.heroType);
    }

    @Test
    void heroNewGameUsesSelectedArcherStats() {
        GameRuntime runtime = new GameRuntime();

        UiCommand command = new UiCommand();
        command.action = "heroNewGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "arquero");
        payload.addProperty("theme", "poison");
        command.payload = payload;

        runtime.handleCommand(command);
        GameViewModel vm = runtime.presentViewModel();

        assertEquals("exploration", vm.screen);
        assertEquals(85, vm.playerHpMax);
        assertEquals(85, vm.playerHp);
        assertEquals("poison", vm.theme);
        assertEquals("arquero", vm.heroType);
    }

    @Test
    void selectedHeroHpPersistsWhenCombatStarts() {
        GameRuntime runtime = new GameRuntime();

        UiCommand newGame = new UiCommand();
        newGame.action = "heroNewGame";
        JsonObject newGamePayload = new JsonObject();
        newGamePayload.addProperty("heroType", "mago");
        newGamePayload.addProperty("theme", "poison");
        newGame.payload = newGamePayload;
        runtime.handleCommand(newGame);

        UiCommand forceCombat = new UiCommand();
        forceCombat.action = "forceCombat";
        forceCombat.payload = new JsonObject();
        runtime.handleCommand(forceCombat);

        GameViewModel vm = runtime.presentViewModel();
        assertEquals("combat", vm.screen);
        assertEquals(65, vm.playerHpMax);
        assertTrue(vm.playerHp <= 65);
    }

    @Test
    void lockedCampaignRejectsChangingHero() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "mago");
        session.setHeroSelectionLocked(true);
        session.markThemeCompleted("poison");
        session.setActiveScreen("hero");

        GameRuntime runtime = new GameRuntime(session);

        UiCommand command = new UiCommand();
        command.action = "selectHero";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "arquero");
        command.payload = payload;

        assertThrows(InvalidRuntimeCommandException.class, () -> runtime.handleCommand(command));
        assertEquals("mago", runtime.presentViewModel().heroType);
    }

    @Test
    void completedThemeCannotBeStartedAgain() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "mago");
        session.setHeroSelectionLocked(true);
        session.markThemeCompleted("poison");
        session.setActiveScreen("hero");

        GameRuntime runtime = new GameRuntime(session);

        UiCommand command = new UiCommand();
        command.action = "heroNewGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "mago");
        payload.addProperty("theme", "poison");
        command.payload = payload;

        assertThrows(InvalidRuntimeCommandException.class, () -> runtime.handleCommand(command));
    }

    @Test
    void lockedCampaignAllowsNewThemeButKeepsSameHero() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "mago");
        session.setHeroSelectionLocked(true);
        session.markThemeCompleted("poison");
        session.setActiveScreen("hero");

        GameRuntime runtime = new GameRuntime(session);

        UiCommand command = new UiCommand();
        command.action = "heroNewGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "mago");
        payload.addProperty("theme", "ice");
        command.payload = payload;

        runtime.handleCommand(command);
        GameViewModel vm = runtime.presentViewModel();

        assertEquals("exploration", vm.screen);
        assertEquals("ice", vm.theme);
        assertEquals(65, vm.playerHpMax);
        assertTrue(vm.completedThemes.contains("poison"));
    }

    @Test
    void campaignOrderRejectsSkippingFirstDungeon() {
        GameRuntime runtime = new GameRuntime();

        UiCommand command = new UiCommand();
        command.action = "heroNewGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "guerrero");
        payload.addProperty("theme", "fire");
        command.payload = payload;

        InvalidRuntimeCommandException ex = assertThrows(
            InvalidRuntimeCommandException.class,
            () -> runtime.handleCommand(command)
        );

        assertTrue(ex.getMessage().contains("Arachnovex"));
    }

    @Test
    void goToHeroSelectResetsLockedCampaignAndAllowsFreshHeroChoice() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "mago");
        session.setHeroSelectionLocked(true);
        session.markThemeCompleted("poison");
        session.setActiveScreen("menu");

        GameRuntime runtime = new GameRuntime(session);

        UiCommand goToHeroSelect = new UiCommand();
        goToHeroSelect.action = "goToHeroSelect";
        goToHeroSelect.payload = new JsonObject();
        runtime.handleCommand(goToHeroSelect);

        GameViewModel heroScreen = runtime.presentViewModel();
        assertEquals("hero", heroScreen.screen);
        assertFalse(heroScreen.heroSelectionLocked);
        assertTrue(heroScreen.completedThemes.isEmpty());

        UiCommand startFresh = new UiCommand();
        startFresh.action = "heroNewGame";
        JsonObject payload = new JsonObject();
        payload.addProperty("heroType", "arquero");
        payload.addProperty("theme", "poison");
        startFresh.payload = payload;
        runtime.handleCommand(startFresh);

        GameViewModel vm = runtime.presentViewModel();
        assertEquals("exploration", vm.screen);
        assertEquals("arquero", vm.heroType);
    }

    @Test
    void newGameFromGameOverAlsoResetsLockedCampaign() {
        GameSession session = GameSessionFactory.createSessionForTheme("poison", "guerrero");
        session.setHeroSelectionLocked(true);
        session.markThemeCompleted("poison");
        session.setActiveScreen("gameover");

        GameRuntime runtime = new GameRuntime(session);

        UiCommand newGame = new UiCommand();
        newGame.action = "newGame";
        newGame.payload = new JsonObject();
        runtime.handleCommand(newGame);

        GameViewModel heroScreen = runtime.presentViewModel();
        assertEquals("hero", heroScreen.screen);
        assertFalse(heroScreen.heroSelectionLocked);
        assertTrue(heroScreen.completedThemes.isEmpty());
    }
}
