package game.unit.application;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.ui.GameViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRuntimeExtendedCommandsTest {

    @Test
    void buyHealthPotionFromInventoryConsumesGoldAndAddsItem() {
        GameSession session = GameSessionFactory.createSessionForTheme("fire", "guerrero");
        session.player().addGold(120);
        GameRuntime runtime = new GameRuntime(session);

        send(runtime, "openInventory", new JsonObject());
        GameViewModel before = runtime.presentViewModel();

        send(runtime, "buyHealthPotion", new JsonObject());
        GameViewModel after = runtime.presentViewModel();

        assertEquals("inventory", after.screen);
        assertEquals(before.inventory.itemCount + 1, after.inventory.itemCount);
        assertEquals(before.gold - 40, after.gold);
        assertNotNull(after.selectedItem);
        assertTrue(after.selectedItem.name.toLowerCase().contains("poc"));
    }

    @Test
    void sellSelectedItemInInventoryRemovesItemAndAddsGold() {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("ice", "guerrero"));

        send(runtime, "openInventory", new JsonObject());
        GameViewModel before = runtime.presentViewModel();
        int selectedValue = before.selectedItem != null ? before.selectedItem.valor : 0;
        int expectedGoldGain = Math.max(1, (Math.max(0, selectedValue) * 60) / 100);

        send(runtime, "sellSelectedItem", new JsonObject());
        GameViewModel after = runtime.presentViewModel();

        assertEquals("inventory", after.screen);
        assertEquals(before.inventory.itemCount - 1, after.inventory.itemCount);
        assertEquals(before.gold + expectedGoldGain, after.gold);
    }

    @Test
    void quickSaveAndQuickLoadRestorePreferredSlotState() {
        GameRuntime runtime = new GameRuntime();

        send(runtime, "heroNewGame", payload("heroType", "mago", "heroName", "Lyria", "theme", "poison"));
        send(runtime, "quickSave", payload("slot", 2));

        send(runtime, "heroNewGame", payload("heroType", "arquero", "heroName", "Thalan", "theme", "poison"));
        GameViewModel mutated = runtime.presentViewModel();
        assertEquals("poison", mutated.theme);
        assertEquals(85, mutated.playerHpMax);

        send(runtime, "quickLoad", payload("slot", 2));
        GameViewModel restored = runtime.presentViewModel();

        assertEquals("poison", restored.theme);
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
    void healthPotionScalesHealingWithMaxHp() {
        GameSession session = GameSessionFactory.createSessionForTheme("dark", "guerrero");
        session.player().restoreProgress(6, 0, 10, 0, 0);
        GameRuntime runtime = new GameRuntime(session);

        send(runtime, "openInventory", new JsonObject());
        send(runtime, "useItem", payload("itemIndex", 0));

        GameViewModel vm = runtime.presentViewModel();
        int expectedHeal = Math.max(50, (int) Math.round(vm.playerHpMax * 0.45));
        int expectedHp = Math.min(vm.playerHpMax, 10 + expectedHeal);

        assertEquals(expectedHp, vm.playerHp);
        assertTrue(vm.eventLog.stream().anyMatch(line -> line.contains("recuperaste") && line.contains("HP")));
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

    @Test
    void styleAndBuffCommandsUpdateCombatTacticsAndResource() {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("poison", "mago"));

        send(runtime, "forceCombat", new JsonObject());
        int beforeResource = runtime.presentViewModel().resource.current;

        send(runtime, "setCombatStyle", payload("style", "aggressive"));
        send(runtime, "applyBuff", payload("type", "power"));

        GameViewModel vm = runtime.presentViewModel();
        assertNotNull(vm.combatTactics);
        assertEquals("Agresivo", vm.combatTactics.style);
        assertTrue(vm.combatTactics.offensiveBuffStacks >= 0);
        assertFalse(vm.combatTactics.defenseActive);
        assertTrue(vm.combatTactics.poisonTurns >= 0);
        assertTrue(vm.combatTactics.poisonDamage >= 0);
        assertTrue(vm.resource.current < beforeResource);
        assertTrue(vm.combatLog.stream().anyMatch(line -> line.toLowerCase().contains("buff")));
    }

    @Test
    void tacticalCheckpointRollbackRestoresPlayerHpAndConsumesCheckpoint() {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("dark", "guerrero"));

        send(runtime, "forceCombat", new JsonObject());
        send(runtime, "saveCombatCheckpoint", new JsonObject());

        GameViewModel afterSave = runtime.presentViewModel();
        int hpSaved = afterSave.playerHp;

        send(runtime, "defend", new JsonObject());
        GameViewModel afterDefend = runtime.presentViewModel();
        assertTrue(afterDefend.playerHp <= hpSaved);

        send(runtime, "rollbackCombatCheckpoint", new JsonObject());
        GameViewModel afterRollback = runtime.presentViewModel();

        assertEquals("combat", afterRollback.screen);
        assertEquals(hpSaved, afterRollback.playerHp);
        assertTrue(afterRollback.combatTactics.hasCheckpoint);
        assertTrue(afterRollback.combatTactics.checkpointConsumed);
        assertTrue(afterRollback.combatLog.stream().anyMatch(line -> line.toLowerCase().contains("checkpoint")));
    }

    @Test
    void useSkillConsumesClassResourcePool() {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("ice", "mago"));

        send(runtime, "forceCombat", new JsonObject());
        int beforeResource = runtime.presentViewModel().resource.current;

        send(runtime, "useSkill", new JsonObject());

        GameViewModel vm = runtime.presentViewModel();
        assertTrue(vm.resource.current < beforeResource);
    }

    private static void send(GameRuntime runtime, String action, JsonObject payload) {
        UiCommand command = new UiCommand();
        command.action = action;
        command.payload = payload;
        runtime.handleCommand(command);
    }

    private static JsonObject payload(String key1, String val1, String key2, String val2, String key3, String val3) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key1, val1);
        payload.addProperty(key2, val2);
        payload.addProperty(key3, val3);
        return payload;
    }

    private static JsonObject payload(String key, int value) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key, value);
        return payload;
    }

    private static JsonObject payload(String key, String value) {
        JsonObject payload = new JsonObject();
        payload.addProperty(key, value);
        return payload;
    }
}
