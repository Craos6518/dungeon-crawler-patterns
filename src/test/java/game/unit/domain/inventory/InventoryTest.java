package game.unit.domain.inventory;

import game.domain.inventory.Inventory;
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
}
