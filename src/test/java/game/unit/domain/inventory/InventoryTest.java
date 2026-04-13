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
    @Test
    void exportAndImportTreePreservesHierarchy() {
        ContainerItem root = new ContainerItem("Raiz", "Principal", 10, 2);
        ContainerItem sub = new ContainerItem("Sub", "Hijo", 5, 1);
        sub.agregar(new SimpleItem("Item1", "Desc", "Tipo", 10, 1));
        root.agregar(sub);
        root.agregar(new SimpleItem("Item2", "Desc", "Tipo", 20, 1));

        Inventory inventory = new Inventory(root);
        assertEquals(2, inventory.size());

        // Exportar
        ContainerItem exported = inventory.exportTree();
        
        // Modificar original
        inventory.add(new SimpleItem("Item3", "Desc", "Tipo", 30, 1));
        assertEquals(3, inventory.size());

        // Importar el exportado
        inventory.importTree(exported, 0);
        assertEquals(2, inventory.size(), "El tamaño debe volver a 2 tras importar");
        
        var items = inventory.exportTree().obtenerItems();
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i instanceof ContainerItem && i.getNombre().equals("Sub")));
        assertTrue(items.stream().anyMatch(i -> i.getNombre().equals("Item2")));
    }
}
