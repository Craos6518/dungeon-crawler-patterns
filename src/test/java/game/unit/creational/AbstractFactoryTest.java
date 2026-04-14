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
        assertEquals("Malachar", jefe.getNombre());
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
    public void testContratoResistenciasPorTema() {
        // Fire
        Personaje f = new FireThemeFactory().crearEnemigoBasico();
        assertTrue(f.getResistenciaFuego() > 0);
        
        // Ice
        Personaje i = new IceThemeFactory().crearEnemigoBasico();
        assertTrue(i.getResistenciaHielo() > 0);
        
        // Poison
        Personaje p = new PoisonThemeFactory().crearEnemigoBasico();
        assertTrue(p.getResistenciaVeneno() > 0);
        
        // Dark
        Personaje d = new DarkThemeFactory().crearEnemigoBasico();
        assertTrue(d.getResistenciaOscuridad() > 0);
    }

    @Test
    public void testContratoLootTematico() {
        DungeonThemeFactory iceFactory = new IceThemeFactory();
        SimpleItem loot = iceFactory.crearTesoroRaro();
        assertTrue(loot.getNombre().toLowerCase().contains("invierno") || 
                   loot.getNombre().toLowerCase().contains("hielo"),
                   "El loot de hielo debe tener nombre temático");
        
        DungeonThemeFactory fireFactory = new FireThemeFactory();
        SimpleItem lootF = fireFactory.crearTesoroRaro();
        assertTrue(lootF.getNombre().toLowerCase().contains("flam") || 
                   lootF.getNombre().toLowerCase().contains("fuego"),
                   "El loot de fuego debe tener nombre temático");
    }

    @Test
    public void testMapeoTemaRuntimeEnSessionFactory() {
        // Probamos que GameSessionFactory resuelve correctamente las fábricas
        // Nota: GameSessionFactory no expone la factoría directamente,
        // pero podemos verificarlo por el nombre de la mazmorra o enemigos creados.
        
        game.application.state.GameSession sessionFire = game.application.state.GameSessionFactory.createSessionForTheme("fire");
        assertTrue(sessionFire.dungeon().themeName().equalsIgnoreCase("fuego"));
        
        game.application.state.GameSession sessionIce = game.application.state.GameSessionFactory.createSessionForTheme("ice");
        assertTrue(sessionIce.dungeon().themeName().equalsIgnoreCase("hielo"));
    }
}
