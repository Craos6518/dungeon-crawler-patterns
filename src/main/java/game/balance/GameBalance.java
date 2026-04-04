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
        "poison", new BossProfile("poison", "Arachnovex", 150, 21, 18, 18),
        "ice", new BossProfile("ice", "Kryovaleth", 190, 25, 32, 20),
        "fire", new BossProfile("fire", "Pyraxis", 225, 29, 36, 23),
        "dark", new BossProfile("dark", "Malachar", 290, 33, 40, 27)
    );

    private static final Map<String, ResourceProfile> RESOURCES = Map.of(
        "guerrero", new ResourceProfile("stamina", 100, 18, 14, 26, 8, 12),
        "mago", new ResourceProfile("mana", 120, 22, 16, 34, 6, 10),
        "arquero", new ResourceProfile("concentracion", 110, 16, 12, 28, 10, 14)
    );

    private GameBalance() {
    }

    public static HeroProfile hero(String heroType) {
        return HEROES.getOrDefault(normalize(heroType), HEROES.get("guerrero"));
    }

    public static BossProfile boss(String themeKey) {
        return BOSSES.getOrDefault(normalize(themeKey), BOSSES.get("fire"));
    }

    public static ResourceProfile resource(String heroType) {
        return RESOURCES.getOrDefault(normalize(heroType), RESOURCES.get("guerrero"));
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
        int attack,
        int defense,
        int speed
    ) {
    }

    public record ResourceProfile(
        String resourceName,
        int max,
        int attackRecovery,
        int defendRecovery,
        int skillCost,
        int buffCost,
        int checkpointCost
    ) {
    }
}
