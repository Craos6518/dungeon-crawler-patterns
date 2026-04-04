package game.application.state;

import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.DomainRuleViolationException;
import game.domain.exploration.Dungeon;
import game.domain.inventory.Inventory;
import game.events.observer.EventManager;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.state.game.GameState;
import game.state.game.GameStateContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
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
    private static final List<String> CAMPAIGN_THEME_ORDER = List.of("poison", "ice", "fire", "dark");

    private final Player player;
    private final Dungeon dungeon;
    private final Combat combat;
    private final EventManager eventManager;
    private final GameCaretaker caretaker;
    private final GameStateContext flowContext;

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
    /** Recompensa de combate pendiente de reclamar en sala de tesoro. */
    private boolean treasurePhaseActive;
    private String treasureEnemyName;
    private int treasureXpGained;
    private boolean treasureBossFight;
    private int treasureRoomIndex;
    private int selectedTreasureIndex;
    private boolean treasureAutoSaved;
    private final List<SimpleItem> treasureLoot;
    /** Conteo de eventos recibidos por observers productivos de sesión. */
    private final Map<EventType, Integer> observedEventCounts;
    private String lastObservedEventType;

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
        this.flowContext = new GameStateContext(new SessionScreenState(GameFlowState.EXPLORATION));
        this.activeScreen = GameFlowState.EXPLORATION.screenKey();
        this.heroType = "guerrero";
        this.heroSelectionLocked = false;
        this.completedThemes = new LinkedHashSet<>();
        this.treasurePhaseActive = false;
        this.treasureEnemyName = "";
        this.treasureXpGained = 0;
        this.treasureBossFight = false;
        this.treasureRoomIndex = 0;
        this.selectedTreasureIndex = -1;
        this.treasureAutoSaved = false;
        this.treasureLoot = new ArrayList<>();
        this.observedEventCounts = new EnumMap<>(EventType.class);
        this.lastObservedEventType = "";
        syncActiveScreenFromContext();
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
        syncActiveScreenFromContext();
        return activeScreen;
    }

    public GameFlowState activeState() {
        return GameFlowState.fromScreenKey(activeScreen());
    }

    public GameStateContext stateContext() {
        return flowContext;
    }

    public void transitionTo(GameFlowState nextState) {
        GameFlowState target = nextState == null ? GameFlowState.EXPLORATION : nextState;

        syncActiveScreenFromContext();
        String previous = this.activeScreen;
        String next = target.screenKey();
        if (next.equals(previous)) {
            return;
        }

        LOGGER.log(Level.INFO, "Cambio de pantalla: {0} -> {1}", new Object[]{previous, next});
        flowContext.cambiarEstado(new SessionScreenState(target));
        syncActiveScreenFromContext();
    }

    public void setActiveScreen(String screen) {
        transitionTo(GameFlowState.fromScreenKey(screen));
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

    public List<String> campaignThemeOrder() {
        return CAMPAIGN_THEME_ORDER;
    }

    public String nextCampaignTheme() {
        for (String theme : CAMPAIGN_THEME_ORDER) {
            if (!completedThemes.contains(theme)) {
                return theme;
            }
        }
        return "";
    }

    public boolean isThemeUnlockedForCampaign(String themeKey) {
        String normalized = normalizeThemeKey(themeKey);
        String next = nextCampaignTheme();
        return !normalized.isBlank() && !next.isBlank() && normalized.equals(next);
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

    public boolean hasPendingTreasure() {
        return treasurePhaseActive;
    }

    public String treasureEnemyName() {
        return treasureEnemyName;
    }

    public int treasureXpGained() {
        return treasureXpGained;
    }

    public boolean treasureBossFight() {
        return treasureBossFight;
    }

    public int treasureRoomIndex() {
        return treasureRoomIndex;
    }

    public int selectedTreasureIndex() {
        return selectedTreasureIndex;
    }

    public boolean treasureAutoSaved() {
        return treasureAutoSaved;
    }

    public List<SimpleItem> treasureLootOptions() {
        return List.copyOf(treasureLoot);
    }

    public int selectedTreasureGoldReward() {
        if (!treasurePhaseActive || treasureLoot.isEmpty() || selectedTreasureIndex < 0) {
            return 0;
        }
        return treasureLoot.get(selectedTreasureIndex).getValorTotal();
    }

    public void registerObservedEvent(EventType eventType) {
        if (eventType == null) {
            return;
        }
        observedEventCounts.merge(eventType, 1, Integer::sum);
        lastObservedEventType = eventType.name();
    }

    public int observedEventCount(EventType eventType) {
        if (eventType == null) {
            return 0;
        }
        return observedEventCounts.getOrDefault(eventType, 0);
    }

    public String lastObservedEventType() {
        return lastObservedEventType;
    }

    public void openTreasureRoom(String enemyName, int gainedXp, boolean bossFight, List<SimpleItem> lootOptions) {
        treasureLoot.clear();
        if (lootOptions != null) {
            for (SimpleItem item : lootOptions) {
                if (item != null) {
                    treasureLoot.add(copyItem(item));
                }
            }
        }

        if (treasureLoot.isEmpty()) {
            treasureLoot.add(copyItem(dungeon.randomCombatReward()));
        }

        treasurePhaseActive = true;
        treasureEnemyName = enemyName == null || enemyName.isBlank() ? "Enemigo desconocido" : enemyName;
        treasureXpGained = Math.max(0, gainedXp);
        treasureBossFight = bossFight;
        treasureRoomIndex = dungeon.currentRoomIndex();
        selectedTreasureIndex = 0;
        treasureAutoSaved = false;

        transitionTo(GameFlowState.TREASURE);
    }

    public void selectTreasureLoot(Integer requestedIndex) {
        requireTreasurePhase();
        if (treasureLoot.isEmpty()) {
            selectedTreasureIndex = -1;
            return;
        }

        int idx = requestedIndex == null ? 0 : requestedIndex;
        selectedTreasureIndex = Math.max(0, Math.min(idx, treasureLoot.size() - 1));
    }

    public void takeSelectedTreasure() {
        requireTreasurePhase();
        if (treasureLoot.isEmpty() || selectedTreasureIndex < 0 || selectedTreasureIndex >= treasureLoot.size()) {
            throw new DomainRuleViolationException("No hay botin seleccionado para recoger.");
        }

        SimpleItem selected = treasureLoot.get(selectedTreasureIndex);
        try {
            inventory().add(selected);
        } catch (RuntimeException ex) {
            throw new DomainRuleViolationException("No puedes recoger el botin: inventario lleno.");
        }

        player().addGold(selected.getValorTotal());
        appendEvent("Botin reclamado: " + selected.getNombre() + " (+" + selected.getValorTotal() + " oro).");
        eventManager.notificar(new GameEvent(EventType.ITEM_RECOGIDO)
            .agregarDato("item", selected.getNombre())
            .agregarDato("origen", "tesoro-combate"));

        completeTreasureAndProgress();
    }

    public void skipTreasure() {
        requireTreasurePhase();
        appendEvent("Abandonas el botin de la sala.");
        completeTreasureAndProgress();
    }

    void restoreTreasureState(
        boolean active,
        String enemyName,
        int gainedXp,
        boolean bossFight,
        int roomIndex,
        List<SimpleItem> lootOptions,
        int selectedIndex,
        boolean autoSaved
    ) {
        treasureLoot.clear();
        if (lootOptions != null) {
            for (SimpleItem item : lootOptions) {
                if (item != null) {
                    treasureLoot.add(copyItem(item));
                }
            }
        }

        treasurePhaseActive = active;
        treasureEnemyName = enemyName == null ? "" : enemyName;
        treasureXpGained = Math.max(0, gainedXp);
        treasureBossFight = bossFight;
        treasureRoomIndex = Math.max(0, Math.min(roomIndex, Math.max(0, dungeon.totalRooms() - 1)));
        treasureAutoSaved = autoSaved;

        if (!active) {
            selectedTreasureIndex = -1;
            treasureLoot.clear();
            treasureEnemyName = "";
            treasureXpGained = 0;
            treasureBossFight = false;
            treasureRoomIndex = dungeon.currentRoomIndex();
            treasureAutoSaved = false;
            return;
        }

        if (treasureLoot.isEmpty()) {
            selectedTreasureIndex = -1;
            return;
        }

        selectedTreasureIndex = Math.max(0, Math.min(selectedIndex, treasureLoot.size() - 1));
    }

    public boolean isStableForSave() {
        GameFlowState screen = activeState();
        if (bootstrapMenuSession) {
            return false;
        }
        if (!player.isAlive()) {
            return false;
        }
        if (combat.isActive()) {
            return false;
        }
        return screen == GameFlowState.EXPLORATION
            || screen == GameFlowState.INVENTORY
            || screen == GameFlowState.SAVES;
    }

    public void assertStableForSave() {
        assertPersistenceInvariants();

        if (bootstrapMenuSession) {
            throw new DomainRuleViolationException("No se puede guardar antes de iniciar o cargar una partida.");
        }
        if (!player.isAlive()) {
            throw new DomainRuleViolationException("No se puede guardar: el heroe esta derrotado.");
        }
        if (combat.isActive()) {
            throw new DomainRuleViolationException("No se puede guardar durante un combate activo.");
        }
        GameFlowState screen = activeState();
        if (screen != GameFlowState.EXPLORATION
                && screen != GameFlowState.INVENTORY
                && screen != GameFlowState.SAVES) {
            throw new DomainRuleViolationException("No se puede guardar en la pantalla actual.");
        }
    }

    public GameMemento createSnapshot() {
        return GameSessionMementoMapper.toMemento(this, activeScreen());
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
        GameFlowState screen = activeState();
        boolean explorationScreen = screen == GameFlowState.EXPLORATION;
        boolean inventoryScreen = screen == GameFlowState.INVENTORY;
        boolean combatScreen = screen == GameFlowState.COMBAT;
        boolean treasureScreen = screen == GameFlowState.TREASURE;
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

        states.put("btn-style-balanced", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-style-aggressive", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-style-defensive", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-buff-power", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-buff-guard", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-save-checkpoint", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-rollback-checkpoint", (heroAlive && combatScreen && combatActive
            && combat.hasTacticalCheckpoint() && !combat.tacticalCheckpointConsumed())
            ? "default" : "disabled");

        states.put("btn-retreat", (heroAlive && combatScreen && combatActive)
            ? "default" : "disabled");

        states.put("btn-quick-item", (heroAlive && combatScreen && combatActive && hasConsumable)
            ? "default" : "disabled");

        states.put("btn-use-item-inv", (heroAlive && inventoryScreen && selectedConsumable)
            ? "default" : "disabled");

        states.put("btn-back-inv", inventoryScreen ? "default" : "disabled");

        states.put("btn-take-all", (heroAlive && treasureScreen && treasurePhaseActive
            && !treasureLoot.isEmpty() && selectedTreasureIndex >= 0) ? "default" : "disabled");

        states.put("btn-skip-loot", (heroAlive && treasureScreen && treasurePhaseActive)
            ? "default" : "disabled");

        return states;
    }

    private static void appendBounded(List<String> target, String value, int maxSize) {
        target.add(value);
        while (target.size() > maxSize) {
            target.remove(0);
        }
    }

    private void assertPersistenceInvariants() {
        inventory().clampSelection();

        int itemCount = inventory().size();
        int selectedIndex = inventory().selectedIndex();

        if (itemCount == 0 && selectedIndex != -1) {
            throw new DomainRuleViolationException("Inventario inconsistente: seleccion invalida para inventario vacio.");
        }
        if (itemCount > 0 && (selectedIndex < 0 || selectedIndex >= itemCount)) {
            throw new DomainRuleViolationException("Inventario inconsistente: indice seleccionado fuera de rango.");
        }
        if (activeState() == GameFlowState.COMBAT && !combat.isActive()) {
            throw new DomainRuleViolationException("Estado inconsistente: pantalla de combate sin enemigo activo.");
        }
        if (activeState() == GameFlowState.TREASURE && !treasurePhaseActive) {
            throw new DomainRuleViolationException("Estado inconsistente: pantalla de tesoro sin botin pendiente.");
        }
    }

    private void requireTreasurePhase() {
        if (!treasurePhaseActive || activeState() != GameFlowState.TREASURE) {
            throw new DomainRuleViolationException("No hay una sala de tesoro activa.");
        }
    }

    private void completeTreasureAndProgress() {
        dungeon.markCurrentRoomTreasureResolved();

        boolean finalBossVictory = treasureBossFight && !dungeon.canAdvanceRoom();
        if (finalBossVictory) {
            String dungeonName = dungeon.model().getNombre();
            markThemeCompleted(dungeon.themeKey());
            setHeroSelectionLocked(true);
            clearTreasureState();
            transitionTo(GameFlowState.HERO);
            appendEvent("Conquistaste " + dungeonName + ". Elige tu siguiente mazmorra.");

            eventManager.notificar(new GameEvent(EventType.SALA_COMPLETADA)
                .agregarDato("resultado", "mazmorra_completada")
                .agregarDato("mazmorra", dungeonName)
                .agregarDato("tema", dungeon.themeKey())
                .agregarDato("salas", dungeon.totalRooms()));
            return;
        }

        if (dungeon.canAdvanceRoom()) {
            dungeon.advanceRoom();
            var nextRoom = dungeon.currentRoom();
            appendEvent("Avanzas a la sala " + (dungeon.currentRoomIndex() + 1) + ": " + nextRoom.name());

            eventManager.notificar(new GameEvent(EventType.SALA_ENTRAR)
                .agregarDato("sala", dungeon.currentRoomIndex() + 1)
                .agregarDato("nombre", nextRoom.name()));
        }

        clearTreasureState();
        transitionTo(GameFlowState.EXPLORATION);
    }

    private void clearTreasureState() {
        treasurePhaseActive = false;
        treasureEnemyName = "";
        treasureXpGained = 0;
        treasureBossFight = false;
        treasureRoomIndex = dungeon.currentRoomIndex();
        selectedTreasureIndex = -1;
        treasureAutoSaved = false;
        treasureLoot.clear();
    }

    private static SimpleItem copyItem(SimpleItem item) {
        return new SimpleItem(
            item.getNombre(),
            item.getDescripcion(),
            item.getTipo(),
            item.getValorTotal(),
            item.getPesoTotal()
        );
    }

    private void syncActiveScreenFromContext() {
        GameState currentState = flowContext.getEstadoActual();
        if (currentState == null) {
            this.activeScreen = GameFlowState.EXPLORATION.screenKey();
            return;
        }
        this.activeScreen = GameFlowState.fromScreenKey(currentState.getNombre()).screenKey();
    }

    private static final class SessionScreenState implements GameState {
        private final GameFlowState flowState;

        private SessionScreenState(GameFlowState flowState) {
            this.flowState = flowState;
        }

        @Override
        public void manejarEntrada(String entrada) {
            // No-op: el runtime productivo gestiona comandos por use cases.
        }

        @Override
        public void actualizar() {
            // No-op: la actualizacion de juego vive en los use cases.
        }

        @Override
        public void render() {
            // No-op: la presentacion se realiza via GamePresenter.
        }

        @Override
        public void onEnter() {
            // No-op.
        }

        @Override
        public void onExit() {
            // No-op.
        }

        @Override
        public String getNombre() {
            return flowState.screenKey();
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
