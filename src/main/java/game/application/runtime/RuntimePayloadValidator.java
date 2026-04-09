package game.application.runtime;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import game.application.state.GameSession;

import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Valida comandos entrantes en runtime.
 *
 * Responsabilidad:
 * - Garantizar invariantes estructurales y tipadas del payload antes de invocar casos de uso.
 * - Rechazar campos fuera de rango (slots, indices, estilos y tipos soportados).
 *
 * No hace:
 * - logica de negocio de campana, combate o inventario.
 * - mutaciones de estado sobre la sesion activa.
 */
final class RuntimePayloadValidator {

    private final Supplier<GameSession> sessionSupplier;
    private final Set<String> supportedThemeKeys;
    private final Set<String> supportedHeroTypes;
    private final int minSlot;
    private final int maxSlot;
    private final Function<String, String> heroTypeNormalizer;

    RuntimePayloadValidator(
        Supplier<GameSession> sessionSupplier,
        Set<String> supportedThemeKeys,
        Set<String> supportedHeroTypes,
        int minSlot,
        int maxSlot,
        Function<String, String> heroTypeNormalizer
    ) {
        this.sessionSupplier = sessionSupplier;
        this.supportedThemeKeys = supportedThemeKeys;
        this.supportedHeroTypes = supportedHeroTypes;
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.heroTypeNormalizer = heroTypeNormalizer;
    }

    void validateEmptyPayload(JsonObject payload) {
        if (payload == null) {
            throw new InvalidRuntimeCommandException("Payload obligatorio.");
        }
    }

    void validateSavePayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateOptionalIntegerField(payload, "slot", minSlot, maxSlot);
    }

    void validateStartGamePayload(JsonObject payload) {
        validateEmptyPayload(payload);

        if (payload.has("theme") && !payload.get("theme").isJsonNull()) {
            validateOptionalStringField(payload, "theme", false);
            String normalizedTheme = payload.get("theme").getAsString().trim().toLowerCase(Locale.ROOT);
            if (!supportedThemeKeys.contains(normalizedTheme)) {
                throw new InvalidRuntimeCommandException(
                    "theme invalido. Valores permitidos: " + String.join(", ", supportedThemeKeys)
                );
            }
        }

        if (payload.has("heroType") && !payload.get("heroType").isJsonNull()) {
            validateOptionalStringField(payload, "heroType", false);
            String normalizedHeroType = normalizeHeroType(payload.get("heroType").getAsString());
            if (normalizedHeroType.isBlank()) {
                throw new InvalidRuntimeCommandException(
                    "heroType invalido. Valores permitidos: " + String.join(", ", supportedHeroTypes)
                );
            }
        }
    }

    void validateSelectSaveSlotPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredIntegerField(payload, "slot", minSlot, maxSlot);
    }

    void validateSelectHeroPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredStringField(payload, "heroType", false);
        String normalizedHeroType = normalizeHeroType(payload.get("heroType").getAsString());
        if (normalizedHeroType.isBlank()) {
            throw new InvalidRuntimeCommandException(
                "heroType invalido. Valores permitidos: " + String.join(", ", supportedHeroTypes)
            );
        }
    }

    void validateSelectLootPayload(JsonObject payload) {
        validateEmptyPayload(payload);

        GameSession session = sessionSupplier.get();
        if (!session.hasPendingTreasure()) {
            throw new InvalidRuntimeCommandException("No hay una sala de tesoro activa.");
        }

        validateOptionalIntegerField(payload, "lootIndex", 0, Integer.MAX_VALUE);

        if (payload.has("lootIndex") && !payload.get("lootIndex").isJsonNull()) {
            int requested = payload.get("lootIndex").getAsInt();
            int lootSize = session.treasureLootOptions().size();
            if (lootSize <= 0) {
                throw new InvalidRuntimeCommandException("No hay botin disponible para seleccionar.");
            }
            if (requested >= lootSize) {
                throw new InvalidRuntimeCommandException("lootIndex fuera de rango para el botin actual.");
            }
        }
    }

    void validateAdvanceRoomPayload(JsonObject payload) {
        validateEmptyPayload(payload);
    }

    void validateSearchTreasurePayload(JsonObject payload) {
        validateEmptyPayload(payload);
    }

    void validateLoadPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateOptionalIntegerField(payload, "slot", minSlot, maxSlot);
    }

    void validateAttackPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredStringField(payload, "targetId", false);
    }

    void validateDefendPayload(JsonObject payload) {
        validateEmptyPayload(payload);
    }

    void validateUseSkillPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        if (payload.has("skill")) {
            validateOptionalStringField(payload, "skill", true);
        }
    }

    void validateSetCombatStylePayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredStringField(payload, "style", false);

        String style = payload.get("style").getAsString().trim().toLowerCase(Locale.ROOT);
        if (!"balanced".equals(style) && !"aggressive".equals(style) && !"defensive".equals(style)) {
            throw new InvalidRuntimeCommandException(
                "style invalido. Valores permitidos: balanced, aggressive, defensive"
            );
        }
    }

    void validateApplyBuffPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        if (!payload.has("type") || payload.get("type").isJsonNull()) {
            return;
        }

        validateOptionalStringField(payload, "type", false);
        String type = payload.get("type").getAsString().trim().toLowerCase(Locale.ROOT);
        if (!"power".equals(type) && !"guard".equals(type) && !"defense".equals(type)) {
            throw new InvalidRuntimeCommandException(
                "type invalido. Valores permitidos: power, guard, defense"
            );
        }
    }

    void validateSelectItemPayload(JsonObject payload) {
        validateEmptyPayload(payload);
        validateRequiredIntegerField(payload, "itemIndex", 0, Integer.MAX_VALUE);

        int index = payload.get("itemIndex").getAsInt();
        int itemCount = sessionSupplier.get().inventory().size();
        if (itemCount == 0) {
            throw new InvalidRuntimeCommandException("Inventario vacio: no hay items para seleccionar.");
        }
        if (index >= itemCount) {
            throw new InvalidRuntimeCommandException("itemIndex fuera de rango para inventario actual.");
        }
    }

    void validateUseItemPayload(JsonObject payload) {
        validateEmptyPayload(payload);

        boolean hasIndex = payload.has("itemIndex") && !payload.get("itemIndex").isJsonNull();
        boolean hasId = payload.has("itemId") && !payload.get("itemId").isJsonNull();

        if (!hasIndex && !hasId) {
            throw new InvalidRuntimeCommandException("useItem requiere itemIndex o itemId.");
        }

        if (hasIndex) {
            validateRequiredIntegerField(payload, "itemIndex", 0, Integer.MAX_VALUE);
            int itemIndex = payload.get("itemIndex").getAsInt();
            if (itemIndex >= sessionSupplier.get().inventory().size()) {
                throw new InvalidRuntimeCommandException("itemIndex fuera de rango para inventario actual.");
            }
        }
        if (hasId) {
            validateOptionalStringField(payload, "itemId", false);
        }
    }

    void validateFilterCategoryPayload(JsonObject payload) {
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

    private String normalizeHeroType(String heroType) {
        return heroTypeNormalizer.apply(heroType);
    }
}
