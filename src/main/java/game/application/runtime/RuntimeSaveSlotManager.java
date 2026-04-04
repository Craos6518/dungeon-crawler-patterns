package game.application.runtime;

import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.SaveGameUseCase;
import game.persistence.memento.GameMemento;
import game.persistence.memento.SaveSlotNotFoundException;

import java.util.Locale;
import java.util.Set;

/**
 * Administra guardado/carga por slots y el slot preferido de runtime.
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

    private int clampSlot(int slot) {
        return Math.max(minSlot, Math.min(maxSlot, slot));
    }
}
