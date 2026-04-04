package game.unit.application;

import game.application.dto.AttackCommandRequest;
import game.application.state.GameSessionFactory;
import game.application.usecase.AttackUseCase;
import game.application.usecase.ForceCombatUseCase;
import game.application.usecase.RetreatCombatUseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatResultFlowTest {

    @Test
    void lethalAttackEndsCombatAndReturnsToExploration() {
        var session = GameSessionFactory.createSessionForTheme("fire", "guerrero");
        new ForceCombatUseCase(session).execute();

        int enemyHp = session.combat().currentEnemy().hp();
        session.combat().currentEnemy().receiveDamage(enemyHp - 1);

        AttackCommandRequest request = new AttackCommandRequest();
        request.targetId = "current";
        new AttackUseCase(session).execute(request);

        assertFalse(session.combat().isActive());
        assertEquals("exploration", session.activeScreen());
        assertTrue(session.combatLog().stream().anyMatch(line -> line.contains("Derrotaste")));
    }

    @Test
    void retreatAttemptProducesExpectedCombatOutcomeLog() {
        var session = GameSessionFactory.createSessionForTheme("poison", "arquero");
        new ForceCombatUseCase(session).execute();

        new RetreatCombatUseCase(session).execute();

        assertTrue("combat".equals(session.activeScreen())
            || "exploration".equals(session.activeScreen())
            || "gameover".equals(session.activeScreen()));
        assertTrue(session.combatLog().stream().anyMatch(line -> line.toLowerCase().contains("retir")));
    }
}
