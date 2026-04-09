package game.integration.e2e;

import game.application.dto.UiCommandResponse;
import game.ui.integration.UiCommandDispatcher;
import game.ui.integration.UiGameController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class RuntimeFlowE2EIntegrationTest {

    @Test
    void fullFlowStartExplorationCombatSaveLoadRemainsConsistent() {
        UiCommandDispatcher dispatcher = new UiCommandDispatcher(new UiGameController(), () -> {
            // No-op: en tests no hay push visual, solo validación de contrato.
        });

        UiCommandResponse started = dispatch(
            dispatcher,
            "{\"action\":\"heroNewGame\",\"payload\":{\"heroType\":\"arquero\",\"theme\":\"poison\"}}"
        );
        assertOk(started);
        assertEquals("exploration", started.data.screen);
        assertEquals("arquero", started.data.heroType);

        UiCommandResponse combat = dispatch(dispatcher, "{\"action\":\"forceCombat\",\"payload\":{}}");
        assertOk(combat);
        assertEquals("combat", combat.data.screen);

        dispatch(dispatcher, "{\"action\":\"setCombatStyle\",\"payload\":{\"style\":\"defensive\"}}");

        UiCommandResponse afterCombat = combat;
        for (int i = 0; i < 15; i++) {
            afterCombat = dispatch(dispatcher, "{\"action\":\"retreatCombat\",\"payload\":{}}");
            assertOk(afterCombat);
            if ("exploration".equals(afterCombat.data.screen)) {
                break;
            }
            if ("gameover".equals(afterCombat.data.screen)) {
                fail("El flujo E2E llegó a gameover antes de guardar, no cumple el contrato esperado.");
            }
        }

        assertEquals("exploration", afterCombat.data.screen);

        UiCommandResponse saved = dispatch(dispatcher, "{\"action\":\"saveToSlot\",\"payload\":{\"slot\":2}}");
        assertOk(saved);

        UiCommandResponse mutated = dispatch(
            dispatcher,
            "{\"action\":\"heroNewGame\",\"payload\":{\"heroType\":\"mago\",\"theme\":\"poison\"}}"
        );
        assertOk(mutated);
        assertEquals("mago", mutated.data.heroType);

        UiCommandResponse loaded = dispatch(dispatcher, "{\"action\":\"loadFromSlot\",\"payload\":{\"slot\":2}}");
        assertOk(loaded);
        assertEquals("exploration", loaded.data.screen);
        assertEquals("poison", loaded.data.theme);
        assertEquals("arquero", loaded.data.heroType);
    }

    private static UiCommandResponse dispatch(UiCommandDispatcher dispatcher, String raw) {
        UiCommandResponse response = dispatcher.dispatchCommandJson(raw);
        assertNotNull(response);
        assertNotNull(response.data);
        return response;
    }

    private static void assertOk(UiCommandResponse response) {
        assertEquals("ok", response.status, response.message);
    }
}
