package game.application.usecase;

import game.application.state.GameSession;
import game.application.state.GameMemento;

/**
 * Ejecuta mutaciones de use case con rollback ante excepcion.
 */
final class UseCaseTransactionSupport {

    private UseCaseTransactionSupport() {
    }

    static void runAtomically(GameSession session, Runnable action) {
        GameMemento snapshot = session.createSnapshot();
        try {
            action.run();
        } catch (RuntimeException ex) {
            try {
                session.restoreSnapshot(snapshot);
            } catch (RuntimeException restoreError) {
                ex.addSuppressed(restoreError);
            }
            throw ex;
        }
    }
}