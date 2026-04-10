package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;

/**
 * Caso de uso: guardar checkpoint táctico parcial (Memento limitado).
 */
public class SaveCombatCheckpointUseCase {

    private final GameSession session;

    public SaveCombatCheckpointUseCase(GameSession session) {
        this.session = session;
    }

    public void execute() {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes guardar checkpoint: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            var result = session.combat().saveTacticalCheckpoint();

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                return;
            }

            if (result.checkpointSaved) {
                session.appendCombat("Guardas un checkpoint tactico parcial para esta pelea.");
            }
            CombatUseCaseSupport.appendResourceFlow(session, result);

            session.eventManager().notificar(new GameEvent(EventType.ESTADO_CAMBIADO)
                .agregarDato("personaje", session.player().name())
                .agregarDato("estado", "checkpoint-tactico-guardado"));

            session.setActiveScreen("combat");
        });
    }
}
