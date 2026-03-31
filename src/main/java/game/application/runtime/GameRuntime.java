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
import game.application.usecase.SaveGameUseCase;
import game.application.usecase.SearchTreasureUseCase;
import game.application.usecase.SelectInventoryItemUseCase;
import game.application.usecase.UseItemUseCase;
import game.application.usecase.UseSkillUseCase;
import game.ui.GameViewModel;
import game.ui.integration.GamePresenter;

import java.util.LinkedHashMap;
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
    private static final int MIN_SLOT = 1;
    private static final int MAX_SLOT = 3;

    private final Gson gson;
    private final GamePresenter presenter;
    private GameSession session;
    private int preferredSaveSlot;

    private AdvanceTurnUseCase advanceTurnUseCase;
    private SearchTreasureUseCase searchTreasureUseCase;
    private SaveGameUseCase saveGameUseCase;
    private LoadGameUseCase loadGameUseCase;
    private ForceCombatUseCase forceCombatUseCase;
    private AttackUseCase attackUseCase;
    private DefendUseCase defendUseCase;
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
        this.loadGameUseCase = new LoadGameUseCase(session);
        this.forceCombatUseCase = new ForceCombatUseCase(session);
        this.attackUseCase = new AttackUseCase(session);
        this.defendUseCase = new DefendUseCase(session);
        this.useItemUseCase = new UseItemUseCase(session);
        this.useSkillUseCase = new UseSkillUseCase(session);
        this.selectInventoryItemUseCase = new SelectInventoryItemUseCase(session);
        this.moveInventorySelectionUseCase = new MoveInventorySelectionUseCase(session);
    }

    private Map<String, TypedCommandHandler<?>> registerHandlers() {
        Map<String, TypedCommandHandler<?>> map = new LinkedHashMap<>();

        map.put("startGame", TypedCommandHandler.of(StartGameCommandRequest.class, Set.of(), payload -> {
            GameSession newSession = GameSessionFactory.createSessionForTheme(payload.theme);
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

        map.put("closeInventory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            session.setActiveScreen(session.hasActiveEnemy() ? "combat" : "exploration");
        }, this::validateEmptyPayload));

        map.put("saveGame", TypedCommandHandler.of(SaveGameCommandRequest.class, Set.of(), payload -> {
            int slot = resolveSlotOrPreferred(payload.slot);
            saveGameUseCase.execute(slot);
            preferredSaveSlot = slot;
        }, this::validateSavePayload));

        map.put("loadGame", TypedCommandHandler.of(LoadGameCommandRequest.class, Set.of(), payload -> {
            int slot = resolveSlotOrPreferred(payload.slot);
            loadGameUseCase.execute(slot);
            preferredSaveSlot = slot;
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

        map.put("useItem", TypedCommandHandler.of(UseItemCommandRequest.class, Set.of(), payload -> {
            useItemUseCase.execute(payload);
        }, this::validateUseItemPayload));

        map.put("useSkill", TypedCommandHandler.of(UseSkillCommandRequest.class, Set.of(), payload -> {
            useSkillUseCase.execute(payload);
        }, this::validateUseSkillPayload));

        map.put("selectItem", TypedCommandHandler.of(SelectItemCommandRequest.class, Set.of("itemIndex"), payload -> {
            selectInventoryItemUseCase.execute(payload.itemIndex);
        }, this::validateSelectItemPayload));

        map.put("inventoryUp", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveUp();
        }, this::validateEmptyPayload));

        map.put("inventoryDown", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            moveInventorySelectionUseCase.moveDown();
        }, this::validateEmptyPayload));

        map.put("rerenderCurrentScreen", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op: la UI recibe estado completo en cada push.
        }, this::validateEmptyPayload));

        map.put("filterCategory", TypedCommandHandler.of(JsonObject.class, Set.of(), payload -> {
            // No-op: filtro de categoria se resuelve del lado de UI.
        }, this::validateFilterCategoryPayload));

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
            return;
        }

        validateOptionalStringField(payload, "theme", false);
        String normalizedTheme = payload.get("theme").getAsString().trim().toLowerCase(Locale.ROOT);
        if (!SUPPORTED_THEME_KEYS.contains(normalizedTheme)) {
            throw new InvalidRuntimeCommandException(
                "theme invalido. Valores permitidos: " + String.join(", ", SUPPORTED_THEME_KEYS)
            );
        }
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

    private int resolveSlotOrPreferred(Integer requestedSlot) {
        if (requestedSlot == null) {
            return preferredSaveSlot;
        }
        return clampSlot(requestedSlot);
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