package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;

/**
 * Caso de uso: activar defensa para mitigar el siguiente ataque enemigo.
 */
public class DefendUseCase {

    private final GameSession session;

    public DefendUseCase(GameSession session) {
        this.session = session;
    }

    public void execute() {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes defender: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            var enemy = session.combat().currentEnemy();
            var result = session.combat().defend(session.dungeon().themeKey());

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                if (result.playerDefeated) {
                    CombatUseCaseSupport.handleDefeat(session);
                }
                return;
            }

            session.appendCombat("Te preparas para defender el proximo ataque.");
            CombatUseCaseSupport.appendResourceFlow(session, result);
            CombatUseCaseSupport.appendEnemyTurnEffects(session, result, enemy);

            if (result.playerDefeated) {
                CombatUseCaseSupport.handleDefeat(session);
            }

            session.combat().resolveTurn();
        });
    }
}
