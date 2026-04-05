package game.unit.ui.integration;

import game.application.dto.UiCommandResponse;
import game.ui.integration.UiCommandDispatcher;
import game.ui.integration.UiGameController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiCommandDispatcherContractTest {

    @Test
    void validCommandReturnsOkResponse() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse response = dispatcher.dispatchCommandJson("{\"action\":\"openInventory\",\"payload\":{}}");

        assertEquals("ok", response.status);
        assertNotNull(response.data);
        assertEquals("inventory", response.data.screen);
    }

    @Test
    void openInventoryDuringCombatAndCloseReturnsToCombat() {
        UiCommandDispatcher dispatcher = newDispatcher();
        dispatcher.dispatchCommandJson("{\"action\":\"forceCombat\",\"payload\":{}}");

        UiCommandResponse openResponse = dispatcher.dispatchCommandJson("{\"action\":\"openInventory\",\"payload\":{}}");
        assertEquals("ok", openResponse.status);
        assertNotNull(openResponse.data);
        assertEquals("inventory", openResponse.data.screen);

        UiCommandResponse closeResponse = dispatcher.dispatchCommandJson("{\"action\":\"closeInventory\",\"payload\":{}}");
        assertEquals("ok", closeResponse.status);
        assertNotNull(closeResponse.data);
        assertEquals("combat", closeResponse.data.screen);
    }

    @Test
    void invalidCommandWithoutActionReturnsControlledError() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse response = dispatcher.dispatchCommandJson("{\"payload\":{}}");

        assertEquals("error", response.status);
        assertTrue(response.message.contains("falta campo action"));
        assertNotNull(response.data);
    }

    @Test
    void malformedJsonReturnsControlledError() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse response = dispatcher.dispatchCommandJson("{");

        assertEquals("error", response.status);
        assertTrue(response.message.contains("JSON de comando invalido"));
        assertNotNull(response.data);
    }

    @Test
    void attackWithoutTargetReturnsError() {
        UiCommandDispatcher dispatcher = newDispatcher();
        dispatcher.dispatchCommandJson("{\"action\":\"forceCombat\",\"payload\":{}}");

        UiCommandResponse response = dispatcher.dispatchCommandJson("{\"action\":\"attack\",\"payload\":{}}");

        assertEquals("error", response.status);
        assertTrue(response.message.contains("targetId es obligatorio"));
    }

    @Test
    void attackWithoutActiveCombatReturnsError() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse response = dispatcher.dispatchCommandJson(
            "{\"action\":\"attack\",\"payload\":{\"targetId\":\"current\"}}"
        );

        assertEquals("error", response.status);
        assertTrue(response.message.contains("No hay un enemigo activo"));
    }

    @Test
    void useItemWithInvalidIndexReturnsError() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse response = dispatcher.dispatchCommandJson(
            "{\"action\":\"useItem\",\"payload\":{\"itemIndex\":999}}"
        );

        assertEquals("error", response.status);
        assertTrue(response.message.contains("itemIndex fuera de rango"));
    }

    @Test
    void attackWithInvalidTargetReturnsControlledError() {
        UiCommandDispatcher dispatcher = newDispatcher();
        dispatcher.dispatchCommandJson("{\"action\":\"forceCombat\",\"payload\":{}}");

        UiCommandResponse response = dispatcher.dispatchCommandJson(
            "{\"action\":\"attack\",\"payload\":{\"targetId\":\"enemy-2\"}}"
        );

        assertEquals("error", response.status);
        assertTrue(response.message.contains("Objetivo de ataque invalido"));
    }

    @Test
    void advanceRoomDuringCombatReturnsError() {
        UiCommandDispatcher dispatcher = newDispatcher();
        dispatcher.dispatchCommandJson("{\"action\":\"forceCombat\",\"payload\":{}}");

        UiCommandResponse response = dispatcher.dispatchCommandJson("{\"action\":\"advanceRoom\",\"payload\":{}}");

        assertEquals("error", response.status);
        assertTrue(response.message.contains("combate activo"));
    }

    @Test
    void saveToSlotFromBootstrapMenuReturnsError() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse openSaves = dispatcher.dispatchCommandJson("{\"action\":\"openSaves\",\"payload\":{}}");
        assertEquals("ok", openSaves.status);
        assertNotNull(openSaves.data);
        assertEquals("saves", openSaves.data.screen);

        UiCommandResponse saveResponse = dispatcher.dispatchCommandJson(
            "{\"action\":\"saveToSlot\",\"payload\":{\"slot\":1}}"
        );

        assertEquals("error", saveResponse.status);
        assertTrue(saveResponse.message.contains("antes de iniciar o cargar"));
    }

    @Test
    void startGameWithoutHeroNameReturnsControlledError() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse response = dispatcher.dispatchCommandJson(
            "{\"action\":\"startGame\",\"payload\":{\"theme\":\"poison\",\"heroType\":\"mago\"}}"
        );

        assertEquals("error", response.status);
        assertTrue(response.message.contains("heroName es obligatorio"));
    }

    @Test
    void startGameWithBlankHeroNameReturnsControlledError() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse response = dispatcher.dispatchCommandJson(
            "{\"action\":\"startGame\",\"payload\":{\"theme\":\"poison\",\"heroType\":\"mago\",\"heroName\":\"   \"}}"
        );

        assertEquals("error", response.status);
        assertTrue(response.message.contains("heroName requerido"));
    }

    @Test
    void selectSaveSlotUpdatesRuntimeSelectionInViewModel() {
        UiCommandDispatcher dispatcher = newDispatcher();

        UiCommandResponse open = dispatcher.dispatchCommandJson("{\"action\":\"openSaves\",\"payload\":{}}");
        assertEquals("ok", open.status);

        UiCommandResponse selected = dispatcher.dispatchCommandJson(
            "{\"action\":\"selectSaveSlot\",\"payload\":{\"slot\":3}}"
        );

        assertEquals("ok", selected.status);
        assertNotNull(selected.data);
        assertNotNull(selected.data.saveSlotsInfo);
        assertEquals(3, selected.data.saveSlotsInfo.selectedSlot);
    }

    @Test
    void selectSaveSlotBelowRangeReturnsControlledError() {
        UiCommandDispatcher dispatcher = newDispatcher();
        dispatcher.dispatchCommandJson("{\"action\":\"openSaves\",\"payload\":{}}");

        UiCommandResponse response = dispatcher.dispatchCommandJson(
            "{\"action\":\"selectSaveSlot\",\"payload\":{\"slot\":0}}"
        );

        assertEquals("error", response.status);
        assertTrue(response.message.contains("slot fuera de rango permitido [1, 3]"));
    }

    @Test
    void selectSaveSlotAboveRangeReturnsControlledError() {
        UiCommandDispatcher dispatcher = newDispatcher();
        dispatcher.dispatchCommandJson("{\"action\":\"openSaves\",\"payload\":{}}");

        UiCommandResponse response = dispatcher.dispatchCommandJson(
            "{\"action\":\"selectSaveSlot\",\"payload\":{\"slot\":4}}"
        );

        assertEquals("error", response.status);
        assertTrue(response.message.contains("slot fuera de rango permitido [1, 3]"));
    }

    private static UiCommandDispatcher newDispatcher() {
        return new UiCommandDispatcher(new UiGameController(), () -> {
            // No-op en tests de contrato.
        });
    }
}
