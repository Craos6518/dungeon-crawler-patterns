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
    /** "exploration" | "combat" | "inventory" */
    public String screen;

    // ── Header compartido (3 pantallas) ──────────────────────────
    public String dungeonName;
    public String dungeonTheme;
    public int    room;
    public int    totalRooms;
    public int    playerHp;
    public int    playerHpMax;
    public int    playerHpPct;   // 0–100 para width del fill
    public int    gold;

    // ── Tema visual ───────────────────────────────────────────────
    /** "fire" | "ice" | "poison" | "dark" */
    public String theme;

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
    public List<String> combatLog;

    // ── Pantalla Inventario ───────────────────────────────────────
    public InventoryInfo inventory;
    public List<InventoryItemInfo> inventoryItems;
    public ItemInfo      selectedItem;
    public Integer       selectedItemIndex;

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
