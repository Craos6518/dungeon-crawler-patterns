package game.ui.integration;

import game.application.state.GameSession;
import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import game.domain.inventory.Item;
import game.ui.GameViewModel;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador Presentacion: traduce estado de dominio a GameViewModel.
 */
public class GamePresenter {

    public GameViewModel present(GameSession session) {
        Dungeon dungeon = session.dungeon().model();
        Room room = session.dungeon().currentRoom().model();

        GameViewModel vm;
        if (session.hasActiveEnemy()) {
            vm = GameViewModel.ofCombate(
                dungeon,
                session.player().character(),
                session.combat().currentEnemy().character(),
                session.dungeon().currentRoomIndex(),
                session.player().gold(),
                session.dungeon().themeKey(),
                session.combatLog()
            );
        } else {
            vm = GameViewModel.ofExploracion(
                dungeon,
                room,
                session.player().character(),
                session.dungeon().currentRoomIndex(),
                session.player().gold(),
                session.dungeon().themeKey(),
                session.eventLog()
            );
        }

        vm.screen = session.activeScreen();
        vm.theme = session.dungeon().themeKey();
        vm.dungeonTheme = session.dungeon().themeName();

        vm.eventLog = session.eventLog();
        vm.combatLog = session.combatLog();

        vm.roomHasEnemy = session.isEnemyPendingInCurrentRoom();
        vm.roomHasTreasure = session.isTreasurePendingInCurrentRoom();

        vm.minimapSymbols = session.dungeon().minimapSymbols();
        vm.buttons = session.buttonsState();

        vm.inventory = new GameViewModel.InventoryInfo();
        List<Item> items = session.inventory().items();
        vm.inventory.itemCount = items.size();
        vm.inventory.maxCapacity = session.inventory().capacity();

        vm.inventoryItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            GameViewModel.InventoryItemInfo row = new GameViewModel.InventoryItemInfo();
            row.index = i;
            row.id = item.getId();
            row.name = item.getName();
            row.type = item.getType();
            row.effect = inferEffectSummary(item);
            vm.inventoryItems.add(row);
        }

        int selectedIndex = session.inventory().selectedIndex();
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

    private static GameViewModel.ItemInfo toItemInfo(Item item) {
        GameViewModel.ItemInfo info = new GameViewModel.ItemInfo();
        info.name = item.getName();
        info.type = item.getType();
        info.desc = item.getDescription();
        info.effect = inferEffectSummary(item);
        info.valor = item.getValue();
        info.peso = item.getWeight();
        return info;
    }

    private static String inferEffectSummary(Item item) {
        String nombre = normalize(item.getName());
        String tipo = normalize(item.getType());

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

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
