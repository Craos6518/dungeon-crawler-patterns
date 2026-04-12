package game.integration.behavioral;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.application.state.GameFlowState;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRuntimeStateFlowIntegrationTest {

    @Test
    void runtimeTransitionsExplorationCombatAndPostCombatUsingStateContext() throws Exception {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("poison", "guerrero"));

        assertEquals(GameFlowState.EXPLORATION.screenKey(), runtime.presentViewModel().screen);
        int inventoryBefore = runtime.presentViewModel().inventory.itemCount;

        send(runtime, "forceCombat", new JsonObject());
        assertEquals(GameFlowState.COMBAT.screenKey(), runtime.presentViewModel().screen);

        GameSession session = extractSession(runtime);
        assertEquals(GameFlowState.COMBAT, session.activeState());
        assertTrue(session.combat().isActive());

        session.player().heal(9_999);
        int enemyHp = session.combat().currentEnemy().hp();
        session.combat().currentEnemy().receiveDamage(Math.max(0, enemyHp - 1));

        send(runtime, "attack", payload("targetId", "current"));

        var treasureVm = runtime.presentViewModel();
        assertEquals(GameFlowState.TREASURE.screenKey(), treasureVm.screen);
        assertNotNull(treasureVm.treasure);
        assertNotNull(treasureVm.treasure.loot);
        assertFalse(treasureVm.treasure.loot.isEmpty());
        assertEquals(GameFlowState.TREASURE, session.activeState());

        send(runtime, "selectLoot", payload("lootIndex", 0));
        send(runtime, "takeLoot", new JsonObject());

        var afterLootVm = runtime.presentViewModel();
        assertEquals(GameFlowState.EXPLORATION.screenKey(), afterLootVm.screen);
        assertEquals(GameFlowState.EXPLORATION, session.activeState());
        assertEquals(inventoryBefore + 1, afterLootVm.inventory.itemCount);

        assertFalse(session.combat().isActive());
    }

    @Test
    void bossTreasureTransitionsToHeroVictoryFlow() throws Exception {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("dark", "guerrero"));
        GameSession session = extractSession(runtime);

        int bossRoom = session.dungeon().totalRooms() - 1;
        session.dungeon().restoreProgress(bossRoom, java.util.Set.of(), java.util.Set.of());

        send(runtime, "forceCombat", new JsonObject());
        assertTrue(session.combat().isBossFight());

        session.player().heal(9_999);
        int enemyHp = session.combat().currentEnemy().hp();
        session.combat().currentEnemy().receiveDamage(Math.max(0, enemyHp - 1));

        send(runtime, "attack", payload("targetId", "current"));
        assertEquals(GameFlowState.TREASURE.screenKey(), runtime.presentViewModel().screen);

        send(runtime, "takeLoot", new JsonObject());

        assertEquals(GameFlowState.HERO.screenKey(), runtime.presentViewModel().screen);
        assertEquals(GameFlowState.HERO, session.activeState());
        assertTrue(session.isThemeCompleted("dark"));
        assertTrue(session.isHeroSelectionLocked());
    }

    @Test
    void bossTreasureTransitionsToMenuWhenCampaignIsFullyCompleted() throws Exception {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("dark", "guerrero"));
        GameSession session = extractSession(runtime);

        session.replaceCompletedThemes(Set.of("poison", "ice", "fire"));
        session.setHeroSelectionLocked(true);

        int bossRoom = session.dungeon().totalRooms() - 1;
        session.dungeon().restoreProgress(bossRoom, Set.of(), Set.of());

        send(runtime, "forceCombat", new JsonObject());
        assertTrue(session.combat().isBossFight());

        session.player().heal(9_999);
        int enemyHp = session.combat().currentEnemy().hp();
        session.combat().currentEnemy().receiveDamage(Math.max(0, enemyHp - 1));

        send(runtime, "attack", payload("targetId", "current"));
        assertEquals(GameFlowState.TREASURE.screenKey(), runtime.presentViewModel().screen);

        send(runtime, "takeLoot", new JsonObject());

        assertEquals(GameFlowState.MENU.screenKey(), runtime.presentViewModel().screen);
        assertEquals(GameFlowState.MENU, session.activeState());
        assertTrue(session.isThemeCompleted("dark"));
        assertEquals("", session.nextCampaignTheme());
    }

    private static void send(GameRuntime runtime, String action, JsonObject payload) {
        UiCommand command = new UiCommand();
        command.action = action;
        command.payload = payload;
        runtime.handleCommand(command);
    }

    private static JsonObject payload(String key, String value) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key, value);
        return payload;
    }

    private static JsonObject payload(String key, int value) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key, value);
        return payload;
    }

    private static GameSession extractSession(GameRuntime runtime) throws Exception {
        Field field = GameRuntime.class.getDeclaredField("session");
        field.setAccessible(true);
        return (GameSession) field.get(runtime);
    }
}
