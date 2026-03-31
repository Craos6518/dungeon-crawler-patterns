package game.application.usecase;

import game.application.state.GameSession;

/**
 * Caso de uso: actualizar seleccion de inventario.
 */
public class SelectInventoryItemUseCase {

    private final GameSession session;

    public SelectInventoryItemUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(Integer itemIndex) {
        session.inventory().select(itemIndex);
    }
}
