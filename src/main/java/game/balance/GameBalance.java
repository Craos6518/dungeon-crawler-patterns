package game.balance;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

/**
 * Balance centralizado del juego para evitar valores hardcodeados dispersos.
 */
public final class GameBalance {

    private static final Map<String, HeroProfile> HEROES = Map.of(
        "guerrero", new HeroProfile("guerrero", "Guerrero", 110, 20, 26, 11),
        "mago", new HeroProfile("mago", "Mago", 65, 27, 10, 23),
        "arquero", new HeroProfile("arquero", "Arquero", 85, 23, 17, 21)
    );

    private static final Map<String, BossProfile> BOSSES = Map.of(
        "fire", new BossProfile("fire", "Pyraxis el Corazon de Ceniza", 165, 27),
        "ice", new BossProfile("ice", "Kryovaleth Vigia del Invierno", 155, 26),
        "poison", new BossProfile("poison", "Arachnovex Matriarca Toxica", 145, 23),
        "dark", new BossProfile("dark", "Malachar Heraldo del Vacio", 220, 34)
    );

    private GameBalance() {
    }

    public static HeroProfile hero(String heroType) {
        return HEROES.getOrDefault(normalize(heroType), HEROES.get("guerrero"));
    }

    public static BossProfile boss(String themeKey) {
        return BOSSES.getOrDefault(normalize(themeKey), BOSSES.get("fire"));
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").trim().toLowerCase(Locale.ROOT);
    }

    public record HeroProfile(
        String type,
        String displayName,
        int hp,
        int attack,
        int defense,
        int speed
    ) {
    }

    public record BossProfile(
        String themeKey,
        String name,
        int hp,
        int attack
    ) {
    }
}
