package game.domain.combat;

import java.util.Locale;

/**
 * Strategy del jugador para modificar riesgo/recompensa durante el combate.
 */
public enum PlayerCombatStyle {
    BALANCED("balanced", "Balanceado", 1.00, 1.00, 1.00),
    AGGRESSIVE("aggressive", "Agresivo", 1.25, 1.20, 1.15),
    DEFENSIVE("defensive", "Defensivo", 0.85, 0.75, 0.90);

    private final String key;
    private final String displayName;
    private final double outgoingDamageMultiplier;
    private final double incomingDamageMultiplier;
    private final double resourceCostMultiplier;

    PlayerCombatStyle(
        String key,
        String displayName,
        double outgoingDamageMultiplier,
        double incomingDamageMultiplier,
        double resourceCostMultiplier
    ) {
        this.key = key;
        this.displayName = displayName;
        this.outgoingDamageMultiplier = outgoingDamageMultiplier;
        this.incomingDamageMultiplier = incomingDamageMultiplier;
        this.resourceCostMultiplier = resourceCostMultiplier;
    }

    public String key() {
        return key;
    }

    public String displayName() {
        return displayName;
    }

    public double outgoingDamageMultiplier() {
        return outgoingDamageMultiplier;
    }

    public double incomingDamageMultiplier() {
        return incomingDamageMultiplier;
    }

    public double resourceCostMultiplier() {
        return resourceCostMultiplier;
    }

    public static PlayerCombatStyle fromRaw(String raw) {
        if (raw == null) {
            return BALANCED;
        }

        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        for (PlayerCombatStyle style : values()) {
            if (style.key.equals(normalized)) {
                return style;
            }
        }
        return BALANCED;
    }
}
