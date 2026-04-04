package game.unit.application;

import game.application.dto.AttackCommandRequest;
import game.application.state.GameSessionFactory;
import game.application.usecase.AttackUseCase;
import game.application.usecase.ForceCombatUseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AttackUseCaseTest {

    @Test
    void executeAttackAffectsCurrentCombatState() {
        var session = GameSessionFactory.createDemoSession();
        ForceCombatUseCase forceCombatUseCase = new ForceCombatUseCase(session);
        AttackUseCase attackUseCase = new AttackUseCase(session);

        forceCombatUseCase.execute();
        int hpBefore = session.combat().currentEnemy().hp();

        attackUseCase.execute(new AttackCommandRequest());

        boolean enemyDamaged = session.combat().isActive() && session.combat().currentEnemy().hp() < hpBefore;
        boolean enemyDefeated = !session.combat().isActive();
        assertTrue(enemyDamaged || enemyDefeated);
    }
}
