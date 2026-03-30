package game.ui.integration;

import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import game.items.model.SimpleItem;
import game.ui.GameViewModel;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador Presentacion: traduce estado de dominio a GameViewModel.
 */
public class GamePresenter {

    public GameViewModel present(UiGameController controller) {
        Dungeon dungeon = controller.getMazmorra();
        Room room = controller.getSalaActualRoom();

        GameViewModel vm;
        if (controller.hasActiveEnemy()) {
            vm = GameViewModel.ofCombate(
                dungeon,
                controller.getHeroe(),
                controller.getEnemigoActual(),
                controller.getSalaActualIndex(),
                controller.getOroAcumulado(),
                controller.getThemeKey(),
                controller.getCombatLog()
            );
        } else {
            vm = GameViewModel.ofExploracion(
                dungeon,
                room,
                controller.getHeroe(),
                controller.getSalaActualIndex(),
                controller.getOroAcumulado(),
                controller.getThemeKey(),
                controller.getEventLog()
            );
        }

        vm.screen = controller.getPantallaActiva();
        vm.theme = controller.getThemeKey();
        vm.dungeonTheme = controller.getThemeName();

        vm.eventLog = controller.getEventLog();
        vm.combatLog = controller.getCombatLog();

        vm.roomHasEnemy = controller.isEnemyPendingInCurrentRoom();
        vm.roomHasTreasure = controller.isTreasurePendingInCurrentRoom();

        vm.minimapSymbols = controller.getMinimapSymbols();
        vm.buttons = controller.getButtonsState();

        vm.inventory = new GameViewModel.InventoryInfo();
        List<SimpleItem> items = controller.getInventoryItems();
        vm.inventory.itemCount = items.size();
        vm.inventory.maxCapacity = controller.getInventoryCapacity();

        vm.inventoryItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            SimpleItem item = items.get(i);
            GameViewModel.InventoryItemInfo row = new GameViewModel.InventoryItemInfo();
            row.index = i;
            row.id = slugify(item.getNombre()) + "-" + i;
            row.name = item.getNombre();
            row.type = item.getTipo();
            row.effect = inferEffectSummary(item);
            vm.inventoryItems.add(row);
        }

        int selectedIndex = controller.getSelectedItemIndex();
        if (!items.isEmpty() && (selectedIndex < 0 || selectedIndex >= items.size())) {
            selectedIndex = 0;
        }

        if (items.isEmpty()) {
            vm.selectedItemIndex = null;
            vm.selectedItem = null;
        } else {
            vm.selectedItemIndex = selectedIndex;
            vm.selectedItem = toItemInfo(items.get(selectedIndex));
        }

        return vm;
    }

    private static GameViewModel.ItemInfo toItemInfo(SimpleItem item) {
        GameViewModel.ItemInfo info = new GameViewModel.ItemInfo();
        info.name = item.getNombre();
        info.type = item.getTipo();
        info.desc = item.getDescripcion();
        info.effect = inferEffectSummary(item);
        info.valor = item.getValorTotal();
        info.peso = item.getPesoTotal();
        return info;
    }

    private static String inferEffectSummary(SimpleItem item) {
        String nombre = normalize(item.getNombre());
        String tipo = normalize(item.getTipo());

        if (nombre.contains("poci")) {
            return "Recupera 50 HP";
        }
        if (nombre.contains("antid")) {
            return "Remueve el veneno";
        }
        if (tipo.contains("arma")) {
            return "Botin ofensivo";
        }
        if (tipo.contains("armadura")) {
            return "Botin defensivo";
        }
        if (tipo.contains("gema") || tipo.contains("runa")) {
            return "Tesoro coleccionable";
        }

        return "Objeto util de aventura";
    }

    private static String slugify(String text) {
        String normalized = normalize(text);
        return normalized.replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
