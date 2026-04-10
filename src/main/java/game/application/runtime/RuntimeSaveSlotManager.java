package game.application.runtime;

import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.SaveGameUseCase;
import game.application.state.GameMemento;
import game.application.ports.persistence.SaveSlotNotFoundException;

import java.util.Locale;
import java.util.Set;

/**
 * Gestiona persistencia por slots para el runtime productivo.
 *
 * Responsabilidad:
 * - Resolver el slot objetivo (explicito o preferido) para operaciones save/load.
 * - Reconstruir una sesion consistente antes de restaurar memento (tema, heroe y semilla).
 *
 * No hace:
 * - validaciones de negocio fuera del dominio de slots.
 * - orquestacion de UI ni transiciones de flujo de pantalla.
 */
final class RuntimeSaveSlotManager {

    private static final String DEFAULT_THEME = "fire";
    private static final String DEFAULT_HERO = "guerrero";

    private final int minSlot;
    private final int maxSlot;
    private final Set<String> supportedHeroTypes;

    private GameSession session;
    private SaveGameUseCase saveGameUseCase;
    private int preferredSaveSlot;

    RuntimeSaveSlotManager(int minSlot, int maxSlot, Set<String> supportedHeroTypes) {
        this.minSlot = minSlot;
        this.maxSlot = maxSlot;
        this.supportedHeroTypes = supportedHeroTypes;
        this.preferredSaveSlot = minSlot;
    }

    void bindSession(GameSession session) {
        this.session = session;
        this.saveGameUseCase = new SaveGameUseCase(session);
    }

    void resetPreferredSaveSlot() {
        this.preferredSaveSlot = minSlot;
    }

    int preferredSaveSlot() {
        return preferredSaveSlot;
    }

    void selectPreferredSlot(Integer requestedSlot) {
        if (requestedSlot == null) {
            throw new InvalidRuntimeCommandException("slot es obligatorio");
        }

        preferredSaveSlot = validateSlotOrThrow(requestedSlot);
    }

    void saveToSlot(Integer requestedSlot) {
        int slot = resolveSlotOrPreferred(requestedSlot);
        saveGameUseCase.execute(slot);
        preferredSaveSlot = slot;
    }

    GameSession loadFromSlot(Integer requestedSlot) {
        int slot = resolveSlotOrPreferred(requestedSlot);
        GameSession loadedSession = loadSessionFromSlot(slot);
        preferredSaveSlot = slot;
        return loadedSession;
    }

    private int resolveSlotOrPreferred(Integer requestedSlot) {
        if (requestedSlot == null) {
            return preferredSaveSlot;
        }
        return validateSlotOrThrow(requestedSlot);
    }

    private GameSession loadSessionFromSlot(int slot) {
        String fileName = "Slot_" + slot;
        if (!session.caretaker().existeEnDisco(fileName)) {
            throw new SaveSlotNotFoundException("Slot vacio: " + fileName + ".save no existe.");
        }
        GameMemento memento = session.caretaker().cargarDesdeDisco(fileName);

        String theme = resolveThemeFromMemento(memento);
        String heroType = resolveHeroTypeFromMemento(memento);
        Long generationSeed = resolveGenerationSeedFromMemento(memento);
        GameSession restoredSession = generationSeed == null
            ? GameSessionFactory.createSessionForTheme(theme, heroType)
            : GameSessionFactory.createSessionForTheme(theme, heroType, generationSeed);
        new LoadGameUseCase(restoredSession).restoreFromMemento(fileName, memento);
        return restoredSession;
    }

    private static String resolveThemeFromMemento(GameMemento memento) {
        if (memento == null || memento.getEstadoMazmorra() == null) {
            return DEFAULT_THEME;
        }

        Object rawTheme = memento.getEstadoMazmorra().get("tema");
        if (rawTheme == null) {
            return DEFAULT_THEME;
        }

        String theme = String.valueOf(rawTheme).trim();
        return theme.isBlank() ? DEFAULT_THEME : theme;
    }

    private String resolveHeroTypeFromMemento(GameMemento memento) {
        if (memento == null || memento.getEstadoPersonaje() == null) {
            return DEFAULT_HERO;
        }

        Object rawHeroType = memento.getEstadoPersonaje().get("heroType");
        if (rawHeroType == null) {
            return DEFAULT_HERO;
        }

        String heroType = String.valueOf(rawHeroType).trim().toLowerCase(Locale.ROOT);
        if (supportedHeroTypes.contains(heroType)) {
            return heroType;
        }
        return DEFAULT_HERO;
    }

    private static Long resolveGenerationSeedFromMemento(GameMemento memento) {
        if (memento == null || memento.getEstadoMazmorra() == null) {
            return null;
        }

        Object rawSeed = memento.getEstadoMazmorra().get("generationSeed");
        if (rawSeed == null) {
            return null;
        }

        if (rawSeed instanceof Number number) {
            return number.longValue();
        }

        try {
            return Long.parseLong(String.valueOf(rawSeed));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int validateSlotOrThrow(int slot) {
        if (slot < minSlot || slot > maxSlot) {
            throw new InvalidRuntimeCommandException(
                "slot fuera de rango permitido [" + minSlot + ", " + maxSlot + "]"
            );
        }
        return slot;
    }
}
