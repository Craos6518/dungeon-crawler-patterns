package game.application.runtime;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import game.application.dto.AttackCommandRequest;
import game.application.dto.LoadGameCommandRequest;
import game.application.dto.SaveGameCommandRequest;
import game.application.dto.SelectItemCommandRequest;
import game.application.dto.StartGameCommandRequest;
import game.application.dto.UiCommand;
import game.application.dto.UseItemCommandRequest;
import game.application.dto.UseSkillCommandRequest;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.application.usecase.AdvanceTurnUseCase;
import game.application.usecase.AttackUseCase;
import game.application.usecase.DefendUseCase;
import game.application.usecase.ForceCombatUseCase;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.MoveInventorySelectionUseCase;
import game.application.usecase.RetreatCombatUseCase;
import game.application.usecase.SaveGameUseCase;
import game.application.usecase.SearchTreasureUseCase;
import game.application.usecase.SelectInventoryItemUseCase;
import game.application.usecase.UseItemUseCase;
import game.application.usecase.UseSkillUseCase;
import game.persistence.memento.GameMemento;
import game.items.model.SimpleItem;
import game.persistence.memento.SaveSlotNotFoundException;
import game.ui.GameViewModel;
import game.ui.integration.GamePresenter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runtime unico de aplicacion. Orquesta comandos, use cases y presentacion de estado.
 */
public class GameRuntime implements GameCommandHandler {

    private static final Logger LOGGER = Logger.getLogger(GameRuntime.class.getName());
    private static final Set<String> SUPPORTED_THEME_KEYS = Set.of("fire", "ice", "poison", "dark");
    private static final Set<String> SUPPORTED_HERO_TYPES = Set.of("guerrero", "mago", "arquero");
    private static final int MIN_SLOT = 1;
    private static final int MAX_SLOT = 3;

    private final Gson gson;
    private final GamePresenter presenter;
    private GameSession session;
    private int preferredSaveSlot;

    private AdvanceTurnUseCase advanceTurnUseCase;
    private SearchTreasureUseCase searchTreasureUseCase;
    private SaveGameUseCase saveGameUseCase;
    private ForceCombatUseCase forceCombatUseCase;
    private AttackUseCase attackUseCase;
    private DefendUseCase defendUseCase;
    private RetreatCombatUseCase retreatCombatUseCase;
    private UseItemUseCase useItemUseCase;
    private UseSkillUseCase useSkillUseCase;
    private SelectInventoryItemUseCase selectInventoryItemUseCase;
    private MoveInventorySelectionUseCase moveInventorySelectionUseCase;

    private final Map<String, TypedCommandHandler<?>> handlers;

    public GameRuntime() {
        this(GameSessionFactory.createInitialMenuSession());
    }

    public GameRuntime(GameSession session) {
        this.gson = new Gson();
        this.presenter = new GamePresenter();
        this.preferredSaveSlot = MIN_SLOT;
        bindSession(session == null ? GameSessionFactory.createInitialMenuSession() : session);
        this.handlers = registerHandlers();
    }

    @Override
    public synchronized boolean supportsAction(String action) {
        return handlers.containsKey(normalizeAction(action));
    }

    @Override
    public synchronized void handleCommand(UiCommand command) {
        if (command == null) {
            throw new InvalidRuntimeCommandException("Comando nulo recibido.");
        }

        String action = normalizeAction(command.action);
        if (action.isBlank()) {
            throw new InvalidRuntimeCommandException("Comando sin accion.");
        }

        TypedCommandHandler<?> handler = handlers.get(action);
        if (handler == null) {
            throw new InvalidRuntimeCommandException("Accion no soportada: " + action);
        }

        JsonObject payload = command.payload;
        if (payload == null) {
            throw new InvalidRuntimeCommandException("Comando invalido: payload obligatorio para accion " + action + ".");
        }

        LOGGER.log(Level.INFO, "Ejecutando action={0}", action);
        try {
            handler.handle(payload, gson);
        } finally {
            if (session != null) {
                session.inventory().clampSelection();
            }
        }
    }

    @Override
    public synchronized GameViewModel presentViewModel() {
        return presenter.present(session);
    }

    @Override
    public synchronized void registrarMensajeSistema(String mensaje) {
        session.appendSystemMessage(mensaje);
    }

    private void bindSession(GameSession session) {
        this.session = session;
        this.advanceTurnUseCase = new AdvanceTurnUseCase(session);
        this.searchTreasureUseCase = new SearchTreasureUseCase(session);
        this.saveGameUseCase = new SaveGameUseCase(session);
        this.forceCombatUseCase = new ForceCombatUseCase(session);
        this.attackUseCase = new AttackUseCase(session);
        this.defendUseCase = new DefendUseCase(session);
        this.retreatCombatUseCase = new RetreatCombatUseCase(session);
        this.useItemUseCase = new UseItemUseCase(session);
        this.useSkillUseCase = new UseSkillUseCase(session);
        this.selectInventoryItemUseCase = new SelectInventoryItemUseCase(session);
        this.moveInventorySelectionUseCase = new MoveInventorySelectionUseCase(session);
    }

    private Map<String, TypedCommandHandler<?>> registerHandlers() {
        Map<String, TypedCommandHandler<?>> map = new LinkedHashMap<>();

        map.put("startGame", TypedCommandHandler.of(StartGameCommandRequest.class, Set.of(), payload -> {
            String theme = resolveThemeOrDefault(payload.theme);
            ensureThemeAvailableForCampaign(theme);

            String heroType = resolveHeroTypeForNewRun(payload.heroType);
            GameSession newSession = createSessionPreservingCampaignProgress(theme, heroType);
            newSession.setActiveScreen("exploration");

            bindSession(newSession);
            preferredSaveSlot = MIN_SLOT;
        }, this::validateStartGamePayload));

        map.put("openMainMenu", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("menu");
            session.appendEvent("Regresas al menu principal.");
        }, this::validateEmptyPayload));

        map.put("advanceRoom", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            advanceTurnUseCase.execute();
        }, this::validateAdvanceRoomPayload));

        map.put("searchTreasure", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            searchTreasureUseCase.execute();
        }, this::validateSearchTreasurePayload));

        map.put("openInventory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("inventory");
            session.inventory().clampSelection();
            session.appendEvent("Inventario abierto.");
        }, this::validateEmptyPayload));

        map.put("toggleInventory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            if ("inventory".equals(session.activeScreen())) {
                session.setActiveScreen(session.hasActiveEnemy() ? "combat" : "exploration");
            } else {
                session.setActiveScreen("inventory");
                session.inventory().clampSelection();
                session.appendEvent("Inventario abierto.");
            }
        }, this::validateEmptyPayload));

        map.put("closeInventory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen(session.hasActiveEnemy() ? "combat" : "exploration");
        }, this::validateEmptyPayload));

        map.put("saveGame", TypedCommandHandler.of(SaveGameCommandRequest.class, Set.of(), payload -> {
            executeSaveToSlot(payload.slot);
        }, this::validateSavePayload));

        map.put("loadGame", TypedCommandHandler.of(LoadGameCommandRequest.class, Set.of(), payload -> {
            executeLoadFromSlot(payload.slot);
        }, this::validateLoadPayload));

        map.put("quickSave", TypedCommandHandler.of(SaveGameCommandRequest.class, Set.of(), payload -> {
            executeSaveToSlot(payload.slot);
        }, this::validateSavePayload));

        map.put("quickLoad", TypedCommandHandler.of(LoadGameCommandRequest.class, Set.of(), payload -> {
            executeLoadFromSlot(payload.slot);
        }, this::validateLoadPayload));

        map.put("forceCombat", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            forceCombatUseCase.execute();
        }, this::validateEmptyPayload));

        map.put("attack", TypedCommandHandler.of(AttackCommandRequest.class, Set.of(), payload -> {
            attackUseCase.execute(payload);
        }, this::validateAttackPayload));

        map.put("defend", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            defendUseCase.execute();
        }, this::validateDefendPayload));

        map.put("retreatCombat", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            retreatCombatUseCase.execute();
        }, this::validateDefendPayload));

        map.put("useItem", TypedCommandHandler.of(UseItemCommandRequest.class, Set.of(), payload -> {
            useItemUseCase.execute(payload);
        }, this::validateUseItemPayload));

        map.put("consumeSelectedItem", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            executeConsumeSelectedItem();
        }, this::validateEmptyPayload));

        map.put("useSkill", TypedCommandHandler.of(UseSkillCommandRequest.class, Set.of(), payload -> {
            useSkillUseCase.execute(payload);
        }, this::validateUseSkillPayload));

        map.put("selectItem", TypedCommandHandler.of(SelectItemCommandRequest.class, Set.of("itemIndex"), payload -> {
            selectInventoryItemUseCase.execute(payload.itemIndex);
        }, this::validateSelectItemPayload));

        map.put("inventoryUp", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveUp();
        }, this::validateEmptyPayload));

        map.put("inventoryPrevious", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveUp();
        }, this::validateEmptyPayload));

        map.put("inventoryDown", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveDown();
        }, this::validateEmptyPayload));

        map.put("inventoryNext", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveDown();
        }, this::validateEmptyPayload));

        map.put("rerenderCurrentScreen", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op: la UI recibe estado completo en cada push.
        }, this::validateEmptyPayload));

        map.put("filterCategory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op: filtro de categoria se resuelve del lado de UI.
        }, this::validateFilterCategoryPayload));

        // ── Nuevas pantallas ────────────────────────────────────────

        map.put("goToHeroSelect", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("hero");
        }, this::validateEmptyPayload));

        map.put("selectHero", TypedCommandHandler.of(StartGameCommandRequest.class, Set.of(), payload -> {
            String heroType = normalizeHeroType(payload.heroType);
            if (heroType.isBlank()) {
                return;
            }

            if (session.isHeroSelectionLocked()) {
                String lockedHero = normalizeHeroType(session.heroType());
                if (lockedHero.isBlank()) {
                    session.setHeroType(heroType);
                    return;
                }
                if (!lockedHero.equals(heroType)) {
                    throw new InvalidRuntimeCommandException(
                        "No puedes cambiar de heroe despues de completar una mazmorra."
                    );
                }
                session.setHeroType(lockedHero);
                return;
            }

            session.setHeroType(heroType);
        }, this::validateSelectHeroPayload));

        map.put("heroNewGame", TypedCommandHandler.of(StartGameCommandRequest.class, Set.of(), payload -> {
            String theme = resolveThemeOrDefault(payload.theme);
            ensureThemeAvailableForCampaign(theme);

            String heroType = resolveHeroTypeForNewRun(payload.heroType);
            GameSession newSession = createSessionPreservingCampaignProgress(theme, heroType);
            newSession.setActiveScreen("exploration");
            bindSession(newSession);
            preferredSaveSlot = MIN_SLOT;
        }, this::validateStartGamePayload));

        map.put("showStats", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("stats");
        }, this::validateEmptyPayload));

        map.put("closeStats", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("menu");
        }, this::validateEmptyPayload));

        map.put("openSaves", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("saves");
        }, this::validateEmptyPayload));

        map.put("saveToSlot", TypedCommandHandler.of(SaveGameCommandRequest.class, Set.of(), payload -> {
            executeSaveToSlot(payload.slot);
        }, this::validateSavePayload));

        map.put("loadFromSlot", TypedCommandHandler.of(LoadGameCommandRequest.class, Set.of(), payload -> {
            executeLoadFromSlot(payload.slot);
        }, this::validateLoadPayload));

        map.put("restoreGame", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // Carga el slot preferido (ultimo usado) al restaurar tras game over.
            executeLoadFromSlot(preferredSaveSlot);
        }, this::validateEmptyPayload));

        map.put("newGame", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("hero");
        }, this::validateEmptyPayload));

        map.put("exitGame", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op en web: el cierre lo maneja la aplicacion JavaFX.
            LOGGER.info("Comando exitGame recibido desde UI.");
        }, this::validateEmptyPayload));

        // ── Loot / Tesoro (no-op hasta implementar TreasureRoomState) ──
        map.put("takeLoot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("exploration");
            session.appendEvent("Has recogido el botin de la sala.");
        }, this::validateEmptyPayload));

        map.put("selectLoot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op por ahora: la selección de loot se usa para render en UI.
        }, this::validateSelectLootPayload));

        map.put("skipLoot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen("exploration");
            session.appendEvent("Avanzas sin recoger el botin.");
        }, this::validateEmptyPayload));

        map.put("selectSaveSlot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op: la seleccion de slot se maneja localmente en JS.
        }, this::validateEmptyPayload));

        return map;
    }

    private void validateEmptyPayload(JsonObject payload) {
        if (payload == null) {
            throw new InvalidRuntimeCommandException("Payload obligatorio.");
        }
    }

    private void validateSavePayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateOptionalIntegerField(payload, "slot", MIN_SLOT, MAX_SLOT);
    }

    private void validateStartGamePayload(JsonObject payload) {
        validateEmptyPayload(payload);
        if (!payload.has("theme") || payload.get("theme").isJsonNull()) {
            if (payload.has("heroType") && !payload.get("heroType").isJsonNull()) {
                validateOptionalStringField(payload, "heroType", false);
                String normalizedHeroType = normalizeHeroType(payload.get("heroType").getAsString());
                if (normalizedHeroType.isBlank()) {
                    throw new InvalidRuntimeCommandException(
                        "heroType invalido. Valores permitidos: " + String.join(", ", SUPPORTED_HERO_TYPES)
                    );
                }
            }
            return;
        }

        validateOptionalStringField(payload, "theme", false);
        String normalizedTheme = payload.get("theme").getAsString().trim().toLowerCase(Locale.ROOT);
        if (!SUPPORTED_THEME_KEYS.contains(normalizedTheme)) {
            throw new InvalidRuntimeCommandException(
                "theme invalido. Valores permitidos: " + String.join(", ", SUPPORTED_THEME_KEYS)
            );
        }

        if (payload.has("heroType") && !payload.get("heroType").isJsonNull()) {
            validateOptionalStringField(payload, "heroType", false);
            String normalizedHeroType = normalizeHeroType(payload.get("heroType").getAsString());
            if (normalizedHeroType.isBlank()) {
                throw new InvalidRuntimeCommandException(
                    "heroType invalido. Valores permitidos: " + String.join(", ", SUPPORTED_HERO_TYPES)
                );
            }
        }
    }

    private void validateSelectHeroPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredStringField(payload, "heroType", false);
        String normalizedHeroType = normalizeHeroType(payload.get("heroType").getAsString());
        if (normalizedHeroType.isBlank()) {
            throw new InvalidRuntimeCommandException(
                "heroType invalido. Valores permitidos: " + String.join(", ", SUPPORTED_HERO_TYPES)
            );
        }
    }

    private void validateSelectLootPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateOptionalIntegerField(payload, "lootIndex", 0, Integer.MAX_VALUE);
    }

    private void validateAdvanceRoomPayload(JsonObject payload) {
        validateEmptyPayload(payload);
    }

    private void validateSearchTreasurePayload(JsonObject payload) {
        validateEmptyPayload(payload);
    }

    private void validateLoadPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateOptionalIntegerField(payload, "slot", MIN_SLOT, MAX_SLOT);
    }

    private void executeSaveToSlot(Integer requestedSlot) {
        int slot = resolveSlotOrPreferred(requestedSlot);
        saveGameUseCase.execute(slot);
        preferredSaveSlot = slot;
    }

    private void executeLoadFromSlot(Integer requestedSlot) {
        int slot = resolveSlotOrPreferred(requestedSlot);
        GameSession loadedSession = loadSessionFromSlot(slot);
        bindSession(loadedSession);
        preferredSaveSlot = slot;
    }

    private void executeConsumeSelectedItem() {
        int consumableIndex = session.inventory().selectedConsumableIndex().orElse(-1);
        if (consumableIndex < 0) {
            throw new InvalidRuntimeCommandException("No hay consumibles disponibles en el inventario.");
        }

        UseItemCommandRequest request = new UseItemCommandRequest();
        request.itemIndex = consumableIndex;
        useItemUseCase.execute(request);
    }

    private int resolveSlotOrPreferred(Integer requestedSlot) {
        if (requestedSlot == null) {
            return preferredSaveSlot;
        }
        return clampSlot(requestedSlot);
    }

    private GameSession loadSessionFromSlot(int slot) {
        String fileName = "Slot_" + slot;
        if (!session.caretaker().existeEnDisco(fileName)) {
            throw new SaveSlotNotFoundException("Slot vacio: " + fileName + ".save no existe.");
        }
        GameMemento memento = session.caretaker().cargarDesdeDisco(fileName);

        String theme = resolveThemeFromMemento(memento);
        String heroType = resolveHeroTypeFromMemento(memento);
        GameSession restoredSession = GameSessionFactory.createSessionForTheme(theme, heroType);
        new LoadGameUseCase(restoredSession).restoreFromMemento(fileName, memento);
        return restoredSession;
    }

    private static String resolveThemeFromMemento(GameMemento memento) {
        if (memento == null || memento.getEstadoMazmorra() == null) {
            return "fire";
        }

        Object rawTheme = memento.getEstadoMazmorra().get("tema");
        if (rawTheme == null) {
            return "fire";
        }

        String theme = String.valueOf(rawTheme).trim();
        return theme.isBlank() ? "fire" : theme;
    }

    private static String resolveHeroTypeFromMemento(GameMemento memento) {
        if (memento == null || memento.getEstadoPersonaje() == null) {
            return "guerrero";
        }

        Object rawHeroType = memento.getEstadoPersonaje().get("heroType");
        if (rawHeroType == null) {
            return "guerrero";
        }

        String heroType = String.valueOf(rawHeroType).trim().toLowerCase(Locale.ROOT);
        if (SUPPORTED_HERO_TYPES.contains(heroType)) {
            return heroType;
        }
        return "guerrero";
    }

    private String resolveThemeOrDefault(String rawTheme) {
        String theme = rawTheme != null ? rawTheme.trim().toLowerCase(Locale.ROOT) : "fire";
        return SUPPORTED_THEME_KEYS.contains(theme) ? theme : "fire";
    }

    private String resolveHeroTypeForNewRun(String payloadHeroType) {
        String requestedHeroType = normalizeHeroType(payloadHeroType);
        String currentHeroType = normalizeHeroType(session.heroType());

        if (session.isHeroSelectionLocked()) {
            String lockedHeroType = currentHeroType.isBlank() ? "guerrero" : currentHeroType;
            if (!requestedHeroType.isBlank() && !lockedHeroType.equals(requestedHeroType)) {
                throw new InvalidRuntimeCommandException(
                    "No puedes cambiar de heroe despues de completar una mazmorra."
                );
            }
            return lockedHeroType;
        }

        if (!requestedHeroType.isBlank()) {
            return requestedHeroType;
        }
        if (!currentHeroType.isBlank()) {
            return currentHeroType;
        }
        return "guerrero";
    }

    private void ensureThemeAvailableForCampaign(String theme) {
        if (session.isThemeCompleted(theme)) {
            throw new InvalidRuntimeCommandException(
                "Esa mazmorra ya fue conquistada. Elige una diferente."
            );
        }
    }

    private GameSession createSessionPreservingCampaignProgress(String theme, String heroType) {
        GameSession newSession = GameSessionFactory.createSessionForTheme(theme, heroType);
        newSession.setHeroType(heroType);

        newSession.replaceCompletedThemes(session.completedThemes());
        newSession.setHeroSelectionLocked(session.isHeroSelectionLocked());
        inheritHeroProgressForLockedCampaign(newSession);

        return newSession;
    }

    private void inheritHeroProgressForLockedCampaign(GameSession newSession) {
        if (!session.isHeroSelectionLocked()) {
            return;
        }

        int inheritedLevel = session.player().level();
        int inheritedExperience = session.player().experience();
        int inheritedMaxHp = session.player().maxHp();
        int inheritedGold = session.player().gold();
        int inheritedDefeatedEnemies = session.player().defeatedEnemies();

        // Continúa la campaña con progreso acumulado y curación completa antes de la nueva mazmorra.
        newSession.player().restoreProgress(
            inheritedLevel,
            inheritedExperience,
            inheritedMaxHp,
            inheritedGold,
            inheritedDefeatedEnemies
        );

        List<SimpleItem> inheritedItems = session.inventory().simpleItems().stream()
            .map(item -> new SimpleItem(
                item.getNombre(),
                item.getDescripcion(),
                item.getTipo(),
                item.getValorTotal(),
                item.getPesoTotal()
            ))
            .toList();

        newSession.inventory().replaceItems(inheritedItems, session.inventory().selectedIndex());
    }

    private static int clampSlot(int slot) {
        return Math.max(MIN_SLOT, Math.min(MAX_SLOT, slot));
    }

    private void validateAttackPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredStringField(payload, "targetId", false);
    }

    private void validateDefendPayload(JsonObject payload) {
        validateEmptyPayload(payload);
    }

    private void validateUseSkillPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        if (payload.has("skill")) {
            validateOptionalStringField(payload, "skill", true);
        }
    }

    private void validateSelectItemPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredIntegerField(payload, "itemIndex", 0, Integer.MAX_VALUE);

        int index = payload.get("itemIndex").getAsInt();
        int itemCount = session.inventory().size();
        if (itemCount == 0) {
            throw new InvalidRuntimeCommandException("Inventario vacio: no hay items para seleccionar.");
        }
        if (index >= itemCount) {
            throw new InvalidRuntimeCommandException("itemIndex fuera de rango para inventario actual.");
        }
    }

    private void validateUseItemPayload(JsonObject payload) {
        validateEmptyPayload(payload);

        boolean hasIndex = payload.has("itemIndex") && !payload.get("itemIndex").isJsonNull();
        boolean hasId = payload.has("itemId") && !payload.get("itemId").isJsonNull();

        if (!hasIndex && !hasId) {
            throw new InvalidRuntimeCommandException("useItem requiere itemIndex o itemId.");
        }

        if (hasIndex) {
            validateRequiredIntegerField(payload, "itemIndex", 0, Integer.MAX_VALUE);
            int itemIndex = payload.get("itemIndex").getAsInt();
            if (itemIndex >= session.inventory().size()) {
                throw new InvalidRuntimeCommandException("itemIndex fuera de rango para inventario actual.");
            }
        }
        if (hasId) {
            validateOptionalStringField(payload, "itemId", false);
        }
    }

    private void validateFilterCategoryPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        if (payload.has("category")) {
            validateOptionalStringField(payload, "category", false);
        }
    }

    private void validateRequiredStringField(JsonObject payload, String field, boolean allowBlank) {
        JsonElement element = payload.get(field);
        if (element == null || element.isJsonNull()) {
            throw new InvalidRuntimeCommandException(field + " es obligatorio");
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw new InvalidRuntimeCommandException(field + " debe ser string");
        }
        String value = element.getAsString();
        if (!allowBlank && (value == null || value.isBlank())) {
            throw new InvalidRuntimeCommandException(field + " no puede estar vacio");
        }
    }

    private void validateOptionalStringField(JsonObject payload, String field, boolean allowBlank) {
        JsonElement element = payload.get(field);
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw new InvalidRuntimeCommandException(field + " debe ser string");
        }
        String value = element.getAsString();
        if (!allowBlank && (value == null || value.isBlank())) {
            throw new InvalidRuntimeCommandException(field + " no puede estar vacio");
        }
    }

    private void validateRequiredIntegerField(JsonObject payload, String field, int min, int max) {
        JsonElement element = payload.get(field);
        if (element == null || element.isJsonNull()) {
            throw new InvalidRuntimeCommandException(field + " es obligatorio");
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new InvalidRuntimeCommandException(field + " debe ser numero entero");
        }

        double raw = element.getAsDouble();
        if (raw % 1 != 0) {
            throw new InvalidRuntimeCommandException(field + " debe ser entero");
        }

        int value = element.getAsInt();
        if (value < min || value > max) {
            throw new InvalidRuntimeCommandException(field + " fuera de rango permitido [" + min + ", " + max + "]");
        }
    }

    private void validateOptionalIntegerField(JsonObject payload, String field, int min, int max) {
        JsonElement element = payload.get(field);
        if (element == null || element.isJsonNull()) {
            return;
        }
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new InvalidRuntimeCommandException(field + " debe ser numero entero");
        }

        double raw = element.getAsDouble();
        if (raw % 1 != 0) {
            throw new InvalidRuntimeCommandException(field + " debe ser entero");
        }

        int value = element.getAsInt();
        if (value < min || value > max) {
            throw new InvalidRuntimeCommandException(field + " fuera de rango permitido [" + min + ", " + max + "]");
        }
    }

    private static String normalizeAction(String action) {
        return action == null ? "" : action.trim();
    }

    private static String normalizeHeroType(String heroType) {
        if (heroType == null) {
            return "";
        }
        String normalized = heroType.trim().toLowerCase(Locale.ROOT);
        return SUPPORTED_HERO_TYPES.contains(normalized) ? normalized : "";
    }

    private interface JsonPayloadValidator {
        void validate(JsonObject payloadJson);
    }

    private static final class TypedCommandHandler<T> {
        private final Class<T> payloadType;
        private final Set<String> requiredJsonFields;
        private final Consumer<T> consumer;
        private final JsonPayloadValidator validator;

        private TypedCommandHandler(
            Class<T> payloadType,
            Set<String> requiredJsonFields,
            Consumer<T> consumer,
            JsonPayloadValidator validator
        ) {
            this.payloadType = payloadType;
            this.requiredJsonFields = requiredJsonFields;
            this.consumer = consumer;
            this.validator = validator;
        }

        static <T> TypedCommandHandler<T> of(
            Class<T> payloadType,
            Set<String> requiredJsonFields,
            Consumer<T> consumer,
            JsonPayloadValidator validator
        ) {
            return new TypedCommandHandler<>(payloadType, requiredJsonFields, consumer, validator);
        }

        void handle(JsonObject payloadJson, Gson gson) {
            for (String requiredField : requiredJsonFields) {
                if (!payloadJson.has(requiredField) || payloadJson.get(requiredField).isJsonNull()) {
                    throw new InvalidRuntimeCommandException("Falta campo requerido en payload: " + requiredField);
                }
            }

            validator.validate(payloadJson);

            T payload = gson.fromJson(payloadJson, payloadType);
            consumer.accept(payload);
        }
    }
}