package game.unit.creational;

import game.balance.GameBalance;
import game.dungeon.theme.FireThemeFactory;
import game.domain.personaje.Dragon;
import game.domain.personaje.Arquero;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Mago;
import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.ArqueroFactory;
import game.domain.personaje.factory.DragonFactory;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.personaje.factory.MagoFactory;
import game.domain.personaje.factory.OrcoFactory;
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

    @Test
    public void testDragonFactory() {
        PersonajeFactory factory = new DragonFactory(290, 33);
        Personaje jefe = factory.crearPersonaje("Malachar");

        assertNotNull(jefe);
        assertTrue(jefe instanceof Dragon);
        assertEquals("Malachar", jefe.getNombre());
        assertEquals(290, jefe.getVida());
        assertEquals(33, ((Dragon) jefe).getFuegoDragon());
    }

    @Test
    public void testOrcoFactory() {
        PersonajeFactory factory = new OrcoFactory(70, 15);
        Personaje enemigo = factory.crearPersonaje("Caballero Oscuro");

        assertNotNull(enemigo);
        assertTrue(enemigo instanceof Orco);
        assertEquals("Caballero Oscuro", enemigo.getNombre());
        assertEquals(70, enemigo.getVida());
        assertEquals(15, ((Orco) enemigo).getFuerza());
    }

    @Test
    public void testFireThemeFactoryBossContractUsesDragonFactoryOutputType() {
        FireThemeFactory factory = new FireThemeFactory();
        Personaje boss = factory.crearJefe();
        GameBalance.BossProfile profile = GameBalance.boss("fire");

        assertNotNull(boss);
        assertEquals(Dragon.class, boss.getClass(),
            "El jefe del tema fire debe ser compatible con el producto de DragonFactory");
        assertEquals(profile.name(), boss.getNombre());
        assertEquals(profile.hp(), boss.getVida());
        assertEquals(profile.attack(), ((Dragon) boss).getFuegoDragon());
    }
}
