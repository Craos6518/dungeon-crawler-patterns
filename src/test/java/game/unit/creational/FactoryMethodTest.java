package game.unit.creational;

import game.domain.personaje.Arquero;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Mago;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.ArqueroFactory;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.personaje.factory.MagoFactory;
import game.domain.personaje.factory.PersonajeFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para el patrón Factory Method
 */
public class FactoryMethodTest {

    @Test
    public void testGuerreroFactory() {
        PersonajeFactory factory = new GuerreroFactory(100, 15);
        Personaje heroe = factory.crearPersonaje("Arthas");
        
        assertNotNull(heroe);
        assertTrue(heroe instanceof Guerrero);
        assertEquals("Arthas", heroe.getNombre());
        assertEquals(100, heroe.getVida());
    }

    @Test
    public void testMagoFactory() {
        PersonajeFactory factory = new MagoFactory(80, 20);
        Personaje heroe = factory.crearPersonaje("Gandalf");
        
        assertNotNull(heroe);
        assertTrue(heroe instanceof Mago);
        assertEquals("Gandalf", heroe.getNombre());
        assertEquals(80, heroe.getVida());
    }

    @Test
    public void testArqueroFactory() {
        PersonajeFactory factory = new ArqueroFactory(90, 18);
        Personaje heroe = factory.crearPersonaje("Legolas");
        
        assertNotNull(heroe);
        assertTrue(heroe instanceof Arquero);
        assertEquals("Legolas", heroe.getNombre());
        assertEquals(90, heroe.getVida());
    }

    @Test
    public void testFactoriesCreanPersonajesDiferentes() {
        PersonajeFactory guerreroFactory = new GuerreroFactory(100, 15);
        PersonajeFactory magoFactory = new MagoFactory(80, 20);
        
        Personaje guerrero = guerreroFactory.crearPersonaje("Heroe1");
        Personaje mago = magoFactory.crearPersonaje("Heroe2");
        
        assertNotEquals(guerrero.getClass(), mago.getClass());
    }
}
