package game.application.state;

import game.balance.GameBalance;
import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.exploration.Dungeon;
import game.domain.inventory.Inventory;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.ArqueroFactory;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.personaje.factory.MagoFactory;
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

import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Random;

/**
 * Fabrica de sesion inicial para la UI web.
 */
public final class GameSessionFactory {
    private static final String HERO_TYPE_GUERRERO = "guerrero";
    private static final String HERO_TYPE_MAGO = "mago";
    private static final String HERO_TYPE_ARQUERO = "arquero";

    private GameSessionFactory() {
    }

    public static GameSession createDemoSession() {
        return createSessionForTheme("fire");
    }

    public static GameSession createInitialMenuSession() {
        GameSession session = createSessionForTheme("fire", HERO_TYPE_GUERRERO);
        session.setBootstrapMenuSession(true);
        session.setActiveScreen("menu");
        session.appendEvent("Selecciona una mazmorra para iniciar tu aventura.");
        return session;
    }

    public static GameSession createSessionForTheme(String themeKey) {
        return createSessionForTheme(themeKey, HERO_TYPE_GUERRERO);
    }

    public static GameSession createSessionForTheme(String themeKey, String heroType) {
        Random random = new Random();

        String normalizedHeroType = normalizeHeroType(heroType);
        Player player = createPlayerForHero(normalizedHeroType);
        DungeonThemeFactory theme = resolveThemeFactory(themeKey);
        Dungeon dungeon = Dungeon.fromTheme(random, theme);
        TurnManager turnManager = new TurnManager();
        Combat combat = new Combat(player, turnManager, random);

        EventManager eventManager = EventManager.getInstance();
        GameCaretaker caretaker = new GameCaretaker(resolveSaveDirectory());

        GameSession session = new GameSession(player, dungeon, combat, eventManager, caretaker);
        session.setHeroType(normalizedHeroType);
        session.appendEvent("Partida UI iniciada para " + player.name() + ".");
        session.appendEvent("Mazmorra: " + dungeon.model().getNombre() + " (Tema: " + dungeon.themeName() + ").");

        eventManager.notificar(new GameEvent(EventType.JUEGO_INICIADO)
            .agregarDato("heroe", player.name())
            .agregarDato("tema", dungeon.themeName()));

        return session;
    }

    private static Player createPlayerForHero(String heroType) {
        GameBalance.HeroProfile profile = GameBalance.hero(heroType);

        Personaje hero = switch (profile.type()) {
            case HERO_TYPE_MAGO -> new MagoFactory(profile.hp(), profile.attack()).crearPersonaje(profile.displayName());
            case HERO_TYPE_ARQUERO -> new ArqueroFactory(profile.hp(), profile.attack()).crearPersonaje(profile.displayName());
            default -> new GuerreroFactory(profile.hp(), profile.attack()).crearPersonaje(profile.displayName());
        };
        return new Player(hero, Inventory.demo());
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

    private static String normalizeHeroType(String heroType) {
        String normalized = normalize(heroType);
        return switch (normalized) {
            case HERO_TYPE_MAGO -> HERO_TYPE_MAGO;
            case HERO_TYPE_ARQUERO -> HERO_TYPE_ARQUERO;
            default -> HERO_TYPE_GUERRERO;
        };
    }

    private static String resolveSaveDirectory() {
        Path cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        Path projectSaves;

        if ("dungeon-crawler-patterns".equals(cwd.getFileName() != null ? cwd.getFileName().toString() : "")) {
            projectSaves = cwd.resolve("game-saves");
        } else {
            Path nestedProject = cwd.resolve("dungeon-crawler-patterns");
            if (nestedProject.toFile().isDirectory()) {
                projectSaves = nestedProject.resolve("game-saves");
            } else {
                projectSaves = cwd.resolve("game-saves");
            }
        }

        return projectSaves.toString();
    }
}
