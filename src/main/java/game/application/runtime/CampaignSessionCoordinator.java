package game.application.runtime;

import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.items.model.SimpleItem;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Coordina reglas de campana al iniciar una nueva sesion de runtime.
 *
 * Responsabilidad:
 * - Validar orden de mazmorras y bloqueo de heroe durante continuidad de campana.
 * - Heredar progreso persistente (nivel, inventario y temas completados) al crear nueva sesion.
 *
 * No hace:
 * - persistencia en disco de partidas.
 * - validacion estructural de payloads JSON.
 */
final class CampaignSessionCoordinator {

    private final Set<String> supportedThemeKeys;
    private final Set<String> supportedHeroTypes;

    CampaignSessionCoordinator(Set<String> supportedThemeKeys, Set<String> supportedHeroTypes) {
        this.supportedThemeKeys = supportedThemeKeys;
        this.supportedHeroTypes = supportedHeroTypes;
    }

    String resolveThemeOrDefault(GameSession session, String rawTheme) {
        String nextTheme = session.nextCampaignTheme();
        String fallback = nextTheme.isBlank() ? "poison" : nextTheme;
        String theme = rawTheme != null ? rawTheme.trim().toLowerCase(Locale.ROOT) : fallback;
        return supportedThemeKeys.contains(theme) ? theme : fallback;
    }

    String resolveHeroTypeForNewRun(GameSession session, String payloadHeroType) {
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

    String resolveHeroNameForNewRun(GameSession session, String payloadHeroName) {
        String requestedHeroName = normalizeHeroName(payloadHeroName);
        if (requestedHeroName.isBlank()) {
            throw new InvalidRuntimeCommandException("heroName invalido. Debe tener entre 3 y 24 caracteres");
        }

        if (session.isHeroSelectionLocked()) {
            String lockedHeroName = normalizeHeroName(session.player().name());
            if (lockedHeroName.isBlank()) {
                return requestedHeroName;
            }

            if (!lockedHeroName.equalsIgnoreCase(requestedHeroName)) {
                throw new InvalidRuntimeCommandException(
                    "No puedes cambiar el nombre del heroe despues de completar una mazmorra."
                );
            }

            // Mantiene el casing original ya persistido en la campaña.
            return session.player().name();
        }

        return requestedHeroName;
    }

    void ensureThemeAvailableForCampaign(GameSession session, String theme) {
        String nextTheme = session.nextCampaignTheme();
        if (nextTheme.isBlank()) {
            throw new InvalidRuntimeCommandException(
                "La campana ya fue completada. Inicia una nueva o carga un guardado anterior."
            );
        }

        if (session.isThemeCompleted(theme)) {
            throw new InvalidRuntimeCommandException(
                "Esa mazmorra ya fue conquistada. Elige una diferente."
            );
        }

        if (!nextTheme.equals(theme)) {
            throw new InvalidRuntimeCommandException(
                "Orden de campana invalido. Debes completar primero "
                    + themeToBossName(nextTheme)
                    + "."
            );
        }
    }

    GameSession createSessionPreservingCampaignProgress(
        GameSession currentSession,
        String theme,
        String heroType,
        String heroName
    ) {
        GameSession newSession = GameSessionFactory.createSessionForThemeRandomized(theme, heroType, heroName);
        newSession.setHeroType(heroType);

        newSession.replaceCompletedThemes(currentSession.completedThemes());
        newSession.setHeroSelectionLocked(currentSession.isHeroSelectionLocked());
        inheritHeroProgressForLockedCampaign(currentSession, newSession);

        return newSession;
    }

    String normalizeHeroType(String heroType) {
        if (heroType == null) {
            return "";
        }
        String normalized = heroType.trim().toLowerCase(Locale.ROOT);
        return supportedHeroTypes.contains(normalized) ? normalized : "";
    }

    String normalizeHeroName(String heroName) {
        if (heroName == null) {
            return "";
        }

        return heroName.trim().replaceAll("\\s+", " ");
    }

    private void inheritHeroProgressForLockedCampaign(GameSession currentSession, GameSession newSession) {
        if (!currentSession.isHeroSelectionLocked()) {
            return;
        }

        int inheritedLevel = currentSession.player().level();
        int inheritedExperience = currentSession.player().experience();
        int inheritedMaxHp = currentSession.player().maxHp();
        int inheritedGold = currentSession.player().gold();
        int inheritedDefeatedEnemies = currentSession.player().defeatedEnemies();
        int inheritedResource = currentSession.player().resource();

        // Continúa la campaña con progreso acumulado y curación completa antes de la nueva mazmorra.
        newSession.player().restoreProgress(
            inheritedLevel,
            inheritedExperience,
            inheritedMaxHp,
            inheritedGold,
            inheritedDefeatedEnemies,
            inheritedResource
        );

        List<SimpleItem> inheritedItems = currentSession.inventory().simpleItems().stream()
            .map(item -> new SimpleItem(
                item.getNombre(),
                item.getDescripcion(),
                item.getTipo(),
                item.getValorTotal(),
                item.getPesoTotal()
            ))
            .toList();

        newSession.inventory().replaceItems(inheritedItems, currentSession.inventory().selectedIndex());
    }

    private static String themeToBossName(String themeKey) {
        return switch (themeKey) {
            case "poison" -> "Arachnovex";
            case "ice" -> "Kryovaleth";
            case "fire" -> "Pyraxis";
            case "dark" -> "Malachar";
            default -> "la siguiente mazmorra";
        };
    }
}
