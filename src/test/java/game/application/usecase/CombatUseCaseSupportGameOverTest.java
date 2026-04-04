package game.application.usecase;

import game.application.state.GameSessionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CombatUseCaseSupportGameOverTest {

    @Test
    void handleDefeatTransitionsToGameOverAndFinishesCombat() {
        var session = GameSessionFactory.createDemoSession();
        new ForceCombatUseCase(session).execute();

        CombatUseCaseSupport.handleDefeat(session);

        assertEquals("gameover", session.activeScreen());
        assertFalse(session.combat().isActive());
    }
}