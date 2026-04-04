package game.domain.inventory;

import game.items.model.ContainerItem;
import game.items.model.ItemComponent;
import game.items.model.SimpleItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Agregado de inventario. Controla seleccion, uso y consistencia de items.
 */
public class Inventory {

    private final ContainerItem container;
    private int selectedItemIndex;

    public Inventory(ContainerItem container) {
        this.container = container;
        this.selectedItemIndex = 0;
        clampSelection();
    }

    public static Inventory demo() {
        ContainerItem mochila = new ContainerItem("Mochila", "Inventario principal", 20, 2);
        mochila.agregar(new SimpleItem("Pocion de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        mochila.agregar(new SimpleItem("Pocion de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        mochila.agregar(new SimpleItem("Antidoto", "Elimina el veneno", "Consumible", 30, 1));
        return new Inventory(mochila);
    }

    public int size() {
        return simpleItems().size();
    }

    public int capacity() {
        return container.getCapacidadMaxima();
    }

    public int selectedIndex() {
        return selectedItemIndex;
    }

    public void add(SimpleItem item) {
        container.agregar(item);
        selectedItemIndex = size() - 1;
        clampSelection();
    }

    public List<Item> items() {
        List<SimpleItem> simples = simpleItems();
        List<Item> rows = new ArrayList<>();
        for (int i = 0; i < simples.size(); i++) {
            rows.add(Item.from(simples.get(i), i));
        }
        return rows;
    }

    public Optional<Item> selectedItem() {
        clampSelection();
        if (selectedItemIndex < 0) {
            return Optional.empty();
        }
        return getByIndex(selectedItemIndex);
    }

    public Optional<Item> getByIndex(int index) {
        List<SimpleItem> simples = simpleItems();
        if (index < 0 || index >= simples.size()) {
            return Optional.empty();
        }
        return Optional.of(Item.from(simples.get(index), index));
    }

    public Optional<Item> findById(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return Optional.empty();
        }
        return items().stream().filter(i -> i.getId().equals(itemId)).findFirst();
    }

    public void select(Integer requestedIndex) {
        if (requestedIndex == null) {
            clampSelection();
            return;
        }

        if (size() == 0) {
            selectedItemIndex = -1;
            return;
        }

        selectedItemIndex = Math.max(0, Math.min(requestedIndex, size() - 1));
    }

    public void moveSelectionUp() {
        if (size() == 0) {
            selectedItemIndex = -1;
            return;
        }
        if (selectedItemIndex < 0) {
            selectedItemIndex = 0;
            return;
        }
        selectedItemIndex = Math.max(0, selectedItemIndex - 1);
    }

    public void moveSelectionDown() {
        if (size() == 0) {
            selectedItemIndex = -1;
            return;
        }
        if (selectedItemIndex < 0) {
            selectedItemIndex = 0;
            return;
        }
        selectedItemIndex = Math.min(size() - 1, selectedItemIndex + 1);
    }

    public Item useItem(String itemId) {
        Optional<Item> candidate = findById(itemId);
        if (candidate.isEmpty()) {
            candidate = selectedItem();
        }

        if (candidate.isEmpty()) {
            throw new IllegalStateException("No hay item seleccionable para usar.");
        }

        Item selected = candidate.get();
        container.remover(selected.getRaw());
        clampSelection();
        return selected;
    }

    public Item useItemAtIndex(Integer requestedIndex) {
        int index = requestedIndex == null ? selectedItemIndex : requestedIndex;
        Item selected = getByIndex(index)
            .orElseThrow(() -> new IllegalStateException("Selecciona un objeto valido para usar."));

        container.remover(selected.getRaw());
        clampSelection();
        return selected;
    }

    public boolean hasConsumable() {
        return items().stream().anyMatch(Item::isConsumable);
    }

    public boolean isSelectedConsumable() {
        return selectedItem().map(Item::isConsumable).orElse(false);
    }

    public OptionalInt firstConsumableIndex() {
        List<SimpleItem> simples = simpleItems();
        for (int i = 0; i < simples.size(); i++) {
            if (Item.from(simples.get(i), i).isConsumable()) {
                return OptionalInt.of(i);
            }
        }
        return OptionalInt.empty();
    }

    public OptionalInt selectedConsumableIndex() {
        clampSelection();

        if (selectedItemIndex >= 0) {
            Optional<Item> selected = getByIndex(selectedItemIndex);
            if (selected.isPresent() && selected.get().isConsumable()) {
                return OptionalInt.of(selectedItemIndex);
            }
        }

        return firstConsumableIndex();
    }

    public void clampSelection() {
        int size = size();
        if (size == 0) {
            selectedItemIndex = -1;
            return;
        }
        if (selectedItemIndex < 0 || selectedItemIndex >= size) {
            selectedItemIndex = 0;
        }
    }

    public List<SimpleItem> simpleItems() {
        List<SimpleItem> simples = new ArrayList<>();
        for (ItemComponent component : container.obtenerItems()) {
            if (component instanceof SimpleItem simple) {
                simples.add(simple);
            }
        }
        return simples;
    }

    public void replaceItems(List<SimpleItem> restoredItems, Integer selectedIndex) {
        List<ItemComponent> current = new ArrayList<>(container.obtenerItems());
        for (ItemComponent item : current) {
            container.remover(item);
        }

        if (restoredItems != null) {
            for (SimpleItem item : restoredItems) {
                container.agregar(item);
            }
        }

        this.selectedItemIndex = selectedIndex == null ? 0 : selectedIndex;
        clampSelection();
    }
}
