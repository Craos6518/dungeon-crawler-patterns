package game.application.state;

import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.DomainRuleViolationException;
import game.domain.exploration.Dungeon;
import game.domain.inventory.Inventory;
import game.events.observer.EventManager;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Estado de aplicacion para la sesion web. No contiene reglas de negocio complejas.
 */
public class GameSession {

    private static final Logger LOGGER = Logger.getLogger(GameSession.class.getName());

    private static final int EVENT_LOG_LIMIT = 12;
    private static final int COMBAT_LOG_LIMIT = 16;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Player player;
    private final Dungeon dungeon;
    private final Combat combat;
    private final EventManager eventManager;
    private final GameCaretaker caretaker;

    private final List<String> eventLog;
    private final List<String> combatLog;

    private String activeScreen;
    /** "guerrero" | "mago" | "arquero" — clase seleccionada en pantalla de héroe */
    private String heroType;
    /** True cuando ya no se permite cambiar de heroe en la campaña actual. */
    private boolean heroSelectionLocked;
    /** Lista de temas ya completados en la campaña actual. */
    private final Set<String> completedThemes;
    /** True cuando la sesión fue creada solo para menú inicial (sin partida activa real). */
    private boolean bootstrapMenuSession;

    public GameSession(
        Player player,
        Dungeon dungeon,
        Combat combat,
        EventManager eventManager,
        GameCaretaker caretaker
    ) {
        this.player = player;
        this.dungeon = dungeon;
        this.combat = combat;
        this.eventManager = eventManager;
        this.caretaker = caretaker;
        this.eventLog = new ArrayList<>();
        this.combatLog = new ArrayList<>();
        this.activeScreen = "exploration";
        this.heroType = "guerrero";
        this.heroSelectionLocked = false;
        this.completedThemes = new LinkedHashSet<>();
    }

    public Player player() {
        return player;
    }

    public Inventory inventory() {
        return player.inventory();
    }

    public Dungeon dungeon() {
        return dungeon;
    }

    public Combat combat() {
        return combat;
    }

    public EventManager eventManager() {
        return eventManager;
    }

    public GameCaretaker caretaker() {
        return caretaker;
    }

    public String activeScreen() {
        return activeScreen;
    }

    public void setActiveScreen(String screen) {
        String next = screen == null || screen.isBlank() ? "exploration" : screen;
        if (!next.equals(this.activeScreen)) {
            LOGGER.log(Level.INFO, "Cambio de pantalla: {0} -> {1}", new Object[]{this.activeScreen, next});
        }
        this.activeScreen = next;
    }

    public String heroType() {
        return heroType;
    }

    public void setHeroType(String heroType) {
        this.heroType = heroType == null
            ? "guerrero"
            : heroType.trim().toLowerCase(Locale.ROOT);
    }

    public boolean isHeroSelectionLocked() {
        return heroSelectionLocked;
    }

    public void setHeroSelectionLocked(boolean heroSelectionLocked) {
        this.heroSelectionLocked = heroSelectionLocked;
    }

    public Set<String> completedThemes() {
        return Set.copyOf(completedThemes);
    }

    public void markThemeCompleted(String themeKey) {
        String normalized = normalizeThemeKey(themeKey);
        if (!normalized.isBlank()) {
            completedThemes.add(normalized);
        }
    }

    public boolean isThemeCompleted(String themeKey) {
        String normalized = normalizeThemeKey(themeKey);
        return !normalized.isBlank() && completedThemes.contains(normalized);
    }

    public void replaceCompletedThemes(Set<String> restoredThemes) {
        completedThemes.clear();
        if (restoredThemes == null) {
            return;
        }
        for (String theme : restoredThemes) {
            markThemeCompleted(theme);
        }
    }

    public boolean isBootstrapMenuSession() {
        return bootstrapMenuSession;
    }

    public void setBootstrapMenuSession(boolean bootstrapMenuSession) {
        this.bootstrapMenuSession = bootstrapMenuSession;
    }

    public List<String> eventLog() {
        return List.copyOf(eventLog);
    }

    public List<String> combatLog() {
        return List.copyOf(combatLog);
    }

    public void appendEvent(String message) {
        appendBounded(eventLog, timestamp() + " " + message, EVENT_LOG_LIMIT);
    }

    public void appendCombat(String message) {
        appendBounded(combatLog, timestamp() + " " + message, COMBAT_LOG_LIMIT);
    }

    public void appendSystemMessage(String message) {
        appendEvent("[SYS] " + message);
    }

    public void replaceEventLog(List<String> restored) {
        eventLog.clear();
        if (restored == null) {
            return;
        }
        for (String line : restored) {
            appendBounded(eventLog, line == null ? "" : line, EVENT_LOG_LIMIT);
        }
    }

    public void replaceCombatLog(List<String> restored) {
        combatLog.clear();
        if (restored == null) {
            return;
        }
        for (String line : restored) {
            appendBounded(combatLog, line == null ? "" : line, COMBAT_LOG_LIMIT);
        }
    }

    public boolean hasActiveEnemy() {
        return combat.isActive();
    }

    public boolean isStableForSave() {
        if (bootstrapMenuSession) {
            return false;
        }
        if (!player.isAlive()) {
            return false;
        }
        if (combat.isActive()) {
            return false;
        }
        return "exploration".equals(activeScreen)
            || "inventory".equals(activeScreen)
            || "saves".equals(activeScreen);
    }

    public void assertStableForSave() {
        if (bootstrapMenuSession) {
            throw new DomainRuleViolationException("No se puede guardar antes de iniciar o cargar una partida.");
        }
        if (!player.isAlive()) {
            throw new DomainRuleViolationException("No se puede guardar: el heroe esta derrotado.");
        }
        if (combat.isActive()) {
            throw new DomainRuleViolationException("No se puede guardar durante un combate activo.");
        }
        if (!"exploration".equals(activeScreen) && !"inventory".equals(activeScreen)
                && !"saves".equals(activeScreen)) {
            throw new DomainRuleViolationException("No se puede guardar en la pantalla actual.");
        }
    }

    public GameMemento createSnapshot() {
        return GameSessionMementoMapper.toMemento(this, activeScreen);
    }

    public void restoreSnapshot(GameMemento snapshot) {
        GameSessionMementoMapper.restore(this, snapshot);
    }

    public boolean isEnemyPendingInCurrentRoom() {
        return dungeon.isEnemyPendingInCurrentRoom(hasActiveEnemy());
    }

    public boolean isTreasurePendingInCurrentRoom() {
        return dungeon.isTreasurePendingInCurrentRoom();
    }

    public Map<String, String> buttonsState() {
        Map<String, String> states = new HashMap<>();

        boolean heroAlive = player.isAlive();
        boolean combatActive = hasActiveEnemy();
        boolean explorationScreen = "exploration".equals(activeScreen);
        boolean inventoryScreen = "inventory".equals(activeScreen);
        boolean combatScreen = "combat".equals(activeScreen);
        boolean hasConsumable = inventory().hasConsumable();
        boolean selectedConsumable = inventory().isSelectedConsumable();
        boolean dungeonCompleted = dungeon.isCurrentRoomBoss()
            && !combatActive
            && !isEnemyPendingInCurrentRoom();

        states.put("btn-avanzar", (heroAlive && explorationScreen && !combatActive
            && (dungeon.canAdvanceRoom() || dungeonCompleted)) ? "default" : "disabled");

        states.put("btn-explorar", (heroAlive && explorationScreen && !combatActive
            && !dungeon.wasCurrentRoomTreasureResolved()) ? "default" : "disabled");

        states.put("btn-inventory", (heroAlive && !combatScreen) ? "default" : "disabled");
        states.put("btn-guardar", isStableForSave() ? "default" : "disabled");
        states.put("btn-save-slot", isStableForSave() ? "default" : "disabled");

        states.put("btn-forzar-combate", (heroAlive && explorationScreen && !combatActive)
            ? "default" : "disabled");

        states.put("btn-atacar", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-defender", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-usar-objeto", (heroAlive && combatScreen && combatActive && hasConsumable)
            ? "default" : "disabled");

        states.put("btn-habilidad", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-use-item-inv", (heroAlive && inventoryScreen && selectedConsumable)
            ? "default" : "disabled");

        states.put("btn-back-inv", inventoryScreen ? "default" : "disabled");

        return states;
    }

    private static void appendBounded(List<String> target, String value, int maxSize) {
        target.add(value);
        while (target.size() > maxSize) {
            target.remove(0);
        }
    }

    private static String timestamp() {
        return LocalDateTime.now().format(TIME_FMT);
    }

    private static String normalizeThemeKey(String themeKey) {
        if (themeKey == null) {
            return "";
        }
        String normalized = themeKey.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "fire", "ice", "poison", "dark" -> normalized;
            default -> "";
        };
    }
}
