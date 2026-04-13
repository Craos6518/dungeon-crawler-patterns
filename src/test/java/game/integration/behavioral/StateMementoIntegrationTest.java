package game.integration.behavioral;

import game.application.state.GameFlowState;
import game.application.state.GameSessionFactory;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.SaveGameUseCase;
import game.infrastructure.persistence.memento.GameCaretaker;
import game.application.state.GameMemento;
import game.infrastructure.persistence.memento.GameOriginator;
import game.state.game.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

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
        // El originator ya tiene vida=100 por defecto desde el constructor
        
        GameMemento memento = originator.guardar();
        caretaker.guardarEnMemoria(memento);
        
        // Cambiar estado
        contextoJuego.cambiarEstado(new ExplorationState(contextoJuego));
        assertEquals("Exploration", contextoJuego.getEstadoActual().getNombre());
        
        // Restaurar desde memento
        GameMemento mementoRecuperado = caretaker.obtenerUltimoMemento();
        assertNotNull(mementoRecuperado, "Debe poder recuperar el memento");
        
        originator.restaurar(mementoRecuperado);
        assertEquals("Menu", originator.getEstadoActual(),
            "El estado restaurado debe ser Menu");
        assertEquals(100, originator.getVidaJugador());
    }
    
    @Test
    public void testGuardarEstadoDuranteCombate() {
        // Transitar a estado de combate
        contextoJuego = new GameStateContext(new MenuState(contextoJuego));
        contextoJuego.cambiarEstado(new ExplorationState(contextoJuego));
        
        GameState estadoExploración = contextoJuego.getEstadoActual();
        contextoJuego.cambiarEstado(new CombatState(contextoJuego, estadoExploración));
        
        assertEquals("Combat", contextoJuego.getEstadoActual().getNombre());
        
        // Guardar estado durante combate - héroe recibió daño
        originator.setEstadoActual("Combat");
        originator.recibirDanio(25);  // Vida de 100 a 75
        originator.progresar(); // Avanzar progreso
        
        GameMemento mementoEnCombate = originator.guardar();
        caretaker.guardarEnMemoria(mementoEnCombate);
        
        // Simular progreso del combate
        originator.recibirDanio(25); // Vida de 75 a 50
        originator.progresar();
        
        // Restaurar checkpoint previo
        GameMemento checkpoint = caretaker.obtenerUltimoMemento();
        originator.restaurar(checkpoint);
        
        assertEquals("Combat", originator.getEstadoActual(),
            "Debe volver al estado Combat");
        assertEquals(75, originator.getVidaJugador(),
            "Debe restaurar vida del héroe");
        assertTrue(originator.getSalaActual() >= 2, 
            "Debe tener progreso guardado");
    }
    
    @Test
    public void testTransicionesDeEstadoConGuardadoAutomatico() {
        // Escenario: Guardar automáticamente en cada transición clave
        
        // 1. Estado Menu → Exploration
        contextoJuego = new GameStateContext(new MenuState(contextoJuego));
        originator.setEstadoActual("Menu");
        caretaker.guardarEnMemoria(originator.guardar());
        
        contextoJuego.cambiarEstado(new ExplorationState(contextoJuego));
        assertEquals("Exploration", contextoJuego.getEstadoActual().getNombre());
        
        // 2. Checkpoint en Exploration
        originator.setEstadoActual("Exploration");
        originator.progresar(); // Avanzar sala
        caretaker.guardarEnMemoria(originator.guardar());
        
        // 3. Transición a Inventory
        GameState estadoExploración = contextoJuego.getEstadoActual();
        contextoJuego.cambiarEstado(new InventoryState(contextoJuego, estadoExploración));
        assertEquals("Inventory", contextoJuego.getEstadoActual().getNombre());
        
        originator.setEstadoActual("Inventory");
        caretaker.guardarEnMemoria(originator.guardar());
        
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
        assertTrue(originator.getSalaActual() >= 2, "Debe tener progreso");
    }
    
    @Test
    public void testGuardarEstadoGameOverYReiniciar() {
        // Simular llegada a Game Over
        contextoJuego = new GameStateContext(new GameOverState(contextoJuego, false));
        
        GameOverState estadoGameOver = (GameOverState) contextoJuego.getEstadoActual();
        assertFalse(estadoGameOver.isVictoria(), "Debe ser derrota");
        
        // Guardar estado de Game Over (para estadísticas)
        originator.setEstadoActual("GameOver");
        originator.recibirDanio(100); // Reducir vida a 0
        // En un juego real, habría llegado al 80% antes de morir
        // (progreso se basa en sala actual)
        
        GameMemento mementoGameOver = originator.guardar();
        caretaker.guardarEnMemoria(mementoGameOver);
        
        // Simular "reintentar" - volver a Menu
        contextoJuego.cambiarEstado(new MenuState(contextoJuego));
        assertEquals("Menu", contextoJuego.getEstadoActual().getNombre());
        
        // Crear nuevo originator para reintento (nueva partida)
        originator = new GameOriginator("Aventurero");
        originator.setEstadoActual("Menu");
        
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
        
        // Simular que el jugador ganó con poca vida
        originator.setEstadoActual("Victory");
        originator.recibirDanio(55); // Reducir vida de 100 a 45
        
        // Avanzar una sala sin subir de nivel (subir nivel restaura vida a 100)
        originator.progresar();
        
        // Guardar el estado DESPUÉS de modificar vida y progreso
        GameMemento mementoVictoria = originator.guardar();
        caretaker.guardarEnMemoria(mementoVictoria);
        
        // Verificar que el memento contiene toda la información
        GameMemento recuperado = caretaker.obtenerUltimoMemento();
        assertNotNull(recuperado);
        
        // Crear nuevo originator y restaurar
        GameOriginator nuevoOriginator = new GameOriginator("Test");
        nuevoOriginator.restaurar(recuperado);
        assertEquals("Victory", nuevoOriginator.getEstadoActual());
        assertTrue(nuevoOriginator.getSalaActual() > 1, "Debe tener progreso");
        assertEquals(45, nuevoOriginator.getVidaJugador(), "Vida debe reflejar el daño recibido");
    }
    
    @Test
    public void testPersistenciaEnDisco() {
        // Este test valida que State + Memento permiten save/load en disco
        
        contextoJuego = new GameStateContext(new ExplorationState(contextoJuego));
        
        originator.setEstadoActual("Exploration");
        originator.recibirDanio(20); // Vida a 80
        originator.progresar(); // Avanzar progreso
        
        GameMemento memento = originator.guardar();
        
        // Guardar en disco
        String nombreArchivo = "checkpoint_test";
        assertDoesNotThrow(() -> caretaker.guardarEnDisco(memento, nombreArchivo),
            "Debe poder guardar en disco sin excepciones");
        
        // Verificar que el archivo existe
        File saveFile = new File(testSavePath + nombreArchivo + ".save");
        assertTrue(saveFile.exists(), "El archivo debe existir en disco");
        
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
        assertEquals(80, originator.getVidaJugador());
    }

    @Test
    public void testLoadRestauraExplorationYNoMenuEnFlujoProductivo() {
        var source = GameSessionFactory.createSessionForTheme("poison", "guerrero");
        source.transitionTo(GameFlowState.EXPLORATION);
        new SaveGameUseCase(source).execute(2);

        var target = GameSessionFactory.createSessionForTheme("poison", "guerrero");
        target.transitionTo(GameFlowState.MENU);
        assertEquals(GameFlowState.MENU.screenKey(), target.activeScreen());

        new LoadGameUseCase(target).execute(2);

        assertEquals(GameFlowState.EXPLORATION.screenKey(), target.activeScreen());
        assertNotEquals(GameFlowState.MENU.screenKey(), target.activeScreen());
    }
    
    @Test
    public void testIntegracionCompletaStateMementoEnFlujoNormal() {
        // Flujo completo: Menu → Exploration → Combat → Victory con saves
        
        // 1. Inicio en Menu - crear estado inicial con null y luego inicializar contexto
        MenuState menuInicial = new MenuState(null);
        contextoJuego = new GameStateContext(menuInicial);
        // Ahora actualizar la referencia del MenuState al contexto real
        // (ya que fue creado con null inicialmente)
        originator.setEstadoActual("Menu");
        caretaker.guardarEnMemoria(originator.guardar());
        
        // 2. Transición manual a Exploration (ya que MenuState tiene null context)
        contextoJuego.cambiarEstado(new ExplorationState(contextoJuego));
        assertEquals("Exploration", contextoJuego.getEstadoActual().getNombre());
        originator.setEstadoActual("Exploration");
        caretaker.guardarEnMemoria(originator.guardar());
        
        // 3. Encontrar enemigo (Exploration → Combat)
        GameState exploración = contextoJuego.getEstadoActual();
        contextoJuego.cambiarEstado(new CombatState(contextoJuego, exploración));
        assertEquals("Combat", contextoJuego.getEstadoActual().getNombre());
        originator.setEstadoActual("Combat");
        originator.recibirDanio(15); // Recibe daño en combate
        caretaker.guardarEnMemoria(originator.guardar());
        
        // 4. Ganar combate (Combat → Victory)
        contextoJuego.cambiarEstado(new GameOverState(contextoJuego, true));
        assertEquals("Victory", contextoJuego.getEstadoActual().getNombre());
        originator.setEstadoActual("Victory");
        originator.progresar(); // Completar progreso
        caretaker.guardarEnMemoria(originator.guardar());
        
        // Validaciones finales
        assertEquals(4, caretaker.getCantidadMementos(),
            "Debe tener 4 checkpoints del flujo completo");
        
        // Poder volver a cualquier punto
        GameMemento combat = caretaker.obtenerMemento(2);  // Checkpoint en Combat
        originator.restaurar(combat);
        assertEquals("Combat", originator.getEstadoActual());
        assertEquals(85, originator.getVidaJugador());
        
        // Este test demuestra cómo State y Memento colaboran:
        // - State: Gestiona las transiciones entre estados del juego
        // - Memento: Captura y restaura el estado completo en cualquier momento
        // - Juntos: Permiten un sistema de save/load robusto
    }
}
