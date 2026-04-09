package game.integration.e2e;

import game.application.dto.UiCommandResponse;
import game.application.runtime.GameRuntime;
import game.ui.integration.WebGameAdapter;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebGameAdapterE2EIntegrationTest {

    @Test
    void dispatchesUiCommandAndExposesUpdatedViewModel() {
        AtomicInteger pushCount = new AtomicInteger();
        GameRuntime runtime = new GameRuntime();
        WebGameAdapter adapter = new WebGameAdapter(runtime, pushCount::incrementAndGet);

        UiCommandResponse response = adapter.dispatchAlertMessage(
            "{\"action\":\"heroNewGame\",\"payload\":{\"heroType\":\"mago\",\"theme\":\"poison\"}}"
        );

        assertEquals("ok", response.status);
        assertNotNull(response.data);
        assertEquals("exploration", response.data.screen);
        assertEquals("poison", response.data.theme);
        assertEquals("mago", response.data.heroType);

        assertEquals(1, pushCount.get());
        assertEquals("mago", adapter.presentViewModel().heroType);
    }
}
