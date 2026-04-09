package game.application.state;

import java.util.Locale;

/**
 * Enum de estados de flujo para el runtime productivo.
 *
 * Responsabilidad:
 * - Definir el conjunto cerrado de pantallas validas en GameSession/GameRuntime.
 * - Normalizar aliases externos mediante fromScreenKey para evitar transiciones por strings arbitrarios.
 *
 * No hace:
 * - ejecutar logica de transicion.
 * - almacenar datos de sesion o UI.
 */
public enum GameFlowState {
    MENU("menu"),
    HERO("hero"),
    EXPLORATION("exploration"),
    COMBAT("combat"),
    INVENTORY("inventory"),
    SAVES("saves"),
    STATS("stats"),
    TREASURE("treasure"),
    GAME_OVER("gameover");

    private final String screenKey;

    GameFlowState(String screenKey) {
        this.screenKey = screenKey;
    }

    public String screenKey() {
        return screenKey;
    }

    public static GameFlowState fromScreenKey(String rawScreen) {
        if (rawScreen == null || rawScreen.isBlank()) {
            return EXPLORATION;
        }

        String normalized = rawScreen.trim().toLowerCase(Locale.ROOT).replace('-', '_');
        return switch (normalized) {
            case "menu", "main_menu" -> MENU;
            case "hero", "hero_select" -> HERO;
            case "exploration", "exploracion", "adventure", "post_combat", "postcombat" -> EXPLORATION;
            case "combat", "combate" -> COMBAT;
            case "inventory", "inventario" -> INVENTORY;
            case "saves", "save", "save_slots" -> SAVES;
            case "stats", "statistics", "estadisticas" -> STATS;
            case "treasure", "loot" -> TREASURE;
            case "gameover", "game_over", "victory", "defeat" -> GAME_OVER;
            default -> EXPLORATION;
        };
    }
}
