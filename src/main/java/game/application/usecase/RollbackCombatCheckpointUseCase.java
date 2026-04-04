package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.events.observer.EventType;
import game.events.observer.GameEvent;

/**
 * Caso de uso: restaurar checkpoint táctico parcial (Memento limitado de un uso).
 */
public class RollbackCombatCheckpointUseCase {

    private final GameSession session;

    public RollbackCombatCheckpointUseCase(GameSession session) {
        this.session = session;
    }

    public void execute() {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes restaurar checkpoint: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            var result = session.combat().rollbackTacticalCheckpoint();

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                return;
            }

            if (result.checkpointRolledBack) {
                session.appendCombat("Restauras tu checkpoint tactico y reviertes el estado del duelo.");
            }
            CombatUseCaseSupport.appendResourceFlow(session, result);

            session.eventManager().notificar(new GameEvent(EventType.ESTADO_CAMBIADO)
                .agregarDato("personaje", session.player().name())
                .agregarDato("estado", "checkpoint-tactico-restaurado"));

            session.setActiveScreen("combat");
        });
    }
}
