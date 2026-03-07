package game.unit.behavioral;

import game.state.game.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para el patrón State
 * 
 * Valida que el patrón State permite modelar estados del juego
 * sin usar condicionales complejos, facilitando las transiciones
 * y el comportamiento específico de cada estado.
 */
public class StatePatternTest {
    
    private GameStateContext contexto;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    public void setUp() {
        // Capturar salida de consola para validar comportamientos
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    public void testContextInicializaConEstadoInicial() {
        contexto = new GameStateContext(new MenuState(null));
        
        assertNotNull(contexto.getEstadoActual(), 
            "El contexto debe tener un estado inicial");
        assertTrue(contexto.isEjecutando(), 
            "El contexto debe estar ejecutándose inicialmente");
    }
    
    @Test
    public void testContextRechazaEstadoNull() {
        contexto = new GameStateContext(new MenuState(null));
        
        assertThrows(IllegalArgumentException.class, 
            () -> contexto.cambiarEstado(null),
            "No debe aceptar estado null");
    }
    
    @Test
    public void testTransicionEntreEstados() {
        // Crear contexto con MenuState
        contexto = new GameStateContext(new MenuState(contexto));
        
        assertEquals("Menu", contexto.getEstadoActual().getNombre());
        
        // Cambiar a ExplorationState
        contexto.cambiarEstado(new ExplorationState(contexto));
        
        assertEquals("Exploration", contexto.getEstadoActual().getNombre());
        
        // Cambiar a CombatState
        contexto.cambiarEstado(new CombatState(contexto, 
            contexto.getEstadoActual()));
        
        assertEquals("Combat", contexto.getEstadoActual().getNombre());
    }
    
    @Test
    public void testCallbacksOnEnterYOnExitSeEjecutan() {
        contexto = new GameStateContext(new MenuState(contexto));
        String output1 = outputStream.toString();
        
        assertTrue(output1.contains("Entrando al menú principal"), 
            "onEnter del estado inicial debe ejecutarse");
        
        // Cambiar a otro estado
        outputStream.reset();
        contexto.cambiarEstado(new ExplorationState(contexto));
        String output2 = outputStream.toString();
        
        assertTrue(output2.contains("Saliendo del menú principal"), 
            "onExit del estado anterior debe ejecutarse");
        assertTrue(output2.contains("Entrando a exploración"), 
            "onEnter del nuevo estado debe ejecutarse");
    }
    
    @Test
    public void testMenuStateCambiaPorEntrada() {
        contexto = new GameStateContext(new MenuState(contexto));
        
        assertEquals("Menu", contexto.getEstadoActual().getNombre());
        
        // Simular entrada "jugar"
        contexto.procesarEntrada("1");
        
        // Debe haber cambiado a ExplorationState
        assertEquals("Exploration", contexto.getEstadoActual().getNombre());
    }
    
    @Test
    public void testGameOverStateConVictoria() {
        contexto = new GameStateContext(new GameOverState(contexto, true));
        
        GameOverState estado = (GameOverState) contexto.getEstadoActual();
        
        assertTrue(estado.isVictoria(), 
            "El estado debe reflejar victoria");
        assertEquals("Victory", estado.getNombre());
    }
    
    @Test
    public void testGameOverStateConDerrota() {
        contexto = new GameStateContext(new GameOverState(contexto, false));
        
        GameOverState estado = (GameOverState) contexto.getEstadoActual();
        
        assertFalse(estado.isVictoria(), 
            "El estado debe reflejar derrota");
        assertEquals("GameOver", estado.getNombre());
    }
    
    @Test
    public void testGameOverStateVolverAlMenu() {
        contexto = new GameStateContext(new GameOverState(contexto, false));
        
        assertEquals("GameOver", contexto.getEstadoActual().getNombre());
        
        // Simular entrada "menu"
        contexto.procesarEntrada("2");
        
        assertEquals("Menu", contexto.getEstadoActual().getNombre());
    }
    
    @Test
    public void testMenuStatePuedeDetenerContexto() {
        contexto = new GameStateContext(new MenuState(contexto));
        
        assertTrue(contexto.isEjecutando());
        
        // Simular entrada "salir"
        contexto.procesarEntrada("3");
        
        assertFalse(contexto.isEjecutando(), 
            "El contexto debe detenerse al elegir 'salir'");
    }
    
    @Test
    public void testInventoryStateVuelveAEstadoAnterior() {
        contexto = new GameStateContext(new ExplorationState(contexto));
        GameState estadoExploración = contexto.getEstadoActual();
        
        // Cambiar a InventoryState pasando el estado anterior
        contexto.cambiarEstado(new InventoryState(contexto, estadoExploración));
        
        assertEquals("Inventory", contexto.getEstadoActual().getNombre());
        
        // Simular salir del inventario
        contexto.procesarEntrada("e");
        
        assertEquals("Exploration", contexto.getEstadoActual().getNombre(), 
            "Debe volver al estado anterior");
    }
    
    @Test
    public void testEstadoDelegaActualizacion() {
        contexto = new GameStateContext(new MenuState(contexto));
        
        // No debe lanzar excepciones
        assertDoesNotThrow(() -> contexto.actualizar());
    }
    
    @Test
    public void testEstadoDelegaRender() {
        contexto = new GameStateContext(new MenuState(contexto));
        
        outputStream.reset();
        contexto.render();
        String output = outputStream.toString();
        
        assertTrue(output.contains("DUNGEON CRAWLER"), 
            "Render debe mostrar contenido del estado actual");
    }
    
    @Test
    public void testCambiosDeEstadoMultiples() {
        contexto = new GameStateContext(new MenuState(contexto));
        
        // Menu -> Exploration -> Combat -> GameOver -> Menu
        assertEquals("Menu", contexto.getEstadoActual().getNombre());
        
        contexto.cambiarEstado(new ExplorationState(contexto));
        assertEquals("Exploration", contexto.getEstadoActual().getNombre());
        
        contexto.cambiarEstado(new CombatState(contexto, 
            contexto.getEstadoActual()));
        assertEquals("Combat", contexto.getEstadoActual().getNombre());
        
        contexto.cambiarEstado(new GameOverState(contexto, true));
        assertEquals("Victory", contexto.getEstadoActual().getNombre());
        
        contexto.cambiarEstado(new MenuState(contexto));
        assertEquals("Menu", contexto.getEstadoActual().getNombre());
    }
    
    @Test
    public void testCadaEstadoTieneNombreUnico() {
        assertEquals("Menu", new MenuState(contexto).getNombre());
        assertEquals("Exploration", new ExplorationState(contexto).getNombre());
        assertEquals("Combat", new CombatState(contexto, null).getNombre());
        assertEquals("Inventory", new InventoryState(contexto, null).getNombre());
        assertEquals("Victory", new GameOverState(contexto, true).getNombre());
        assertEquals("GameOver", new GameOverState(contexto, false).getNombre());
    }
    
    @Test
    public void testPatronStateEliminaCondicionalesComplejos() {
        // Este test valida el beneficio del patrón:
        // En lugar de tener un gran switch/if basado en un enum de estado,
        // cada estado encapsula su propio comportamiento
        
        contexto = new GameStateContext(new MenuState(contexto));
        
        // El comportamiento cambia según el estado sin condicionales en el cliente
        String estado1 = contexto.getEstadoActual().getNombre();
        
        contexto.cambiarEstado(new ExplorationState(contexto));
        String estado2 = contexto.getEstadoActual().getNombre();
        
        assertNotEquals(estado1, estado2, 
            "Los estados deben tener comportamiento diferenciado");
        
        // El contexto no necesita saber qué estado concreto es
        assertNotNull(contexto.getEstadoActual());
    }
    
    @Test
    public void testIntegracionContratoGameState() {
        // Validar que todos los métodos del contrato State funcionan
        contexto = new GameStateContext(new MenuState(contexto));
        GameState estado = contexto.getEstadoActual();
        
        assertDoesNotThrow(() -> {
            estado.manejarEntrada("test");
            estado.actualizar();
            estado.render();
            estado.onEnter();
            estado.onExit();
            estado.getNombre();
        }, "Todos los métodos del contrato State deben funcionar");
    }
    
    @org.junit.jupiter.api.AfterEach
    public void tearDown() {
        // Restaurar salida estándar
        System.setOut(originalOut);
    }
}
