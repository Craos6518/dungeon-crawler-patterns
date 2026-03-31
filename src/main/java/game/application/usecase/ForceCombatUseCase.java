package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;

/**
 * Caso de uso: iniciar combate forzado en la sala actual.
 */
public class ForceCombatUseCase {

    private final GameSession session;

    public ForceCombatUseCase(GameSession session) {
        this.session = session;
    }

    public void execute() {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes iniciar combate: el heroe esta derrotado.");
        }
        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.ensureCombatStarted(session, true, true);
        });
    }
}
