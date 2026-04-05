package game.ui.integration;

import game.balance.GameBalance;
import game.application.state.GameFlowState;
import game.application.state.GameSession;
import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import game.domain.inventory.Item;
import game.items.model.SimpleItem;
import game.domain.personaje.Arquero;
import game.domain.personaje.Mago;
import game.domain.personaje.Personaje;
import game.persistence.memento.GameMemento;
import game.ui.GameViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adaptador Presentacion: traduce estado de dominio a GameViewModel.
 */
public class GamePresenter {

    private static final DateTimeFormatter SAVE_TIME_FMT = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    public GameViewModel present(GameSession session) {
        return present(session, 1);
    }

    public GameViewModel present(GameSession session, int selectedSaveSlot) {
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

            if (vm.enemy != null && session.combat().isBossFight()) {
                vm.enemy.tier = "jefe";
            }
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

        GameFlowState activeState = session.activeState();
        String screen = activeState.screenKey();

        vm.screen = screen;
        vm.theme = session.dungeon().themeKey();
        vm.dungeonTheme = session.dungeon().themeName();
        vm.heroName = session.player().name();
        vm.heroType = session.heroType();
        vm.heroSelectionLocked = session.isHeroSelectionLocked();
        vm.completedThemes = new ArrayList<>(session.completedThemes());
        vm.nextCampaignTheme = session.nextCampaignTheme();
        vm.campaignThemeOrder = new ArrayList<>(session.campaignThemeOrder());

        vm.resource = new GameViewModel.ResourceInfo();
        vm.resource.type = session.player().resourceType();
        vm.resource.current = session.player().resource();
        vm.resource.max = session.player().maxResource();
        vm.resource.pct = safePct(vm.resource.current, vm.resource.max);

        vm.combatTactics = new GameViewModel.CombatTacticsInfo();
        vm.combatTactics.style = session.combat().playerStyle().displayName();
        vm.combatTactics.offensiveBuffStacks = session.combat().offensiveBuffStacks();
        vm.combatTactics.guardBuffStacks = session.combat().guardBuffStacks();
        vm.combatTactics.hasCheckpoint = session.combat().hasTacticalCheckpoint();
        vm.combatTactics.checkpointConsumed = session.combat().tacticalCheckpointConsumed();

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

        // ── Pantallas nuevas ──
        if (activeState == GameFlowState.STATS) {
            vm.stats = buildStatsInfo(session);
        }
        if (activeState == GameFlowState.SAVES) {
            vm.saveSlotsInfo = buildSaveSlotsInfo(session, selectedSaveSlot);
        }
        if (activeState == GameFlowState.GAME_OVER) {
            vm.gameOver = buildGameOverInfo(session);
        }
        if (activeState == GameFlowState.TREASURE) {
            vm.treasure = buildTreasureInfo(session);
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

    // ── Builders de las nuevas pantallas ────────────────────────────────

    private static GameViewModel.StatsInfo buildStatsInfo(GameSession session) {
        if (session.isBootstrapMenuSession()) {
            GameViewModel.StatsInfo statsFromSave = buildStatsInfoFromLatestSave(session);
            if (statsFromSave != null) {
                return statsFromSave;
            }
            return buildEmptyStatsInfo();
        }

        GameViewModel.StatsInfo st = new GameViewModel.StatsInfo();
        Personaje hero = session.player().character();
        String heroType = resolveHeroType(session, hero);

        st.heroName       = hero.getNombre();
        st.heroType       = heroType;
        st.heroHp         = hero.getVida();
        st.heroHpMax      = hero.getVidaMaxima();
        st.heroHpPct      = safePct(hero.getVida(), hero.getVidaMaxima());
        st.heroAtk        = session.player().attackStat();
        st.heroDef        = session.player().defenseStat();
        st.heroSpeed      = session.player().speedStat();
        st.goldTotal      = session.player().gold();
        st.itemsCollected = session.inventory().items().size();
        st.roomsExplored  = session.dungeon().currentRoomIndex() + 1;
        st.dungeonName    = session.dungeon().model().getNombre();
        st.enemiesDefeated = session.player().defeatedEnemies();
        return st;
    }

    private static GameViewModel.StatsInfo buildStatsInfoFromLatestSave(GameSession session) {
        GameMemento latestMemento = null;
        for (int slot = 1; slot <= 3; slot++) {
            String fileName = "Slot_" + slot;
            if (!session.caretaker().existeEnDisco(fileName)) {
                continue;
            }

            try {
                GameMemento candidate = session.caretaker().cargarDesdeDisco(fileName);
                if (latestMemento == null
                    || (candidate.getFechaGuardado() != null
                    && (latestMemento.getFechaGuardado() == null
                    || candidate.getFechaGuardado().isAfter(latestMemento.getFechaGuardado())))) {
                    latestMemento = candidate;
                }
            } catch (RuntimeException ignored) {
                // Ignora slots corruptos; si todos fallan se renderiza estado vacío.
            }
        }

        if (latestMemento == null) {
            return null;
        }
        return buildStatsInfoFromMemento(latestMemento);
    }

    private static GameViewModel.StatsInfo buildStatsInfoFromMemento(GameMemento memento) {
        GameViewModel.StatsInfo st = new GameViewModel.StatsInfo();
        Map<String, Object> charState = memento.getEstadoPersonaje();
        Map<String, Object> inventoryState = memento.getEstadoInventario();
        Map<String, Object> dungeonState = memento.getEstadoMazmorra();

        String heroType = normalizeHeroType(readString(charState.get("heroType"), ""));
        if (heroType.isBlank()) {
            heroType = inferHeroTypeFromName(memento.getNombreJugador());
        }

        st.heroName = readString(memento.getNombreJugador(), "Aventurero");
        st.heroType = heroType;
        st.heroHp = Math.max(0, readInt(charState.get("vida"), 0));
        st.heroHpMax = Math.max(0, readInt(charState.get("vidaMaxima"), st.heroHp));
        st.heroHpPct = safePct(st.heroHp, st.heroHpMax);
        GameBalance.HeroProfile profile = GameBalance.hero(heroType);
        int level = Math.max(1, readInt(charState.get("nivel"), memento.getNivelActual()));
        st.heroAtk = profile.attack() + Math.max(0, level - 1) * 2;
        st.heroDef = profile.defense() + Math.max(0, level - 1) * 2;
        st.heroSpeed = profile.speed() + Math.max(0, level - 1);
        st.goldTotal = Math.max(0, readInt(charState.get("oroAcumulado"), 0));
        st.itemsCollected = extractItemsCount(inventoryState);
        st.roomsExplored = Math.max(1, memento.getSalaActual());
        st.dungeonName = readString(dungeonState.get("tema"), "—");
        st.enemiesDefeated = Math.max(0, readInt(charState.get("enemigosDerrotados"), 0));
        return st;
    }

    private static GameViewModel.StatsInfo buildEmptyStatsInfo() {
        GameViewModel.StatsInfo st = new GameViewModel.StatsInfo();
        st.heroName = "Sin partida activa";
        st.heroType = "sin_partida";
        st.heroHp = 0;
        st.heroHpMax = 0;
        st.heroHpPct = 0;
        st.heroAtk = 0;
        st.heroDef = 0;
        st.heroSpeed = 0;
        st.goldTotal = 0;
        st.itemsCollected = 0;
        st.roomsExplored = 0;
        st.dungeonName = "—";
        st.enemiesDefeated = 0;
        return st;
    }

    private static GameViewModel.TreasureInfo buildTreasureInfo(GameSession session) {
        GameViewModel.TreasureInfo tr = new GameViewModel.TreasureInfo();
        tr.enemyDefeated  = session.treasureEnemyName().isBlank()
            ? "Enemigo derrotado en sala " + (session.dungeon().currentRoomIndex() + 1)
            : session.treasureEnemyName();
        tr.expGained      = session.treasureXpGained();
        tr.goldGained     = session.selectedTreasureGoldReward();
        tr.roomsExplored  = session.dungeon().currentRoomIndex() + 1;
        tr.enemiesDefeated = session.player().defeatedEnemies();
        tr.goldTotal      = session.player().gold();
        tr.itemsCollected = session.inventory().items().size();
        tr.hpCurrent      = session.player().character().getVida();
        tr.hpMax          = session.player().character().getVidaMaxima();
        tr.checkpointRoom = session.treasureRoomIndex() + 1;
        tr.autoSaved      = session.treasureAutoSaved();
        tr.loot           = new java.util.ArrayList<>();

        List<SimpleItem> loot = session.treasureLootOptions();
        int selectedIndex = session.selectedTreasureIndex();
        for (int i = 0; i < loot.size(); i++) {
            SimpleItem item = loot.get(i);
            GameViewModel.TreasureInfo.LootItem row = new GameViewModel.TreasureInfo.LootItem();
            row.icon = inferTreasureIcon(item);
            row.name = item.getNombre();
            row.rarity = inferTreasureRarity(item);
            row.desc = item.getDescripcion();
            row.selected = i == selectedIndex;
            tr.loot.add(row);
        }

        return tr;
    }

    private static String inferTreasureIcon(SimpleItem item) {
        String type = normalize(item.getTipo());
        String name = normalize(item.getNombre());
        if (type.contains("consum") || name.contains("poci") || name.contains("antid")) {
            return "🧪";
        }
        if (type.contains("arma")) {
            return "⚔";
        }
        if (type.contains("armadura")) {
            return "🛡";
        }
        if (type.contains("gema") || type.contains("runa")) {
            return "💎";
        }
        return "⭐";
    }

    private static String inferTreasureRarity(SimpleItem item) {
        int value = item.getValorTotal();
        if (value >= 120) {
            return "epico";
        }
        if (value >= 60) {
            return "raro";
        }
        return "comun";
    }

    private static GameViewModel.SaveSlotsInfo buildSaveSlotsInfo(GameSession session, int selectedSaveSlot) {
        GameViewModel.SaveSlotsInfo info = new GameViewModel.SaveSlotsInfo();
        info.slots = new java.util.ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            GameViewModel.SaveSlotsInfo.SlotInfo slot = new GameViewModel.SaveSlotsInfo.SlotInfo();
            slot.slot = i;
            String fileName = "Slot_" + i;

            if (!session.caretaker().existeEnDisco(fileName)) {
                applyEmptySlot(slot);
                info.slots.add(slot);
                continue;
            }

            try {
                GameMemento memento = session.caretaker().cargarDesdeDisco(fileName);
                fillSlotFromMemento(memento, slot);
            } catch (RuntimeException ex) {
                applyEmptySlot(slot);
            }

            info.slots.add(slot);
        }
        info.selectedSlot = selectedSaveSlot;
        return info;
    }

    private static GameViewModel.GameOverInfo buildGameOverInfo(GameSession session) {
        GameViewModel.GameOverInfo go = new GameViewModel.GameOverInfo();
        Personaje hero = session.player().character();
        String heroType = resolveHeroType(session, hero);
        go.heroName          = hero.getNombre();
        go.heroType          = heroType;
        go.defeatedBy        = "enemigo en la sala " + (session.dungeon().currentRoomIndex() + 1);
        go.roomsExplored     = session.dungeon().currentRoomIndex() + 1;
        go.goldGained        = session.player().gold();
        go.enemiesDefeated   = session.player().defeatedEnemies();
        go.turnsPlayed       = Math.max(session.combatLog().size(), session.eventLog().size());
        go.hasSaveToRestore  = hasValidSave(session, "Slot_1")
                               || hasValidSave(session, "Slot_2")
                               || hasValidSave(session, "Slot_3");
        return go;
    }

    private static void fillSlotFromMemento(GameMemento memento, GameViewModel.SaveSlotsInfo.SlotInfo slot) {
        Map<String, Object> charState = memento.getEstadoPersonaje();
        Map<String, Object> dungeonState = memento.getEstadoMazmorra();

        slot.empty = false;
        slot.heroName = memento.getNombreJugador();

        String heroTypeFromMemento = normalizeHeroType(readString(charState.get("heroType"), ""));
        slot.heroType = heroTypeFromMemento.isBlank()
            ? inferHeroTypeFromName(slot.heroName)
            : heroTypeFromMemento;

        slot.heroIcon = iconForHeroType(slot.heroType);
        slot.hp = readInt(charState.get("vida"), 0);
        slot.hpMax = readInt(charState.get("vidaMaxima"), 0);
        slot.roomNumber = Math.max(1, memento.getSalaActual());

        Object rawTheme = dungeonState.get("tema");
        slot.dungeonTheme = rawTheme == null ? "?" : String.valueOf(rawTheme);
        slot.saveType = "manual";
        slot.savedAt = formatSavedAt(memento.getFechaGuardado());
    }

    private static void applyEmptySlot(GameViewModel.SaveSlotsInfo.SlotInfo slot) {
        slot.empty = true;
        slot.heroIcon = "💭";
        slot.heroName = "ranura vacía";
        slot.heroType = "guerrero";
        slot.hp = 0;
        slot.hpMax = 0;
        slot.roomNumber = 0;
        slot.dungeonTheme = "?";
        slot.saveType = "manual";
        slot.savedAt = "sin guardado";
    }

    private static boolean hasValidSave(GameSession session, String fileName) {
        if (!session.caretaker().existeEnDisco(fileName)) {
            return false;
        }
        try {
            session.caretaker().cargarDesdeDisco(fileName);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private static String formatSavedAt(LocalDateTime savedAt) {
        if (savedAt == null) {
            return "fecha desconocida";
        }
        return savedAt.format(SAVE_TIME_FMT);
    }

    private static int readInt(Object raw, int fallback) {
        if (raw instanceof Number num) {
            return num.intValue();
        }
        if (raw instanceof String text) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }
        return fallback;
    }

    private static String readString(Object raw, String fallback) {
        if (raw == null) {
            return fallback;
        }
        String text = String.valueOf(raw).trim();
        return text.isBlank() ? fallback : text;
    }

    private static String resolveHeroType(GameSession session, Personaje hero) {
        String configured = normalizeHeroType(session.heroType());
        if (!configured.isBlank()) {
            return configured;
        }
        if (hero instanceof Mago) {
            return "mago";
        }
        if (hero instanceof Arquero) {
            return "arquero";
        }
        return "guerrero";
    }

    private static String normalizeHeroType(String heroType) {
        if (heroType == null) {
            return "";
        }
        String normalized = heroType.trim().toLowerCase(Locale.ROOT);
        if ("mago".equals(normalized) || "arquero".equals(normalized) || "guerrero".equals(normalized)) {
            return normalized;
        }
        return "";
    }

    private static String inferHeroTypeFromName(String heroName) {
        String normalized = normalize(heroName);
        if (normalized.contains("mago")) {
            return "mago";
        }
        if (normalized.contains("arquero")) {
            return "arquero";
        }
        return "guerrero";
    }

    private static String iconForHeroType(String heroType) {
        return switch (heroType) {
            case "mago" -> "🔮";
            case "arquero" -> "🏹";
            default -> "🗡️";
        };
    }

    private static int extractItemsCount(Map<String, Object> inventoryState) {
        if (inventoryState == null) {
            return 0;
        }
        Object items = inventoryState.get("items");
        if (items instanceof List<?> list) {
            return list.size();
        }
        return 0;
    }

    private static int safePct(int current, int max) {
        if (max <= 0) return 0;
        return Math.max(0, Math.min(100, current * 100 / max));
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
