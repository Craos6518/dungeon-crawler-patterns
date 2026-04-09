package game.application.usecase;

import game.application.state.GameFlowState;
import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.domain.inventory.Item;
import game.items.model.SimpleItem;

/**
 * Caso de uso: transacciones de tienda desde inventario (comprar/vender).
 */
public class InventoryShopUseCase {

    private static final int HEALTH_POTION_COST = 40;
    private static final int SELL_PERCENT = 60;

    private final GameSession session;

    public InventoryShopUseCase(GameSession session) {
        this.session = session;
    }

    public void buyHealthPotion() {
        UseCaseTransactionSupport.runAtomically(session, () -> {
            ensureInventoryScreen();
            if (!session.player().isAlive()) {
                throw new DomainRuleViolationException("No puedes comprar: el heroe esta derrotado.");
            }
            if (session.inventory().size() >= session.inventory().capacity()) {
                throw new DomainRuleViolationException("Inventario lleno: vende objetos antes de comprar.");
            }
            if (!session.player().spendGold(HEALTH_POTION_COST)) {
                throw new DomainRuleViolationException("Oro insuficiente para comprar una pocion de vida.");
            }

            session.inventory().add(new SimpleItem(
                "Pocion de Vida",
                "Restaura 50 HP",
                "Consumible",
                50,
                1
            ));
            session.appendEvent("Compraste Pocion de Vida por " + HEALTH_POTION_COST + " oro.");
        });
    }

    public void sellSelectedItem() {
        UseCaseTransactionSupport.runAtomically(session, () -> {
            ensureInventoryScreen();
            if (!session.player().isAlive()) {
                throw new DomainRuleViolationException("No puedes vender: el heroe esta derrotado.");
            }

            Item sold = session.inventory().removeItemAtIndex(session.inventory().selectedIndex());
            int gain = computeSellPrice(sold.getValue());
            session.player().addGold(gain);
            session.appendEvent("Vendiste " + sold.getName() + " por " + gain + " oro.");
        });
    }

    public static int healthPotionCost() {
        return HEALTH_POTION_COST;
    }

    public static int computeSellPrice(int itemValue) {
        int normalized = Math.max(0, itemValue);
        if (normalized == 0) {
            return 1;
        }
        return Math.max(1, (normalized * SELL_PERCENT) / 100);
    }

    private void ensureInventoryScreen() {
        if (session.activeState() != GameFlowState.INVENTORY) {
            throw new DomainRuleViolationException("La tienda solo esta disponible dentro del inventario.");
        }
    }
}
