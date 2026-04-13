package game.patterns.command.actions;

import game.domain.combat.Combat;
import game.domain.combat.CombatResult;
import game.domain.inventory.Inventory;
import game.domain.inventory.Item;
import game.patterns.combat.facade.CombatFacade;

/**
 * Command concreto para consumir un item de inventario y aplicarlo en combate.
 */
public class UseItemCommand implements Command {
    @FunctionalInterface
    private interface ItemEffectApplier {
        CombatResult apply(Item item, String themeKey);
    }

    private final Inventory inventory;
    private final String itemId;
    private final ItemEffectApplier itemEffectApplier;
    private final String themeKey;

    private boolean executed;
    private Item consumedItem;
    private CombatResult combatResult;

    public UseItemCommand(Inventory inventory, String itemId) {
        this(inventory, itemId, (ItemEffectApplier) null, null);
    }

    public UseItemCommand(Inventory inventory, String itemId, Combat combat, String themeKey) {
        this(inventory, itemId, combat == null ? null : combat::useItem, themeKey);
    }

    public UseItemCommand(Inventory inventory, String itemId, CombatFacade combatFacade, String themeKey) {
        this(inventory, itemId, combatFacade == null ? null : combatFacade::useItem, themeKey);
    }

    private UseItemCommand(Inventory inventory, String itemId, ItemEffectApplier itemEffectApplier, String themeKey) {
        this.inventory = inventory;
        this.itemId = itemId;
        this.itemEffectApplier = itemEffectApplier;
        this.themeKey = themeKey;
        this.executed = false;
        this.consumedItem = null;
        this.combatResult = null;
    }

    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException("No se puede usar el item");
        }

        consumedItem = inventory.useItem(itemId);
        if (itemEffectApplier != null) {
            combatResult = itemEffectApplier.apply(consumedItem, themeKey);
        }
        executed = true;
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException(
            "UseItemCommand no soporta undo: consumir items y resolver efectos no es reversible de forma segura"
        );
    }

    @Override
    public boolean canExecute() {
        if (executed || inventory == null) {
            return false;
        }

        if (itemId == null || itemId.isBlank()) {
            return inventory.selectedItem().isPresent();
        }

        return inventory.findById(itemId).isPresent() || inventory.selectedItem().isPresent();
    }

    @Override
    public String getDescription() {
        String target = (itemId == null || itemId.isBlank()) ? "item seleccionado" : itemId;
        return "Usar item de inventario: " + target;
    }

    public Item getConsumedItem() {
        return consumedItem;
    }

    public CombatResult getCombatResult() {
        return combatResult;
    }

    public boolean isExecuted() {
        return executed;
    }
}
