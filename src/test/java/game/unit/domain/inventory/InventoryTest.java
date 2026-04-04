package game.unit.domain.inventory;

import game.domain.inventory.Inventory;
import game.items.model.ContainerItem;
import game.items.model.SimpleItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest {

    @Test
    void useItemRemovesElementFromInventory() {
        Inventory inventory = Inventory.demo();
        int initialSize = inventory.size();

        inventory.select(0);
        var used = inventory.useItemAtIndex(0);

        assertEquals(initialSize - 1, inventory.size());
        assertTrue(used.isConsumable());
    }

    @Test
    void sizeAndItemsIncludeNestedSimpleItemsFromCompositeTree() {
        ContainerItem backpack = new ContainerItem("Mochila", "Principal", 10, 2);
        ContainerItem pocket = new ContainerItem("Bolso", "Interno", 5, 1);
        pocket.agregar(new SimpleItem("Pocion", "Recupera vida", "Consumible", 25, 1));
        backpack.agregar(pocket);

        Inventory inventory = new Inventory(backpack);

        assertEquals(1, inventory.size());
        assertEquals("Pocion", inventory.items().get(0).getName());
    }
}
