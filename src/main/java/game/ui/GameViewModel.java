package game.ui;

import java.util.List;
import java.util.Map;

/**
 * ViewModel que Java serializa a JSON y envía a la UI HTML vía:
 *   engine.executeScript("window.updateGameState(" + gson.toJson(vm) + ")")
 *
 * Campos alineados con los IDs documentados en game.html (data-bind).
 * Regla: la UI SOLO renderiza — nunca muta estado local con datos del modelo.
 */
public class GameViewModel {

    // ── Pantalla activa ──────────────────────────────────────────
    /** "menu" | "exploration" | "combat" | "inventory" */
    public String screen;

    // ── Header compartido (3 pantallas) ──────────────────────────
    public String dungeonName;
    public String dungeonTheme;
    public int    room;
    public int    totalRooms;
    public int    playerHp;
    public int    playerHpMax;
    public int    playerHpPct;   // 0–100 para width del fill
    public ResourceInfo resource;
    public int    gold;

    // ── Tema visual ───────────────────────────────────────────────
    /** "fire" | "ice" | "poison" | "dark" */
    public String theme;

    // ── Progresión de campaña (pantalla héroe) ──────────────────
    /** Héroe actualmente seleccionado para campaña. */
    public String heroType;
    /** True cuando el héroe queda bloqueado para mantener continuidad de campaña. */
    public boolean heroSelectionLocked;
    /** Temas ya conquistados que no pueden volver a iniciarse en la campaña actual. */
    public List<String> completedThemes;
    /** Siguiente tema desbloqueado segun la progresion de campana. */
    public String nextCampaignTheme;
    /** Orden canonico de la campana para guiar UI y validaciones. */
    public List<String> campaignThemeOrder;

    // ── Pantalla Exploración ──────────────────────────────────────
    public String  roomName;
    public String  roomDesc;
    public String  roomDifficulty;
    public boolean roomHasTreasure;
    public boolean roomHasEnemy;
    public List<String> eventLog;
    /** "cleared" | "current" | "unknown" | "boss" */
    public List<String> minimapSymbols;

    // ── Pantalla Combate ──────────────────────────────────────────
    public EnemyInfo    enemy;
    public CombatTacticsInfo combatTactics;
    public List<String> combatLog;

    // ── Pantalla Inventario ───────────────────────────────────────
    public InventoryInfo inventory;
    public List<InventoryItemInfo> inventoryItems;
    public ItemInfo      selectedItem;
    public Integer       selectedItemIndex;

    // ── Pantalla Estadísticas ─────────────────────────────────────
    public StatsInfo stats;

    // ── Pantalla Sala de Tesoro ───────────────────────────────────
    public TreasureInfo treasure;

    // ── Pantalla Slots de Guardado ────────────────────────────────
    public SaveSlotsInfo saveSlotsInfo;

    // ── Pantalla Game Over ────────────────────────────────────────
    public GameOverInfo gameOver;

    // ── Estados de botones ────────────────────────────────────────
    /**
     * Mapa  id-del-botón → estado ("default" | "disabled" | "cooldown").
     * Ejemplo: buttons.put("btn-habilidad", "cooldown")
     */
    public Map<String, String> buttons;

    // ── Sub-objetos ───────────────────────────────────────────────

    public static class EnemyInfo {
        public String name;
        public int    hp;
        public int    hpMax;
        public int    hpPct;   // 0–100
        /** "menor" | "elite" | "jefe" */
        public String tier;
    }

    public static class ResourceInfo {
        public String type;
        public int current;
        public int max;
        public int pct;
    }

    public static class CombatTacticsInfo {
        public String style;
        public int offensiveBuffStacks;
        public int guardBuffStacks;
        public boolean defenseActive;
        public int poisonTurns;
        public int poisonDamage;
        public boolean hasCheckpoint;
        public boolean checkpointConsumed;
    }

    public static class InventoryInfo {
        public int itemCount;
        public int maxCapacity;
    }

    public static class InventoryItemInfo {
        public int    index;
        public String id;
        public String name;
        public String type;
        public String effect;
    }

    public static class ItemInfo {
        public String name;
        /** "consumible" | "arma" | "armadura" | "gema" | "runa" */
        public String type;
        public String desc;
        public String effect;
        public int    valor;   // item.getValorTotal()
        public double peso;    // item.getPesoTotal()
    }

    // ── Estadísticas del héroe y partida ──────────────────────────
    public static class StatsInfo {
        /** "guerrero" | "mago" | "arquero" */
        public String heroType;
        public int heroHp;
        public int heroHpMax;
        public int heroHpPct;
        public int heroAtk;
        public int heroDef;
        public int heroSpeed;
        public int roomsExplored;
        public int enemiesDefeated;
        public int goldTotal;
        public int itemsCollected;
        public String dungeonName;
    }

    // ── Sala de Tesoro post-victoria ──────────────────────────────
    public static class TreasureInfo {
        public String enemyDefeated;
        public int    expGained;
        public int    goldGained;
        public List<LootItem> loot;
        public int    roomsExplored;
        public int    enemiesDefeated;
        public int    goldTotal;
        public int    itemsCollected;
        public int    hpCurrent;
        public int    hpMax;
        public int    checkpointRoom;
        public boolean autoSaved;

        public static class LootItem {
            public String icon;
            public String name;
            /** "raro" | "comun" | "epico" */
            public String rarity;
            public String desc;
            public boolean selected;
        }
    }

    // ── Ranuras de guardado con metadatos ─────────────────────────
    public static class SaveSlotsInfo {
        public List<SlotInfo> slots;
        public int selectedSlot;

        public static class SlotInfo {
            public int     slot;
            public boolean empty;
            public String  heroIcon;
            public String  heroLabel;
            public String  heroType;
            public int     hp;
            public int     hpMax;
            public int     roomNumber;
            public String  dungeonTheme;
            /** "auto" | "manual" */
            public String  saveType;
            public String  savedAt;
        }
    }

    // ── Game Over ─────────────────────────────────────────────────
    public static class GameOverInfo {
        public String heroType;
        public String defeatedBy;
        public int    roomsExplored;
        public int    enemiesDefeated;
        public int    goldGained;
        public int    turnsPlayed;
        public boolean hasSaveToRestore;
    }

    // ── Fábrica estática de conveniencia ─────────────────────────

    /** Crea un ViewModel con los datos de exploración desde los modelos del juego. */
    public static GameViewModel ofExploracion(
            game.dungeon.model.Dungeon dungeon,
            game.dungeon.model.Room    room,
            game.domain.personaje.Personaje hero,
            int roomIndex,
            int gold,
            String theme,
            List<String> eventLog) {

        GameViewModel vm = new GameViewModel();
        vm.screen        = "exploration";
        vm.theme         = theme;

        vm.dungeonName   = dungeon.getNombre();
        vm.dungeonTheme  = String.valueOf(dungeon.getNivelDificultad()); // reutiliza campo descriptivo
        vm.room          = roomIndex + 1;
        vm.totalRooms    = dungeon.getSalas().size();
        vm.gold          = gold;

        vm.playerHp      = hero.getVida();
        vm.playerHpMax   = hero.getVidaMaxima();
        vm.playerHpPct   = safePct(hero.getVida(), hero.getVidaMaxima());

        vm.roomName         = room.getNombre();
        vm.roomDesc         = room.getDescripcion();
        vm.roomDifficulty   = String.valueOf(room.getDificultad());
        vm.roomHasTreasure  = room.tieneTesoro();
        vm.roomHasEnemy     = room.tieneEnemigo();
        vm.eventLog         = eventLog != null ? eventLog : List.of();

        return vm;
    }

    /** Crea un ViewModel de combate. */
    public static GameViewModel ofCombate(
            game.dungeon.model.Dungeon dungeon,
            game.domain.personaje.Personaje hero,
            game.domain.personaje.Personaje enemigo,
            int roomIndex,
            int gold,
            String theme,
            List<String> combatLog) {

        GameViewModel vm = new GameViewModel();
        vm.screen        = "combat";
        vm.theme         = theme;

        vm.dungeonName   = dungeon.getNombre();
        vm.dungeonTheme  = String.valueOf(dungeon.getNivelDificultad());
        vm.room          = roomIndex + 1;
        vm.totalRooms    = dungeon.getSalas().size();
        vm.gold          = gold;

        vm.playerHp      = hero.getVida();
        vm.playerHpMax   = hero.getVidaMaxima();
        vm.playerHpPct   = safePct(hero.getVida(), hero.getVidaMaxima());

        EnemyInfo ei = new EnemyInfo();
        ei.name   = enemigo.getNombre();
        ei.hp     = enemigo.getVida();
        ei.hpMax  = enemigo.getVidaMaxima();
        ei.hpPct  = safePct(enemigo.getVida(), enemigo.getVidaMaxima());
        ei.tier   = inferirTier(enemigo);
        vm.enemy  = ei;

        vm.combatLog = combatLog != null ? combatLog : List.of();

        return vm;
    }

    // ── Utilidades privadas ───────────────────────────────────────

    private static int safePct(int current, int max) {
        if (max <= 0) return 0;
        return Math.max(0, Math.min(100, current * 100 / max));
    }

    private static String inferirTier(game.domain.personaje.Personaje p) {
        int hp = p.getVidaMaxima();
        if (hp >= 200) return "jefe";
        if (hp >= 120) return "elite";
        return "menor";
    }
}
