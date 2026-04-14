package game.integration.structural;

import com.google.gson.JsonObject;
import game.application.dto.UiCommand;
import game.application.runtime.GameRuntime;
import game.application.state.GameFlowState;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.domain.character.Enemy;
import game.domain.personaje.EnemigoBasico;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacadeIntegrationTest {

    @Test
    void runtimeCommandsRouteCombatThroughFacadeAndEndWhenEnemyHpReachesZero() throws Exception {
        GameRuntime runtime = new GameRuntime(GameSessionFactory.createSessionForTheme("dark", "guerrero", 123L));
        GameSession session = extractSession(runtime);

        send(runtime, "forceCombat", new JsonObject());
        assertEquals(GameFlowState.COMBAT.screenKey(), runtime.presentViewModel().screen);
        assertTrue(session.combat().isActive());

        Enemy guaranteedEnemy = new Enemy(new EnemigoBasico("Titan", 700, 12), 12, 40, 10);
        session.combat().restoreActiveEnemy(guaranteedEnemy, false);
        assertTrue(session.combat().isActive());

        int playerHpBefore = session.player().hp();
        var enemyRef = session.combat().currentEnemy();
        int enemyHpBefore = enemyRef.hp();

        send(runtime, "attack", payload("targetId", "current"));
        send(runtime, "defend", new JsonObject());
        send(runtime, "setCombatStyle", payload("style", "aggressive"));

        int playerHpAfterThreeRounds = session.player().hp();
        int enemyHpAfterThreeRounds = enemyRef.hp();

        assertTrue(enemyHpAfterThreeRounds < enemyHpBefore, "La HP del enemigo debe reducirse tras 3 rondas.");
        assertTrue(playerHpAfterThreeRounds < playerHpBefore, "La HP del jugador debe cambiar por acciones enemigas.");

        if (!session.combat().isActive()) {
            assertEquals(0, enemyRef.hp());
            assertEquals(GameFlowState.TREASURE.screenKey(), runtime.presentViewModel().screen);
            return;
        }

        session.player().heal(9_999);
        enemyRef.receiveDamage(Math.max(0, enemyRef.hp() - 1));

        send(runtime, "attack", payload("targetId", "current"));

        assertFalse(session.combat().isActive());
        assertEquals(0, enemyRef.hp());
        assertEquals(GameFlowState.TREASURE.screenKey(), runtime.presentViewModel().screen);
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

    private static GameSession extractSession(GameRuntime runtime) throws Exception {
        Field field = GameRuntime.class.getDeclaredField("session");
        field.setAccessible(true);
        return (GameSession) field.get(runtime);
    }
}
