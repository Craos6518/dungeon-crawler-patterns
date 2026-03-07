package game.unit.creational;

import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.DungeonBuilder;
import game.dungeon.builder.DungeonDirector;
import game.dungeon.model.Dungeon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para el patrón Builder
 */
public class BuilderPatternTest {

    @Test
    public void testConstruirMazmorraManual() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        
        Dungeon dungeon = builder
            .setNombre("Torre de Prueba")
            .setTema("Magia")
            .setNivelDificultad(2)
            .agregarSala("Sala 1", "Primera sala", 1, false, true)
            .agregarSala("Sala 2", "Segunda sala", 2, true, true)
            .setSalaJefe("Sala Final", "Jefe final", 3)
            .build();
        
        assertNotNull(dungeon);
        assertEquals("Torre de Prueba", dungeon.getNombre());
        assertEquals("Magia", dungeon.getTema());
        assertEquals(2, dungeon.getNivelDificultad());
        assertEquals(2, dungeon.getCantidadSalas());
        assertNotNull(dungeon.getSalaJefe());
    }

    @Test
    public void testBuilderConDirector() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        DungeonDirector director = new DungeonDirector(builder);
        
        Dungeon mazmorraBasica = director.construirMazmorraBasica();
        
        assertNotNull(mazmorraBasica);
        assertEquals("Cueva del Inicio", mazmorraBasica.getNombre());
        assertEquals("Cueva", mazmorraBasica.getTema());
        assertTrue(mazmorraBasica.getCantidadSalas() > 0);
    }

    @Test
    public void testBuilderReset() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        
        Dungeon dungeon1 = builder
            .setNombre("Dungeon 1")
            .setTema("Fuego")
            .setNivelDificultad(1)
            .setSalaJefe("Jefe 1", "Boss", 5)
            .build();
        
        Dungeon dungeon2 = builder
            .setNombre("Dungeon 2")
            .setTema("Hielo")
            .setNivelDificultad(2)
            .setSalaJefe("Jefe 2", "Boss", 5)
            .build();
        
        assertNotEquals(dungeon1.getNombre(), dungeon2.getNombre());
        assertNotEquals(dungeon1.getTema(), dungeon2.getTema());
    }

    @Test
    public void testBuilderThrowsExceptionSinNombre() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        
        assertThrows(IllegalStateException.class, () -> {
            builder
                .setTema("Test")
                .setSalaJefe("Jefe", "Boss", 3)
                .build();
        });
    }

    @Test
    public void testDirectorConstruyeMazmorrasFuego() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        DungeonDirector director = new DungeonDirector(builder);
        
        Dungeon mazmorraFuego = director.construirMazmorraFuego();
        
        assertNotNull(mazmorraFuego);
        assertEquals("Volcán Ardiente", mazmorraFuego.getNombre());
        assertEquals("Fuego", mazmorraFuego.getTema());
        assertEquals(3, mazmorraFuego.getNivelDificultad());
    }
}
