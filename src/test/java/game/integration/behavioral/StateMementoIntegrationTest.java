package game.integration.behavioral;

import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;
import game.state.game.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración que demuestra la colaboración entre los patrones
 * State y Memento para guardar y restaurar estados completos del juego.
 * 
 * Escenario: Un juego que transita por diferentes estados (Menu, Exploration,
 * Combat, Inventory, GameOver) y permite guardar/cargar el progreso en
 * cualquier momento.
 */
public class StateMementoIntegrationTest {
    
    private GameStateContext contextoJuego;
    private GameOriginator originator;
    private GameCaretaker caretaker;
    private String testSavePath;
    
    @BeforeEach
    public void setUp() {
        // Configurar ruta de guardado para tests
        testSavePath = "./test-saves/integration/";
        new File(testSavePath).mkdirs();
        
        // Inicializar componentes
        contextoJuego = new GameStateContext(new MenuState(null));
        originator = new GameOriginator("Aventurero");
        caretaker = new GameCaretaker(testSavePath);
    }
    
    @AfterEach
    public void tearDown() {
        // Limpiar archivos de test
        File dir = new File(testSavePath);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            dir.delete();
        }
    }
    
    @Test
    public void testGuardarYRestaurarEstadoMenu() {
        // Estado inicial: Menu
        contextoJuego = new GameStateContext(new MenuState(contextoJuego));
        
        assertEquals("Menu", contextoJuego.getEstadoActual().getNombre(),
            "Debe iniciar en estado Menu");
        
        // Guardar estado del juego en Menu
        originator.setEstadoActual("Menu");
        originator.setVidaHeroeActual(100);
        originator.setProgreso(0);
        
        GameMemento memento = originator.guardar();
        caretaker.guardarMemento(memento);
        
        // Cambiar estado
        contextoJuego.cambiarEstado(new ExplorationState(contextoJuego));
        assertEquals("Exploration", contextoJuego.getEstadoActual().getNombre());
        
        // Restaurar desde memento
        GameMemento mementoRecuperado = caretaker.obtenerUltimoMemento();
        assertNotNull(mementoRecuperado, "Debe poder recuperar el memento");
        
        originator.restaurar(mementoRecuperado);
        assertEquals("Menu", originator.getEstadoActual(),
            "El estado restaurado debe ser Menu");
        assertEquals(100, originator.getVidaHeroeActual());
    }
    
    @Test
    public void testGuardarEstadoDuranteCombate() {
        // Transitar a estado de combate
        contextoJuego = new GameStateContext(new MenuState(contextoJuego));
        contextoJuego.cambiarEstado(new ExplorationState(contextoJuego));
        
        GameState estadoExploración = contextoJuego.getEstadoActual();
        contextoJuego.cambiarEstado(new CombatState(contextoJuego, estadoExploración));
        
        assertEquals("Combat", contextoJuego.getEstadoActual().getNombre());
        
        // Guardar estado durante combate
        originator.setEstadoActual("Combat");
        originator.setVidaHeroeActual(75);  // Héroe recibió daño
        originator.setProgreso(50);
        
        Map<String, Object> datosExtra = new HashMap<>();
        datosExtra.put("enemigoDerrota", false);
        datosExtra.put("turnosTranscurridos", 3);
        originator.setDatosExtra(datosExtra);
        
        GameMemento mementoEnCombate = originator.guardar();
        caretaker.guardarMemento(mementoEnCombate);
        
        // Simular progreso del combate
        originator.setVidaHeroeActual(50);
        originator.setProgreso(75);
        
        // Restaurar checkpoint previo
        GameMemento checkpoint = caretaker.obtenerUltimoMemento();
        originator.restaurar(checkpoint);
        
        assertEquals("Combat", originator.getEstadoActual(),
            "Debe volver al estado Combat");
        assertEquals(75, originator.getVidaHeroeActual(),
            "Debe restaurar vida del héroe");
        assertEquals(50, originator.getProgreso(),
            "Debe restaurar progreso");
    }
    
    @Test
    public void testTransicionesDeEstadoConGuardadoAutomatico() {
        // Escenario: Guardar automáticamente en cada transición clave
        
        // 1. Estado Menu → Exploration
        contextoJuego = new GameStateContext(new MenuState(contextoJuego));
        originator.setEstadoActual("Menu");
        originator.setVidaHeroeActual(100);
        caretaker.guardarMemento(originator.guardar());
        
        contextoJuego.cambiarEstado(new ExplorationState(contextoJuego));
        assertEquals("Exploration", contextoJuego.getEstadoActual().getNombre());
        
        // 2. Checkpoint en Exploration
        originator.setEstadoActual("Exploration");
        originator.setProgreso(25);
        caretaker.guardarMemento(originator.guardar());
        
        // 3. Transition a Inventory
        GameState estadoExploración = contextoJuego.getEstadoActual();
        contextoJuego.cambiarEstado(new InventoryState(contextoJuego, estadoExploración));
        assertEquals("Inventory", contextoJuego.getEstadoActual().getNombre());
        
        originator.setEstadoActual("Inventory");
        caretaker.guardarMemento(originator.guardar());
        
        // 4. Volver a Exploration desde Inventory
        contextoJuego.procesarEntrada("e");  // Salir del inventario
        assertEquals("Exploration", contextoJuego.getEstadoActual().getNombre());
        
        // Verificar historial de mementos
        assertEquals(3, caretaker.getCantidadMementos(),
            "Debe tener 3 checkpoints guardados");
        
        // Restaurar a checkpoint anterior (antes de abrir inventario)
        GameMemento checkpointAnterior = caretaker.obtenerMemento(1);  // Exploration
        assertNotNull(checkpointAnterior);
        originator.restaurar(checkpointAnterior);
        assertEquals("Exploration", originator.getEstadoActual());
        assertEquals(25, originator.getProgreso());
    }
    
    @Test
    public void testGuardarEstadoGameOverYReiniciar() {
        // Simular llegada a Game Over
        contextoJuego = new GameStateContext(new GameOverState(contextoJuego, false));
        
        GameOverState estadoGameOver = (GameOverState) contextoJuego.getEstadoActual();
        assertFalse(estadoGameOver.isVictoria(), "Debe ser derrota");
        
        // Guardar estado de Game Over (para estadísticas)
        originator.setEstadoActual("GameOver");
        originator.setVidaHeroeActual(0);
        originator.setProgreso(80);  // Llegó al 80% antes de morir
        
        GameMemento mementoGameOver = originator.guardar();
        caretaker.guardarMemento(mementoGameOver);
        
        // Simular "reintentar" - volver a Menu
        contextoJuego.cambiarEstado(new MenuState(contextoJuego));
        assertEquals("Menu", contextoJuego.getEstadoActual().getNombre());
        
        // Cargar checkpoint anterior (antes del Game Over)
        // En un juego real, esto sería el último save antes de morir
        originator.setEstadoActual("Menu");
        originator.setVidaHeroeActual(100);
        originator.setProgreso(0);
        
        assertTrue(contextoJuego.isEjecutando(),
            "El juego debe seguir ejecutándose después de reiniciar");
    }
    
    @Test
    public void testGuardarEstadoVictoriaConProgreso() {
        // Simular victoria
        contextoJuego = new GameStateContext(new GameOverState(contextoJuego, true));
        
        GameOverState estadoVictoria = (GameOverState) contextoJuego.getEstadoActual();
        assertTrue(estadoVictoria.isVictoria(), "Debe ser victoria");
        assertEquals("Victory", estadoVictoria.getNombre());
        
        // Guardar estado final victorioso
        originator.setEstadoActual("Victory");
        originator.setVidaHeroeActual(45);  // Ganó con poca vida
        originator.setProgreso(100);
        
        Map<String, Object> estadisticasFinales = new HashMap<>();
        estadisticasFinales.put("enemigosDerrota", 15);
        estadisticasFinales.put("tiempoJuego", 1200);  // segundos
        estadisticasFinales.put("itemsRecolectados", 8);
        originator.setDatosExtra(estadisticasFinales);
        
        GameMemento mementoVictoria = originator.guardar();
        caretaker.guardarMemento(mementoVictoria);
        
        // Verificar que el memento contiene toda la información
        GameMemento recuperado = caretaker.obtenerUltimoMemento();
        assertNotNull(recuperado);
        
        originator.restaurar(recuperado);
        assertEquals("Victory", originator.getEstadoActual());
        assertEquals(100, originator.getProgreso());
        assertEquals(45, originator.getVidaHeroeActual());
    }
    
    @Test
    public void testPersistenciaEnDisco() {
        // Este test valida que State + Memento permiten save/load en disco
        
        contextoJuego = new GameStateContext(new ExplorationState(contextoJuego));
        
        originator.setEstadoActual("Exploration");
        originator.setVidaHeroeActual(80);
        originator.setProgreso(60);
        
        GameMemento memento = originator.guardar();
        
        // Guardar en disco
        String nombreArchivo = "checkpoint_test.save";
        assertTrue(caretaker.guardarEnDisco(memento, nombreArchivo),
            "Debe poder guardar en disco");
        
        // Simular cierre y apertura del juego
        caretaker = null;
        originator = null;
        
        // Nuevo caretaker (nuevo juego cargado)
        caretaker = new GameCaretaker(testSavePath);
        originator = new GameOriginator("Nuevo Jugador");
        
        // Cargar desde disco
        GameMemento mementoCargado = caretaker.cargarDesdeDisco(nombreArchivo);
        assertNotNull(mementoCargado, "Debe poder cargar desde disco");
        
        originator.restaurar(mementoCargado);
        assertEquals("Exploration", originator.getEstadoActual(),
            "Estado debe persistir entre sesiones");
        assertEquals(80, originator.getVidaHeroeActual());
        assertEquals(60, originator.getProgreso());
    }
    
    @Test
    public void testIntegracionCompletaStateMementoEnFlujoNormal() {
        // Flujo completo: Menu → Exploration → Combat → Victory con saves
        
        // 1. Inicio en Menu
        contextoJuego = new GameStateContext(new MenuState(contextoJuego));
        originator.setEstadoActual("Menu");
        caretaker.guardarMemento(originator.guardar());
        
        // 2. Iniciar partida (Menu → Exploration)
        contextoJuego.procesarEntrada("1");  // "jugar"
        assertEquals("Exploration", contextoJuego.getEstadoActual().getNombre());
        originator.setEstadoActual("Exploration");
        originator.setVidaHeroeActual(100);
        caretaker.guardarMemento(originator.guardar());
        
        // 3. Encontrar enemigo (Exploration → Combat)
        GameState exploración = contextoJuego.getEstadoActual();
        contextoJuego.cambiarEstado(new CombatState(contextoJuego, exploración));
        assertEquals("Combat", contextoJuego.getEstadoActual().getNombre());
        originator.setEstadoActual("Combat");
        originator.setVidaHeroeActual(85);
        caretaker.guardarMemento(originator.guardar());
        
        // 4. Ganar combate (Combat → Victory)
        contextoJuego.cambiarEstado(new GameOverState(contextoJuego, true));
        assertEquals("Victory", contextoJuego.getEstadoActual().getNombre());
        originator.setEstadoActual("Victory");
        originator.setProgreso(100);
        caretaker.guardarMemento(originator.guardar());
        
        // Validaciones finales
        assertEquals(4, caretaker.getCantidadMementos(),
            "Debe tener 4 checkpoints del flujo completo");
        
        // Poder volver a cualquier punto
        GameMemento combat = caretaker.obtenerMemento(2);  // Checkpoint en Combat
        originator.restaurar(combat);
        assertEquals("Combat", originator.getEstadoActual());
        assertEquals(85, originator.getVidaHeroeActual());
        
        // Este test demuestra cómo State y Memento colaboran:
        // - State: Gestiona las transiciones entre estados del juego
        // - Memento: Captura y restaura el estado completo en cualquier momento
        // - Juntos: Permiten un sistema de save/load robusto
    }
}
