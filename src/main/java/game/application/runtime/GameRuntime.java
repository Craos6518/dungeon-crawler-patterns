package game.application.runtime;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import game.application.dto.AttackCommandRequest;
import game.application.dto.LoadGameCommandRequest;
import game.application.dto.SaveGameCommandRequest;
import game.application.dto.SelectItemCommandRequest;
import game.application.dto.StartGameCommandRequest;
import game.application.dto.UiCommand;
import game.application.dto.UseItemCommandRequest;
import game.application.dto.UseSkillCommandRequest;
import game.application.state.GameFlowState;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.application.usecase.AdvanceTurnUseCase;
import game.application.usecase.ApplyCombatBuffUseCase;
import game.application.usecase.AttackUseCase;
import game.application.usecase.DefendUseCase;
import game.application.usecase.ForceCombatUseCase;
import game.application.usecase.MoveInventorySelectionUseCase;
import game.application.usecase.RetreatCombatUseCase;
import game.application.usecase.RollbackCombatCheckpointUseCase;
import game.application.usecase.SaveCombatCheckpointUseCase;
import game.application.usecase.SearchTreasureUseCase;
import game.application.usecase.SelectInventoryItemUseCase;
import game.application.usecase.SetCombatStyleUseCase;
import game.application.usecase.UseItemUseCase;
import game.application.usecase.UseSkillUseCase;
import game.ui.GameViewModel;
import game.ui.integration.GamePresenter;

import java.util.LinkedHashMap;
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
    /** Colaborador para reglas de campaña y creación de nuevas sesiones de juego. */
    private final CampaignSessionCoordinator campaignSessionCoordinator;
    /** Colaborador para validación estructural/tipada de payloads por acción. */
    private final RuntimePayloadValidator payloadValidator;
    /** Colaborador para persistencia por slot y resolución de slot preferido. */
    private final RuntimeSaveSlotManager saveSlotManager;

    private GameSession session;

    private AdvanceTurnUseCase advanceTurnUseCase;
    private SearchTreasureUseCase searchTreasureUseCase;
    private ForceCombatUseCase forceCombatUseCase;
    private AttackUseCase attackUseCase;
    private DefendUseCase defendUseCase;
    private RetreatCombatUseCase retreatCombatUseCase;
    private SetCombatStyleUseCase setCombatStyleUseCase;
    private ApplyCombatBuffUseCase applyCombatBuffUseCase;
    private SaveCombatCheckpointUseCase saveCombatCheckpointUseCase;
    private RollbackCombatCheckpointUseCase rollbackCombatCheckpointUseCase;
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
        this.campaignSessionCoordinator = new CampaignSessionCoordinator(SUPPORTED_THEME_KEYS, SUPPORTED_HERO_TYPES);
        this.payloadValidator = new RuntimePayloadValidator(
            () -> this.session,
            SUPPORTED_THEME_KEYS,
            SUPPORTED_HERO_TYPES,
            MIN_SLOT,
            MAX_SLOT,
            campaignSessionCoordinator::normalizeHeroType
        );
        this.saveSlotManager = new RuntimeSaveSlotManager(MIN_SLOT, MAX_SLOT, SUPPORTED_HERO_TYPES);
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
        this.saveSlotManager.bindSession(session);
        this.advanceTurnUseCase = new AdvanceTurnUseCase(session);
        this.searchTreasureUseCase = new SearchTreasureUseCase(session);
        this.forceCombatUseCase = new ForceCombatUseCase(session);
        this.attackUseCase = new AttackUseCase(session);
        this.defendUseCase = new DefendUseCase(session);
        this.retreatCombatUseCase = new RetreatCombatUseCase(session);
        this.setCombatStyleUseCase = new SetCombatStyleUseCase(session);
        this.applyCombatBuffUseCase = new ApplyCombatBuffUseCase(session);
        this.saveCombatCheckpointUseCase = new SaveCombatCheckpointUseCase(session);
        this.rollbackCombatCheckpointUseCase = new RollbackCombatCheckpointUseCase(session);
        this.useItemUseCase = new UseItemUseCase(session);
        this.useSkillUseCase = new UseSkillUseCase(session);
        this.selectInventoryItemUseCase = new SelectInventoryItemUseCase(session);
        this.moveInventorySelectionUseCase = new MoveInventorySelectionUseCase(session);
    }

    private Map<String, TypedCommandHandler<?>> registerHandlers() {
        Map<String, TypedCommandHandler<?>> map = new LinkedHashMap<>();

        map.put("startGame", TypedCommandHandler.of(StartGameCommandRequest.class, Set.of(), payload -> {
            String theme = campaignSessionCoordinator.resolveThemeOrDefault(session, payload.theme);
            campaignSessionCoordinator.ensureThemeAvailableForCampaign(session, theme);
            String heroType = campaignSessionCoordinator.resolveHeroTypeForNewRun(session, payload.heroType);
            GameSession newSession = campaignSessionCoordinator.createSessionPreservingCampaignProgress(
                session,
                theme,
                heroType
            );
            newSession.transitionTo(GameFlowState.EXPLORATION);

            bindSession(newSession);
            saveSlotManager.resetPreferredSaveSlot();
        }, payloadValidator::validateStartGamePayload));

        map.put("openMainMenu", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(GameFlowState.MENU);
            session.appendEvent("Regresas al menu principal.");
        }, payloadValidator::validateEmptyPayload));

        map.put("advanceRoom", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            advanceTurnUseCase.execute();
        }, payloadValidator::validateAdvanceRoomPayload));

        map.put("searchTreasure", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            searchTreasureUseCase.execute();
        }, payloadValidator::validateSearchTreasurePayload));

        map.put("openInventory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(GameFlowState.INVENTORY);
            session.inventory().clampSelection();
            session.appendEvent("Inventario abierto.");
        }, payloadValidator::validateEmptyPayload));

        map.put("toggleInventory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            if (session.activeState() == GameFlowState.INVENTORY) {
                session.transitionTo(session.hasActiveEnemy() ? GameFlowState.COMBAT : GameFlowState.EXPLORATION);
            } else {
                session.transitionTo(GameFlowState.INVENTORY);
                session.inventory().clampSelection();
                session.appendEvent("Inventario abierto.");
            }
        }, payloadValidator::validateEmptyPayload));

        map.put("closeInventory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(session.hasActiveEnemy() ? GameFlowState.COMBAT : GameFlowState.EXPLORATION);
        }, payloadValidator::validateEmptyPayload));

        map.put("saveGame", TypedCommandHandler.of(SaveGameCommandRequest.class, Set.of(), payload -> {
            saveSlotManager.saveToSlot(payload.slot);
        }, payloadValidator::validateSavePayload));

        map.put("loadGame", TypedCommandHandler.of(LoadGameCommandRequest.class, Set.of(), payload -> {
            bindSession(saveSlotManager.loadFromSlot(payload.slot));
        }, payloadValidator::validateLoadPayload));

        map.put("quickSave", TypedCommandHandler.of(SaveGameCommandRequest.class, Set.of(), payload -> {
            saveSlotManager.saveToSlot(payload.slot);
        }, payloadValidator::validateSavePayload));

        map.put("quickLoad", TypedCommandHandler.of(LoadGameCommandRequest.class, Set.of(), payload -> {
            bindSession(saveSlotManager.loadFromSlot(payload.slot));
        }, payloadValidator::validateLoadPayload));

        map.put("forceCombat", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            forceCombatUseCase.execute();
        }, payloadValidator::validateEmptyPayload));

        map.put("attack", TypedCommandHandler.of(AttackCommandRequest.class, Set.of(), payload -> {
            attackUseCase.execute(payload);
        }, payloadValidator::validateAttackPayload));

        map.put("defend", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            defendUseCase.execute();
        }, payloadValidator::validateDefendPayload));

        map.put("retreatCombat", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            retreatCombatUseCase.execute();
        }, payloadValidator::validateDefendPayload));

        map.put("setCombatStyle", TypedCommandHandler.of(JsonObject.class, Set.of("style"), payload -> {
            setCombatStyleUseCase.execute(payload.get("style").getAsString());
        }, payloadValidator::validateSetCombatStylePayload));

        map.put("applyBuff", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            String buffType = payload.has("type") && !payload.get("type").isJsonNull()
                ? payload.get("type").getAsString()
                : "power";
            applyCombatBuffUseCase.execute(buffType);
        }, payloadValidator::validateApplyBuffPayload));

        map.put("saveCombatCheckpoint", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            saveCombatCheckpointUseCase.execute();
        }, payloadValidator::validateDefendPayload));

        map.put("rollbackCombatCheckpoint", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            rollbackCombatCheckpointUseCase.execute();
        }, payloadValidator::validateDefendPayload));

        map.put("useItem", TypedCommandHandler.of(UseItemCommandRequest.class, Set.of(), payload -> {
            useItemUseCase.execute(payload);
        }, payloadValidator::validateUseItemPayload));

        map.put("consumeSelectedItem", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            executeConsumeSelectedItem();
        }, payloadValidator::validateEmptyPayload));

        map.put("useSkill", TypedCommandHandler.of(UseSkillCommandRequest.class, Set.of(), payload -> {
            useSkillUseCase.execute(payload);
        }, payloadValidator::validateUseSkillPayload));

        map.put("selectItem", TypedCommandHandler.of(SelectItemCommandRequest.class, Set.of("itemIndex"), payload -> {
            selectInventoryItemUseCase.execute(payload.itemIndex);
        }, payloadValidator::validateSelectItemPayload));

        map.put("inventoryUp", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveUp();
        }, payloadValidator::validateEmptyPayload));

        map.put("inventoryPrevious", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveUp();
        }, payloadValidator::validateEmptyPayload));

        map.put("inventoryDown", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveDown();
        }, payloadValidator::validateEmptyPayload));

        map.put("inventoryNext", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveDown();
        }, payloadValidator::validateEmptyPayload));

        map.put("rerenderCurrentScreen", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // Stub explicito: la UI recibe estado completo en cada push.
        }, payloadValidator::validateEmptyPayload));

        map.put("filterCategory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // Stub explicito: filtro de categoria se resuelve del lado de UI.
        }, payloadValidator::validateFilterCategoryPayload));

        // ── Nuevas pantallas ────────────────────────────────────────

        map.put("goToHeroSelect", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(GameFlowState.HERO);
        }, payloadValidator::validateEmptyPayload));

        map.put("selectHero", TypedCommandHandler.of(StartGameCommandRequest.class, Set.of(), payload -> {
            String heroType = campaignSessionCoordinator.normalizeHeroType(payload.heroType);
            if (heroType.isBlank()) {
                return;
            }

            if (session.isHeroSelectionLocked()) {
                String lockedHero = campaignSessionCoordinator.normalizeHeroType(session.heroType());
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
        }, payloadValidator::validateSelectHeroPayload));

        map.put("heroNewGame", TypedCommandHandler.of(StartGameCommandRequest.class, Set.of(), payload -> {
            String theme = campaignSessionCoordinator.resolveThemeOrDefault(session, payload.theme);
            campaignSessionCoordinator.ensureThemeAvailableForCampaign(session, theme);
            String heroType = campaignSessionCoordinator.resolveHeroTypeForNewRun(session, payload.heroType);
            GameSession newSession = campaignSessionCoordinator.createSessionPreservingCampaignProgress(
                session,
                theme,
                heroType
            );
            newSession.transitionTo(GameFlowState.EXPLORATION);
            bindSession(newSession);
            saveSlotManager.resetPreferredSaveSlot();
        }, payloadValidator::validateStartGamePayload));

        map.put("showStats", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(GameFlowState.STATS);
        }, payloadValidator::validateEmptyPayload));

        map.put("closeStats", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(GameFlowState.MENU);
        }, payloadValidator::validateEmptyPayload));

        map.put("openSaves", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(GameFlowState.SAVES);
        }, payloadValidator::validateEmptyPayload));

        map.put("saveToSlot", TypedCommandHandler.of(SaveGameCommandRequest.class, Set.of(), payload -> {
            saveSlotManager.saveToSlot(payload.slot);
        }, payloadValidator::validateSavePayload));

        map.put("loadFromSlot", TypedCommandHandler.of(LoadGameCommandRequest.class, Set.of(), payload -> {
            bindSession(saveSlotManager.loadFromSlot(payload.slot));
        }, payloadValidator::validateLoadPayload));

        map.put("restoreGame", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // Carga el slot preferido (ultimo usado) al restaurar tras game over.
            bindSession(saveSlotManager.loadFromSlot(saveSlotManager.preferredSaveSlot()));
        }, payloadValidator::validateEmptyPayload));

        map.put("newGame", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.transitionTo(GameFlowState.HERO);
        }, payloadValidator::validateEmptyPayload));

        map.put("exitGame", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op en web: el cierre lo maneja la aplicacion JavaFX.
            LOGGER.info("Comando exitGame recibido desde UI.");
        }, payloadValidator::validateEmptyPayload));

        // ── Loot / Tesoro ───────────────────────────────────────────────
        map.put("takeLoot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.takeSelectedTreasure();
        }, payloadValidator::validateEmptyPayload));

        map.put("selectLoot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // Handler activo: selecciona botin real de la sala de tesoro.
            Integer lootIndex = payload.has("lootIndex") && !payload.get("lootIndex").isJsonNull()
                ? payload.get("lootIndex").getAsInt()
                : 0;
            session.selectTreasureLoot(lootIndex);
        }, payloadValidator::validateSelectLootPayload));

        map.put("skipLoot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.skipTreasure();
        }, payloadValidator::validateEmptyPayload));

        map.put("selectSaveSlot", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // Stub explicito: la seleccion de slot se maneja localmente en JS.
        }, payloadValidator::validateEmptyPayload));

        return map;
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

    private static String normalizeAction(String action) {
        return action == null ? "" : action.trim();
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