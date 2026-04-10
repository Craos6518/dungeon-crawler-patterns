package game.application.usecase;

import game.application.dto.UseItemCommandRequest;
import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;

/**
 * Caso de uso: usar un item del inventario sobre el heroe.
 */
public class UseItemUseCase {

    private final GameSession session;

    public UseItemUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(UseItemCommandRequest request) {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes usar objetos: el heroe esta derrotado.");
        }

        Integer itemIndex = request == null ? null : request.itemIndex;
        String itemId = request == null ? null : request.itemId;

        UseCaseTransactionSupport.runAtomically(session, () -> {
            var item = consumeItem(itemIndex, itemId);
            var enemy = session.combat().currentEnemy();
            var result = session.combat().useItem(item, session.dungeon().themeKey());

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                if (result.playerDefeated) {
                    CombatUseCaseSupport.handleDefeat(session);
                }
                return;
            }

            if (result.potionUsed) {
                session.appendEvent("Usaste " + item.getName() + " y recuperaste " + result.healedHp + " HP.");
                session.appendCombat("Consumiste una pocion y recuperaste " + result.healedHp + " HP.");
            }

            if (result.antidoteUsed) {
                session.appendEvent("Antidoto aplicado. El veneno fue removido.");
                session.appendCombat("Estado alterado eliminado: veneno.");
            }

            CombatUseCaseSupport.appendResourceFlow(session, result);

            session.eventManager().notificar(new game.application.ports.events.GameEvent(game.application.ports.events.EventType.ITEM_USADO)
                .agregarDato("usuario", session.player().name())
                .agregarDato("item", item.getName()));

            if (enemy != null) {
                CombatUseCaseSupport.appendEnemyTurnEffects(session, result, enemy);
            }

            if (result.playerDefeated) {
                CombatUseCaseSupport.handleDefeat(session);
            }

            session.combat().resolveTurn();
        });
    }

    private game.domain.inventory.Item consumeItem(Integer itemIndex, String itemId) {
        try {
            return itemIndex != null
                ? session.inventory().useItemAtIndex(itemIndex)
                : session.inventory().useItem(itemId);
        } catch (IllegalStateException ex) {
            throw new DomainRuleViolationException(ex.getMessage(), ex);
        }
    }
}
