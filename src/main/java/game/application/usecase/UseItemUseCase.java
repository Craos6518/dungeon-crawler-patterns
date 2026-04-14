package game.application.usecase;

import game.application.dto.UseItemCommandRequest;
import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.domain.combat.CombatResult;
import game.domain.inventory.Item;
import game.patterns.command.actions.UseItemCommand;

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
            String resolvedItemId = resolveItemId(itemIndex, itemId);
            var enemy = session.combat().currentEnemy();

            Item item;
            CombatResult result;
            try {
                UseItemCommand useItemCommand = new UseItemCommand(
                    session.inventory(),
                    resolvedItemId,
                    session.combat(),
                    session.dungeon().themeKey()
                );
                session.combat().executeCommand(useItemCommand);

                item = useItemCommand.getConsumedItem();
                result = useItemCommand.getCombatResult();
            } catch (IllegalStateException ex) {
                throw new DomainRuleViolationException(ex.getMessage(), ex);
            }

            if (item == null || result == null) {
                throw new DomainRuleViolationException("No se pudo ejecutar el uso de item en combate.");
            }

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

    private String resolveItemId(Integer itemIndex, String itemId) {
        if (itemIndex == null) {
            return itemId;
        }

        return session.inventory().getByIndex(itemIndex)
            .map(Item::getId)
            .orElseThrow(() -> new DomainRuleViolationException("Selecciona un objeto valido para usar."));
    }
}
