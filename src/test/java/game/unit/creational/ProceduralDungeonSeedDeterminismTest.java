package game.unit.creational;

import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.ProceduralDungeonGenerator;
import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import game.dungeon.theme.DarkThemeFactory;
import game.dungeon.theme.DungeonThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.dungeon.theme.IceThemeFactory;
import game.dungeon.theme.PoisonThemeFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProceduralDungeonSeedDeterminismTest {

    @ParameterizedTest(name = "{0}: semilla reproducible y variacion estructural")
    @MethodSource("themeCases")
    void sameSeedReproducesAndDifferentSeedChangesStructure(
        String themeName,
        DungeonThemeFactory themeFactory,
        long stableSeed,
        long differentSeed
    ) {
        Dungeon first = generate(themeFactory, stableSeed);
        Dungeon second = generate(themeFactory, stableSeed);
        Dungeon other = generate(themeFactory, differentSeed);

        assertStructurallyEqual(first, second);

        boolean roomCountDiff = first.getCantidadSalas() != other.getCantidadSalas();
        boolean enemyDistributionDiff = countEnemyRooms(first) != countEnemyRooms(other);
        boolean optionalPresenceDiff = !optionalRoomNames(first).equals(optionalRoomNames(other));

        assertTrue(
            roomCountDiff || enemyDistributionDiff || optionalPresenceDiff,
            "Se esperaba variacion estructural para tema " + themeName
        );
    }

    private static Stream<Arguments> themeCases() {
        return Stream.of(
            Arguments.of("Fuego", new FireThemeFactory(), 123L, 125L),
            Arguments.of("Hielo", new IceThemeFactory(), 123L, 125L),
            Arguments.of("Veneno", new PoisonThemeFactory(), 123L, 125L),
            Arguments.of("Oscuridad", new DarkThemeFactory(), 123L, 125L)
        );
    }

    private static Dungeon generate(DungeonThemeFactory factory, long seed) {
        return ProceduralDungeonGenerator.generar(
            new ConcreteDungeonBuilder(),
            factory,
            new Random(seed)
        );
    }

    private static void assertStructurallyEqual(Dungeon expected, Dungeon actual) {
        assertEquals(expected.getNombre(), actual.getNombre());
        assertEquals(expected.getTema(), actual.getTema());
        assertEquals(expected.getNivelDificultad(), actual.getNivelDificultad());
        assertEquals(expected.getCantidadSalas(), actual.getCantidadSalas());

        for (int i = 0; i < expected.getSalas().size(); i++) {
            Room expectedRoom = expected.getSalas().get(i);
            Room actualRoom = actual.getSalas().get(i);

            assertEquals(expectedRoom.getNombre(), actualRoom.getNombre());
            assertEquals(expectedRoom.getDescripcion(), actualRoom.getDescripcion());
            assertEquals(expectedRoom.getDificultad(), actualRoom.getDificultad());
            assertEquals(expectedRoom.tieneEnemigo(), actualRoom.tieneEnemigo());
            assertEquals(expectedRoom.tieneTesoro(), actualRoom.tieneTesoro());
        }

        assertEquals(expected.getSalaJefe().getNombre(), actual.getSalaJefe().getNombre());
        assertEquals(expected.getSalaJefe().getDescripcion(), actual.getSalaJefe().getDescripcion());
        assertEquals(expected.getSalaJefe().getDificultad(), actual.getSalaJefe().getDificultad());
    }

    private static int countEnemyRooms(Dungeon dungeon) {
        int count = 0;
        for (Room room : dungeon.getSalas()) {
            if (room.tieneEnemigo()) {
                count++;
            }
        }
        return count;
    }

    private static List<String> optionalRoomNames(Dungeon dungeon) {
        List<Room> rooms = dungeon.getSalas();
        if (rooms.size() <= 2) {
            return List.of();
        }

        return rooms.subList(1, rooms.size() - 1).stream()
            .map(Room::getNombre)
            .toList();
    }
}
