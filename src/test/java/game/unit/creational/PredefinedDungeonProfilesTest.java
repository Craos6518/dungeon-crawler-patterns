package game.unit.creational;

import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.ProceduralDungeonGenerator;
import game.dungeon.model.Dungeon;
import game.dungeon.theme.DarkThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.dungeon.theme.IceThemeFactory;
import game.dungeon.theme.PoisonThemeFactory;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifica que las mazmorras tematicas respetan los perfiles predefinidos.
 */
public class PredefinedDungeonProfilesTest {

    @Test
    public void fuegoDebeRespetarNombreYCantidadDeSalas() {
        Dungeon dungeon = ProceduralDungeonGenerator.generar(
            new ConcreteDungeonBuilder(),
            new FireThemeFactory(),
            new Random(123)
        );

        assertEquals("Volcan de Ignareth", dungeon.getNombre());
        assertEquals(7, dungeon.getCantidadSalas());
    }

    @Test
    public void hieloDebeRespetarNombreYCantidadDeSalas() {
        Dungeon dungeon = ProceduralDungeonGenerator.generar(
            new ConcreteDungeonBuilder(),
            new IceThemeFactory(),
            new Random(123)
        );

        assertEquals("Catacumbas de Glaciurvh", dungeon.getNombre());
        assertEquals(8, dungeon.getCantidadSalas());
    }

    @Test
    public void venenoDebeRespetarNombreYCantidadDeSalas() {
        Dungeon dungeon = ProceduralDungeonGenerator.generar(
            new ConcreteDungeonBuilder(),
            new PoisonThemeFactory(),
            new Random(123)
        );

        assertEquals("Pantanos de Viridax", dungeon.getNombre());
        assertEquals(8, dungeon.getCantidadSalas());
    }

    @Test
    public void oscuridadDebeRespetarNombreYCantidadDeSalas() {
        Dungeon dungeon = ProceduralDungeonGenerator.generar(
            new ConcreteDungeonBuilder(),
            new DarkThemeFactory(),
            new Random(123)
        );

        assertEquals("Ciudadela de Umbrakar", dungeon.getNombre());
        assertEquals(10, dungeon.getCantidadSalas());
        assertEquals("Portal de Umbrakar", dungeon.getSalas().get(0).getNombre());
        assertEquals("Trono de Malachar", dungeon.getSalas().get(9).getNombre());
    }
}
