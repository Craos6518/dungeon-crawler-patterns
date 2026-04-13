package game.application.runtime;

import game.application.state.GameFlowState;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;

/**
 * Orquesta el ciclo de vida de sesiones para el runtime productivo.
 *
 * Responsabilidad:
 * - Resolver sesion inicial cuando el runtime arranca.
 * - Guiar la secuencia de flujo hacia exploracion al iniciar una partida.
 */
final class GameRuntimeCoordinator {

    GameSession resolveInitialSession(GameSession providedSession) {
        if (providedSession != null) {
            return providedSession;
        }
        return GameSessionFactory.createInitialMenuSession();
    }

    GameSession orchestrateSessionToExploration(GameSession session) {
        if (session == null) {
            throw new InvalidRuntimeCommandException("No existe sesion para orquestar.");
        }

        GameFlowState current = session.activeState();
        if (current == GameFlowState.EXPLORATION) {
            return session;
        }

        if (current == GameFlowState.MENU) {
            session.transitionTo(GameFlowState.HERO);
        }

        session.transitionTo(GameFlowState.EXPLORATION);
        return session;
    }
}
