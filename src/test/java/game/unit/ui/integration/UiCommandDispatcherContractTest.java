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
        assertTrue(response.message.contains("Selecciona un objeto valido"));
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

    private static UiCommandDispatcher newDispatcher() {
        return new UiCommandDispatcher(new UiGameController(), () -> {
            // No-op en tests de contrato.
        });
    }
}
