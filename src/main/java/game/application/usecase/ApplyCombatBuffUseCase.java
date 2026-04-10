package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;

/**
 * Caso de uso: aplicar buffs acumulativos (Decorator) durante combate.
 */
public class ApplyCombatBuffUseCase {

    private final GameSession session;

    public ApplyCombatBuffUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(String buffType) {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes potenciarte: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            var enemy = session.combat().currentEnemy();
            var result = session.combat().applyStackingBuff(buffType, session.dungeon().themeKey());

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                if (result.playerDefeated) {
                    CombatUseCaseSupport.handleDefeat(session);
                }
                return;
            }

            if (result.buffApplied) {
                session.appendCombat(
                    "Aplicas buff de " + result.buffType + " (acumulaciones: " + result.buffStacks + ")."
                );
            }
            CombatUseCaseSupport.appendResourceFlow(session, result);

            if (enemy != null) {
                CombatUseCaseSupport.appendEnemyTurnEffects(session, result, enemy);
            }

            if (result.playerDefeated) {
                CombatUseCaseSupport.handleDefeat(session);
                return;
            }

            session.eventManager().notificar(new GameEvent(EventType.EFECTO_APLICADO)
                .agregarDato("personaje", session.player().name())
                .agregarDato("efecto", result.buffType)
                .agregarDato("acumulaciones", result.buffStacks));

            session.combat().resolveTurn();
            session.setActiveScreen("combat");
        });
    }
}
