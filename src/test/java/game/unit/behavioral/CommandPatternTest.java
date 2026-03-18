package game.unit.behavioral;

import game.command.actions.*;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para el patrón Command
 */
public class CommandPatternTest {
    
    private Personaje guerrero;
    private Personaje enemigo;
    private SimpleItem pocion;
    private CommandInvoker invoker;
    
    @BeforeEach
    public void setUp() {
        guerrero = new Guerrero("Arthas", 100, 20);
        enemigo = new EnemigoBasico("Orco", 50, 10);
        pocion = new SimpleItem("Poción", "Cura 50 HP", "Consumible", 50, 1);
        invoker = new CommandInvoker();
    }
    
    @Test
    public void testAttackCommandExecution() {
        int vidaInicialEnemigo = enemigo.getVida();
        
        Command ataque = new AttackCommand(guerrero, enemigo);
        assertTrue(ataque.canExecute(), "El comando debería poder ejecutarse");
        
        ataque.execute();
        
        assertTrue(enemigo.getVida() < vidaInicialEnemigo, 
            "El enemigo debería haber recibido daño");
    }
    
    @Test
    public void testDefendCommandExecution() {
        Command defensa = new DefendCommand(guerrero);
        
        assertTrue(defensa.canExecute(), "El comando debería poder ejecutarse");
        assertFalse(((DefendCommand) defensa).isEjecutado(), 
            "No debería estar ejecutado inicialmente");
        
        defensa.execute();
        
        assertTrue(((DefendCommand) defensa).isEjecutado(), 
            "Debería estar ejecutado después de execute()");
    }
    
    @Test
    public void testCannotExecuteCommandOnDeadCharacter() {
        // Matar al enemigo
        while (enemigo.estaVivo()) {
            enemigo.recibirDanio(100);
        }
        
        Command ataque = new AttackCommand(guerrero, enemigo);
        assertFalse(ataque.canExecute(), 
            "No debería poder ejecutarse un ataque contra un personaje muerto");
    }
    
    @Test
    public void testCommandInvokerHistory() {
        assertEquals(0, invoker.getCantidadComandos(), 
            "El historial debería estar vacío inicialmente");
        
        Command ataque1 = new AttackCommand(guerrero, enemigo);
        Command ataque2 = new AttackCommand(guerrero, enemigo);
        Command defensa = new DefendCommand(guerrero);
        
        invoker.ejecutarComando(ataque1);
        invoker.ejecutarComando(ataque2);
        invoker.ejecutarComando(defensa);
        
        assertEquals(3, invoker.getCantidadComandos(), 
            "El historial debería contener 3 comandos");
        
        var historial = invoker.getHistorial();
        assertEquals(ataque1, historial.get(0), "El primer comando debería ser ataque1");
        assertEquals(ataque2, historial.get(1), "El segundo comando debería ser ataque2");
        assertEquals(defensa, historial.get(2), "El tercer comando debería ser defensa");
    }
    
    @Test
    public void testCommandDescription() {
        Command ataque = new AttackCommand(guerrero, enemigo);
        String descripcion = ataque.getDescription();
        
        assertNotNull(descripcion, "La descripción no debería ser null");
        assertTrue(descripcion.contains("Arthas"), "La descripción debería contener el nombre del atacante");
        assertTrue(descripcion.contains("Orco"), "La descripción debería contener el nombre del defensor");
    }
    
    @Test
    public void testUseItemCommand() {
        Command usarItem = new UseItemCommand(guerrero, pocion, guerrero);
        
        assertTrue(usarItem.canExecute(), "El comando debería poder ejecutarse");
        assertEquals("Arthas usa Poción en Arthas", usarItem.getDescription());
        
        usarItem.execute();
        
        assertFalse(usarItem.canExecute(), 
            "No debería poder ejecutarse de nuevo (item ya usado)");
    }
    
    @Test
    public void testSkillCommand() {
        Command habilidad = new SkillCommand(guerrero, "Golpe Mortal", enemigo);
        
        assertTrue(habilidad.canExecute(), "El comando debería poder ejecutarse");
        
        String descripcion = habilidad.getDescription();
        assertTrue(descripcion.contains("Golpe Mortal"), 
            "La descripción debería contener el nombre de la habilidad");
        
        habilidad.execute();
    }
    
    @Test
    public void testInvokerThrowsExceptionWhenExecutingInvalidCommand() {
        // Matar al enemigo
        while (enemigo.estaVivo()) {
            enemigo.recibirDanio(100);
        }
        
        Command ataque = new AttackCommand(guerrero, enemigo);
        
        assertThrows(IllegalStateException.class, () -> {
            invoker.ejecutarComando(ataque);
        }, "Debería lanzar excepción al intentar ejecutar un comando inválido");
    }
    
    @Test
    public void testClearHistory() {
        Command ataque = new AttackCommand(guerrero, enemigo);
        invoker.ejecutarComando(ataque);
        
        assertEquals(1, invoker.getCantidadComandos());
        
        invoker.limpiarHistorial();
        
        assertEquals(0, invoker.getCantidadComandos(), 
            "El historial debería estar vacío después de limpiar");
    }
}
