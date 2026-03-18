package game.unit.creational;

import game.domain.personaje.Personaje;
import game.dungeon.theme.DarkThemeFactory;
import game.dungeon.theme.DungeonThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.dungeon.theme.IceThemeFactory;
import game.dungeon.theme.PoisonThemeFactory;
import game.items.model.SimpleItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para el patrón Abstract Factory
 */
public class AbstractFactoryTest {

    @Test
    public void testFireThemeFactory() {
        DungeonThemeFactory factory = new FireThemeFactory();
        
        assertEquals("Fuego", factory.getNombreTema());
        
        Personaje basico = factory.crearEnemigoBasico();
        assertNotNull(basico);
        assertTrue(basico.getNombre().contains("Fuego") || 
                   basico.getNombre().contains("Salamandra"));
        
        Personaje jefe = factory.crearJefe();
        assertNotNull(jefe);
        assertTrue(jefe.getVida() > basico.getVida());
        
        SimpleItem tesoro = factory.crearTesoroComun();
        assertNotNull(tesoro);
        
        SimpleItem tesoroRaro = factory.crearTesoroRaro();
        assertNotNull(tesoroRaro);
        assertTrue(tesoroRaro.getValorTotal() > tesoro.getValorTotal());
    }

    @Test
    public void testPoisonThemeFactory() {
        DungeonThemeFactory factory = new PoisonThemeFactory();
        
        assertEquals("Veneno", factory.getNombreTema());
        
        Personaje basico = factory.crearEnemigoBasico();
        assertNotNull(basico);
        
        SimpleItem tesoro = factory.crearTesoroComun();
        assertNotNull(tesoro);
    }

    @Test
    public void testIceThemeFactory() {
        DungeonThemeFactory factory = new IceThemeFactory();
        
        assertEquals("Hielo", factory.getNombreTema());
        
        Personaje medio = factory.crearEnemigoMedio();
        assertNotNull(medio);
        
        Personaje jefe = factory.crearJefe();
        assertNotNull(jefe);
        assertTrue(jefe.getVida() > medio.getVida());
    }

    @Test
    public void testDarkThemeFactory() {
        DungeonThemeFactory factory = new DarkThemeFactory();
        
        assertEquals("Oscuridad", factory.getNombreTema());
        
        Personaje jefe = factory.crearJefe();
        assertNotNull(jefe);
        assertEquals("Señor de las Sombras", jefe.getNombre());
    }

    @Test
    public void testFactoriesCreanFamiliasCoherentes() {
        DungeonThemeFactory fireFactory = new FireThemeFactory();
        DungeonThemeFactory iceFactory = new IceThemeFactory();
        
        Personaje enemigoFuego = fireFactory.crearEnemigoBasico();
        Personaje enemigoHielo = iceFactory.crearEnemigoBasico();
        
        SimpleItem tesoroFuego = fireFactory.crearTesoroComun();
        SimpleItem tesoroHielo = iceFactory.crearTesoroComun();
        
        assertNotEquals(enemigoFuego.getNombre(), enemigoHielo.getNombre());
        assertNotEquals(tesoroFuego.getNombre(), tesoroHielo.getNombre());
    }

    @Test
    public void testTodosLosTemasCreanJefes() {
        DungeonThemeFactory[] factories = {
            new FireThemeFactory(),
            new PoisonThemeFactory(),
            new IceThemeFactory(),
            new DarkThemeFactory()
        };
        
        for (DungeonThemeFactory factory : factories) {
            Personaje jefe = factory.crearJefe();
            assertNotNull(jefe);
            assertTrue(jefe.getVida() > 100, 
                "El jefe de " + factory.getNombreTema() + " debe tener más de 100 HP");
        }
    }
}
