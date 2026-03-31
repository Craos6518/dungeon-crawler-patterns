package game.application.state;

import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.exploration.Dungeon;
import game.domain.turn.TurnManager;
import game.dungeon.theme.DarkThemeFactory;
import game.dungeon.theme.DungeonThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.dungeon.theme.IceThemeFactory;
import game.dungeon.theme.PoisonThemeFactory;
import game.events.observer.EventManager;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.persistence.memento.GameCaretaker;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;

/**
 * Fabrica de sesion inicial para la UI web.
 */
public final class GameSessionFactory {

    private GameSessionFactory() {
    }

    public static GameSession createDemoSession() {
        return createSessionForTheme("fire");
    }

    public static GameSession createInitialMenuSession() {
        GameSession session = createSessionForTheme("fire");
        session.setActiveScreen("menu");
        session.appendEvent("Selecciona una mazmorra para iniciar tu aventura.");
        return session;
    }

    public static GameSession createSessionForTheme(String themeKey) {
        Random random = new Random();

        Player player = Player.demo();
        DungeonThemeFactory theme = resolveThemeFactory(themeKey);
        Dungeon dungeon = Dungeon.fromTheme(random, theme);
        TurnManager turnManager = new TurnManager();
        Combat combat = new Combat(player, turnManager, random);

        EventManager eventManager = EventManager.getInstance();
        GameCaretaker caretaker = new GameCaretaker("./game-saves/");

        GameSession session = new GameSession(player, dungeon, combat, eventManager, caretaker);
        session.appendEvent("Partida UI iniciada para " + player.name() + ".");
        session.appendEvent("Mazmorra: " + dungeon.model().getNombre() + " (Tema: " + dungeon.themeName() + ").");

        eventManager.notificar(new GameEvent(EventType.JUEGO_INICIADO)
            .agregarDato("heroe", player.name())
            .agregarDato("tema", dungeon.themeName()));

        return session;
    }

    private static DungeonThemeFactory resolveThemeFactory(String themeKey) {
        String normalized = normalize(themeKey);
        return switch (normalized) {
            case "ice", "hielo" -> new IceThemeFactory();
            case "poison", "veneno" -> new PoisonThemeFactory();
            case "dark", "oscuridad", "oscuro" -> new DarkThemeFactory();
            default -> new FireThemeFactory();
        };
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").trim().toLowerCase(Locale.ROOT);
    }
}
