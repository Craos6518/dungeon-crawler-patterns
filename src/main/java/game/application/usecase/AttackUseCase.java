package game.application.usecase;

import game.application.dto.AttackCommandRequest;
import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;

/**
 * Caso de uso: ejecutar ataque del jugador sobre el enemigo activo.
 */
public class AttackUseCase {

    private final GameSession session;

    public AttackUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(AttackCommandRequest request) {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes atacar: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            String targetId = request == null ? null : request.targetId;
            var enemy = session.combat().currentEnemy();
            var result = session.combat().attack(targetId, session.dungeon().themeKey());

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                if (result.playerDefeated) {
                    CombatUseCaseSupport.handleDefeat(session);
                }
                return;
            }

            session.appendCombat(session.player().name() + " ataca e inflige " + result.playerDamage + " de dano.");
            CombatUseCaseSupport.appendResourceFlow(session, result);

            session.eventManager().notificar(new game.application.ports.events.GameEvent(game.application.ports.events.EventType.ATAQUE_REALIZADO)
                .agregarDato("atacante", session.player().name())
                .agregarDato("defensor", enemy.name())
                .agregarDato("danio", result.playerDamage));

            if (result.enemyDefeated) {
                CombatUseCaseSupport.handleVictory(session, enemy, result);
                return;
            }

            CombatUseCaseSupport.appendEnemyTurnEffects(session, result, enemy);

            if (result.playerDefeated) {
                CombatUseCaseSupport.handleDefeat(session);
            }

            session.combat().resolveTurn();
        });
    }
}
