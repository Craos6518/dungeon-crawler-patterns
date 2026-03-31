package game.application.usecase;

import game.application.state.GameSession;

/**
 * Caso de uso: mover cursor de inventario.
 */
public class MoveInventorySelectionUseCase {

    private final GameSession session;

    public MoveInventorySelectionUseCase(GameSession session) {
        this.session = session;
    }

    public void moveUp() {
        session.inventory().moveSelectionUp();
    }

    public void moveDown() {
        session.inventory().moveSelectionDown();
    }
}
