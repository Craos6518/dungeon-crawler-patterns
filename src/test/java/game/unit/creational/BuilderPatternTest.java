package game.unit.creational;

import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.DungeonBuilder;
import game.dungeon.builder.DungeonDirector;
import game.dungeon.model.Dungeon;
import game.dungeon.theme.FireThemeFactory;
import java.util.Random;
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

    @Test
    public void testEquivalenciaDirectorYFactory() {
        long seed = 456L;
        FireThemeFactory theme = new FireThemeFactory();
        
        // Vía Director
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        DungeonDirector director = new DungeonDirector(builder);
        game.domain.exploration.Dungeon dungeon1 = director.buildForTheme(theme, seed);
        
        // Vía Aggregate (que ahora usa el Director internamente)
        game.domain.exploration.Dungeon dungeon2 = game.domain.exploration.Dungeon.fromTheme(new Random(seed), theme, seed);
        
        assertEquals(dungeon1.totalRooms(), dungeon2.totalRooms(), "Mismas salas");
        assertEquals(dungeon1.model().getNombre(), dungeon2.model().getNombre(), "Mismo nombre");
        assertEquals(dungeon1.model().getNivelDificultad(), dungeon2.model().getNivelDificultad(), "Misma dificultad");
    }

    @Test
    public void testDeterminismoSemilla() {
        long seed = 789L;
        FireThemeFactory theme = new FireThemeFactory();
        DungeonDirector director = new DungeonDirector(new ConcreteDungeonBuilder());
        
        game.domain.exploration.Dungeon d1 = director.buildForTheme(theme, seed);
        game.domain.exploration.Dungeon d2 = director.buildForTheme(theme, seed);
        
        assertEquals(d1.totalRooms(), d2.totalRooms());
        for (int i = 0; i < d1.totalRooms(); i++) {
            assertEquals(d1.model().getSalas().get(i).getNombre(), 
                         d2.model().getSalas().get(i).getNombre());
            assertEquals(d1.model().getSalas().get(i).getDificultad(), 
                         d2.model().getSalas().get(i).getDificultad());
        }
    }

    @Test
    public void testDificultadPerfiles() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        DungeonDirector director = new DungeonDirector(builder);
        
        Dungeon easy = director.construirMazmorraBasica();
        Dungeon hard = director.construirMazmorraOscura();
        
        assertTrue(hard.getNivelDificultad() > easy.getNivelDificultad(), 
            "La mazmorra oscura debe ser más difícil que la básica");
        
        assertTrue(hard.getSalaJefe().getDificultad() > easy.getSalaJefe().getDificultad(),
            "El jefe oscuro debe tener mayor dificultad que el básico");
    }
}
