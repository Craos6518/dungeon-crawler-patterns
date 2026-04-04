package game.domain.exploration;

import game.domain.character.Enemy;
import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.DungeonBuilder;
import game.dungeon.builder.ProceduralDungeonGenerator;
import game.dungeon.theme.DungeonThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.items.model.SimpleItem;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Agregado Dungeon. Controla progreso de exploracion y consistencia de salas.
 */
public class Dungeon {

    private final Random random;
    private final DungeonThemeFactory theme;
    private final game.dungeon.model.Dungeon model;
    private final Set<Integer> treasureResolved;
    private final Set<Integer> enemyResolved;

    private int currentRoomIndex;

    public Dungeon(Random random, DungeonThemeFactory theme, game.dungeon.model.Dungeon model) {
        this.random = random;
        this.theme = theme;
        this.model = model;
        this.treasureResolved = new HashSet<>();
        this.enemyResolved = new HashSet<>();
        this.currentRoomIndex = 0;
    }

    public static Dungeon demo(Random random) {
        return fromTheme(random, new FireThemeFactory());
    }

    public static Dungeon fromTheme(Random random, DungeonThemeFactory theme) {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        game.dungeon.model.Dungeon generated = ProceduralDungeonGenerator.generar(builder, theme, random);
        return new Dungeon(random, theme, generated);
    }

    public game.dungeon.model.Dungeon model() {
        return model;
    }

    public DungeonThemeFactory theme() {
        return theme;
    }

    public String themeName() {
        return theme.getNombreTema();
    }

    public String themeKey() {
        return themeNameToKey(themeName());
    }

    public int currentRoomIndex() {
        return currentRoomIndex;
    }

    public Set<Integer> treasureResolvedRooms() {
        return Collections.unmodifiableSet(treasureResolved);
    }

    public Set<Integer> enemyResolvedRooms() {
        return Collections.unmodifiableSet(enemyResolved);
    }

    public int totalRooms() {
        return model.getSalas().size();
    }

    public Room currentRoom() {
        int idx = Math.max(0, Math.min(currentRoomIndex, totalRooms() - 1));
        return new Room(model.getSalas().get(idx));
    }

    public boolean canAdvanceRoom() {
        return currentRoomIndex < totalRooms() - 1;
    }

    public void advanceRoom() {
        if (!canAdvanceRoom()) {
            throw new IllegalStateException("Ya estas en la ultima sala de la mazmorra.");
        }
        currentRoomIndex++;
    }

    public void restoreProgress(int roomIndex, Set<Integer> restoredTreasureResolved, Set<Integer> restoredEnemyResolved) {
        int safeIndex = Math.max(0, Math.min(roomIndex, Math.max(0, totalRooms() - 1)));
        this.currentRoomIndex = safeIndex;

        treasureResolved.clear();
        if (restoredTreasureResolved != null) {
            for (Integer idx : restoredTreasureResolved) {
                if (idx != null && idx >= 0 && idx < totalRooms()) {
                    treasureResolved.add(idx);
                }
            }
        }

        enemyResolved.clear();
        if (restoredEnemyResolved != null) {
            for (Integer idx : restoredEnemyResolved) {
                if (idx != null && idx >= 0 && idx < totalRooms()) {
                    enemyResolved.add(idx);
                }
            }
        }
    }

    public boolean isCurrentRoomBoss() {
        return currentRoomIndex == totalRooms() - 1;
    }

    public boolean isEnemyPendingInCurrentRoom(boolean hasActiveEnemy) {
        if (hasActiveEnemy) {
            return true;
        }
        return currentRoom().hasEnemy() && !enemyResolved.contains(currentRoomIndex);
    }

    public boolean isTreasurePendingInCurrentRoom() {
        return currentRoom().hasTreasure() && !treasureResolved.contains(currentRoomIndex);
    }

    public void markCurrentRoomEnemyResolved() {
        enemyResolved.add(currentRoomIndex);
    }

    public void markCurrentRoomTreasureResolved() {
        treasureResolved.add(currentRoomIndex);
    }

    public boolean wasCurrentRoomTreasureResolved() {
        return treasureResolved.contains(currentRoomIndex);
    }

    public Optional<SimpleItem> searchTreasureInCurrentRoom() {
        if (wasCurrentRoomTreasureResolved()) {
            return Optional.empty();
        }

        markCurrentRoomTreasureResolved();

        Room room = currentRoom();
        boolean hasDrop = room.hasTreasure() || random.nextInt(100) < 45;
        if (!hasDrop) {
            return Optional.empty();
        }

        if (random.nextInt(100) < 30) {
            return Optional.of(theme.crearTesoroRaro());
        }
        return Optional.of(theme.crearTesoroComun());
    }

    public Optional<Enemy> spawnEnemyForCurrentRoom(boolean forced) {
        if (!forced && enemyResolved.contains(currentRoomIndex)) {
            return Optional.empty();
        }

        boolean boss = isCurrentRoomBoss();
        game.domain.personaje.Personaje character;

        if (boss) {
            character = theme.crearJefe();
        } else if (forced || currentRoom().hasEnemy() || random.nextInt(100) < 60) {
            character = random.nextInt(100) < 70
                ? theme.crearEnemigoBasico()
                : theme.crearEnemigoMedio();
        } else {
            return Optional.empty();
        }

        Enemy enemy = new Enemy(character);
        enemy.setExperienceReward(Math.max(20, enemy.hp() * 2));
        return Optional.of(enemy);
    }

    public SimpleItem randomCombatReward() {
        return random.nextBoolean() ? theme.crearTesoroRaro() : theme.crearTesoroComun();
    }

    public boolean shouldRollRandomEncounterOnAdvance() {
        return random.nextInt(100) < 25;
    }

    public List<String> minimapSymbols() {
        List<String> symbols = new ArrayList<>();
        int total = totalRooms();
        for (int i = 0; i < total; i++) {
            if (i == currentRoomIndex) {
                symbols.add("current");
            } else if (i < currentRoomIndex || enemyResolved.contains(i)) {
                symbols.add("cleared");
            } else if (i == total - 1) {
                symbols.add("boss");
            } else {
                symbols.add("unknown");
            }
        }
        return symbols;
    }

    private static String themeNameToKey(String themeName) {
        String normalized = normalize(themeName);
        if (normalized.contains("hielo")) {
            return "ice";
        }
        if (normalized.contains("veneno")) {
            return "poison";
        }
        if (normalized.contains("oscur")) {
            return "dark";
        }
        return "fire";
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }
}
