package game.unit.behavioral;

import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para el patrón Memento
 */
public class MementoPatternTest {
    
    private GameOriginator juego;
    private GameCaretaker caretaker;
    
    @BeforeEach
    public void setUp() {
        juego = new GameOriginator("Héroe de Prueba");
        caretaker = new GameCaretaker("./test-saves/");
        
        // Limpiar directorio de test
        File dir = new File("./test-saves/");
        if (dir.exists()) {
            File[] archivos = dir.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    archivo.delete();
                }
            }
        }
    }
    
    @Test
    public void testCreateMemento() {
        GameMemento memento = juego.guardar();
        
        assertNotNull(memento);
        assertEquals("Héroe de Prueba", memento.getNombreJugador());
        assertEquals(1, memento.getNivelActual());
        assertEquals(1, memento.getSalaActual());
        assertNotNull(memento.getFechaGuardado());
    }
    
    @Test
    public void testRestoreFromMemento() {
        // Progresar en el juego
        juego.progresar();
        juego.progresar();
        
        int salaActual = juego.getSalaActual();
        assertTrue(salaActual > 1, "La sala actual debería haber aumentado");
        
        // Crear un nuevo juego y restaurar desde memento vacío
        GameOriginator juegoNuevo = new GameOriginator("Otro Héroe");
        GameMemento mementoOriginal = juego.guardar();
        
        juegoNuevo.restaurar(mementoOriginal);
        
        assertEquals("Héroe de Prueba", juegoNuevo.getNombreJugador());
        assertEquals(salaActual, juegoNuevo.getSalaActual());
    }
    
    @Test
    public void testMementoPreservesState() {
        juego.progresar(); // Sala 2, Exp 50
        
        int vidaAntes = juego.getVidaJugador();
        int expAntes = juego.getExperiencia();
        int salaAntes = juego.getSalaActual();
        
        GameMemento memento = juego.guardar();
        
        // Cambiar el estado
        juego.recibirDanio(30);
        int vidaDespues = juego.getVidaJugador();
        
        // Verificar que el estado cambió
        assertNotEquals(vidaAntes, vidaDespues, "La vida debería haber descendido");
        assertEquals(vidaAntes - 30, vidaDespues);
        
        // Restaurar
        juego.restaurar(memento);
        
        // Verificar que se restauró al estado anterior
        assertEquals(vidaAntes, juego.getVidaJugador());
        assertEquals(expAntes, juego.getExperiencia());
        assertEquals(salaAntes, juego.getSalaActual());
    }
    
    @Test
    public void testCaretakerStoresMultipleMementos() {
        GameMemento memento1 = juego.guardar();
        caretaker.guardarEnMemoria(memento1);
        
        juego.progresar();
        GameMemento memento2 = juego.guardar();
        caretaker.guardarEnMemoria(memento2);
        
        juego.progresar();
        GameMemento memento3 = juego.guardar();
        caretaker.guardarEnMemoria(memento3);
        
        assertEquals(3, caretaker.getCantidadMementos());
        
        GameMemento recuperado = caretaker.obtenerMemento(1);
        assertEquals(memento2, recuperado);
    }
    
    @Test
    public void testCaretakerGetLastMemento() {
        juego.progresar();
        GameMemento memento1 = juego.guardar();
        caretaker.guardarEnMemoria(memento1);
        
        juego.progresar();
        GameMemento memento2 = juego.guardar();
        caretaker.guardarEnMemoria(memento2);
        
        GameMemento ultimo = caretaker.obtenerUltimoMemento();
        
        assertEquals(memento2, ultimo);
    }
    
    @Test
    public void testCaretakerThrowsExceptionWhenEmpty() {
        assertThrows(IllegalStateException.class, () -> {
            caretaker.obtenerUltimoMemento();
        });
    }
    
    @Test
    public void testCaretakerThrowsExceptionForInvalidIndex() {
        GameMemento memento = juego.guardar();
        caretaker.guardarEnMemoria(memento);
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            caretaker.obtenerMemento(5);
        });
    }
    
    @Test
    public void testSaveToDisk() {
        juego.progresar();
        juego.progresar();
        
        GameMemento memento = juego.guardar();
        
        assertDoesNotThrow(() -> {
            caretaker.guardarEnDisco(memento, "test_partida");
        });
        
        // Verificar que el archivo existe
        File archivo = new File("./test-saves/test_partida.save");
        assertTrue(archivo.exists(), "El archivo de guardado debería existir");
    }
    
    @Test
    public void testLoadFromDisk() {
        juego.progresar();
        juego.progresar();
        int salaGuardada = juego.getSalaActual();
        
        GameMemento memento = juego.guardar();
        caretaker.guardarEnDisco(memento, "test_carga");
        
        // Crear nuevo juego y cargar
        GameOriginator juegoNuevo = new GameOriginator("Nuevo Héroe");
        GameMemento cargado = caretaker.cargarDesdeDisco("test_carga");
        
        juegoNuevo.restaurar(cargado);
        
        assertEquals("Héroe de Prueba", juegoNuevo.getNombreJugador());
        assertEquals(salaGuardada, juegoNuevo.getSalaActual());
    }
    
    @Test
    public void testListSavedGames() {
        GameMemento memento1 = juego.guardar();
        caretaker.guardarEnDisco(memento1, "save1");
        
        juego.progresar();
        GameMemento memento2 = juego.guardar();
        caretaker.guardarEnDisco(memento2, "save2");
        
        var guardados = caretaker.listarGuardados();
        
        assertTrue(guardados.size() >= 2);
        assertTrue(guardados.contains("save1"));
        assertTrue(guardados.contains("save2"));
    }
    
    @Test
    public void testDeleteSavedGame() {
        GameMemento memento = juego.guardar();
        caretaker.guardarEnDisco(memento, "test_delete");
        
        assertTrue(caretaker.listarGuardados().contains("test_delete"));
        
        boolean eliminado = caretaker.eliminarGuardado("test_delete");
        
        assertTrue(eliminado);
        assertFalse(caretaker.listarGuardados().contains("test_delete"));
    }
    
    @Test
    public void testMementoIsImmutable() {
        GameMemento memento = juego.guardar();
        
        int nivelOriginal = memento.getNivelActual();
        
        // Los getters retornan copias, así que modificarlas no debería afectar el memento
        var estado = memento.getEstadoPersonaje();
        estado.put("vida", 0);
        
        // El memento original no debería cambiar
        assertEquals(nivelOriginal, memento.getNivelActual());
    }
    
    @Test
    public void testClearHistory() {
        caretaker.guardarEnMemoria(juego.guardar());
        caretaker.guardarEnMemoria(juego.guardar());
        
        assertEquals(2, caretaker.getCantidadMementos());
        
        caretaker.limpiarHistorial();
        
        assertEquals(0, caretaker.getCantidadMementos());
    }
}
