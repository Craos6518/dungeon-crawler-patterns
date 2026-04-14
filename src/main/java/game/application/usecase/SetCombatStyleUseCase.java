package game.application.usecase;

import game.application.state.GameSession;
import game.application.state.GameFlowState;
import game.domain.DomainRuleViolationException;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;

/**
 * Caso de uso: cambiar estrategia de combate del héroe en tiempo real.
 */
public class SetCombatStyleUseCase {

    private final GameSession session;

    public SetCombatStyleUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(String styleKey) {
        if (styleKey == null) {
            throw new IllegalArgumentException("Combat style cannot be null");
        }

        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes cambiar de estilo: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            var enemy = session.combat().currentEnemy();
            var result = session.combat().setCombatStyle(styleKey, session.dungeon().themeKey());

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                if (result.playerDefeated) {
                    CombatUseCaseSupport.handleDefeat(session);
                }
                return;
            }

            if (result.styleChanged) {
                session.appendCombat("Adoptas el estilo de combate: " + result.styleName + ".");
            }
            CombatUseCaseSupport.appendResourceFlow(session, result);

            if (enemy != null) {
                CombatUseCaseSupport.appendEnemyTurnEffects(session, result, enemy);
            }

            if (result.playerDefeated) {
                CombatUseCaseSupport.handleDefeat(session);
                return;
            }

            session.eventManager().notificar(new GameEvent(EventType.ACCION_REALIZADA)
                .agregarDato("personaje", session.player().name())
                .agregarDato("accion", "cambio-estilo")
                .agregarDato("estilo", result.styleName));

            session.combat().resolveTurn();
            session.transitionTo(GameFlowState.COMBAT);
        });
    }
}
