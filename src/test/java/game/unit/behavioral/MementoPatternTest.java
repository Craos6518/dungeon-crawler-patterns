package game.unit.behavioral;

import game.application.ports.persistence.SaveSlotNotFoundException;
import game.application.state.GameMemento;
import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.application.state.GameSessionMementoMapper;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.SaveGameUseCase;
import game.infrastructure.persistence.memento.GameCaretaker;
import game.infrastructure.persistence.memento.SaveDataCorruptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para el patrón Memento (Remediado)
 */
public class MementoPatternTest {
    
    private GameSession session;
    private GameCaretaker caretaker;
    private final String TEST_SAVE_DIR = "./test-saves-memento/";
    
    @BeforeEach
    public void setUp() {
        // Limpiar y crear directorio de test
        File dir = new File(TEST_SAVE_DIR);
        if (dir.exists()) {
            File[] archivos = dir.listFiles();
            if (archivos != null) {
                for (File archivo : archivos) {
                    archivo.delete();
                }
            }
        } else {
            dir.mkdirs();
        }

        caretaker = new GameCaretaker(TEST_SAVE_DIR);
        
        // Crear sesión inyectando el caretaker de test
        GameSession original = GameSessionFactory.createSessionForTheme("fire");
        session = new GameSession(
            original.player(),
            original.dungeon(),
            original.combat(),
            original.eventManager(),
            caretaker
        );
    }
    
    @Test
    public void testCreateMemento() {
        GameMemento memento = session.createSnapshot();
        
        assertNotNull(memento);
        assertEquals("Guerrero", memento.getNombreJugador());
        assertEquals("1.0", memento.getSchemaVersion());
    }
    
    @Test
    public void testRestoreFromMemento() {
        session.player().restoreProgress(2, 500, 100, 10, 0, 100);
        int nivelAntes = session.player().level();
        assertEquals(2, nivelAntes);
        
        GameMemento memento = session.createSnapshot();
        
        // Reset a nivel 1
        session.player().restoreProgress(1, 0, 100, 0, 0, 100);
        assertEquals(1, session.player().level());
        
        session.restoreSnapshot(memento);
        assertEquals(2, session.player().level());
    }
    
    @Test
    public void testEsquemaIncompatibleLanzaExcepcion() {
        GameMemento mementoLegacy = new GameMemento.Builder()
            .schemaVersion("0.9")
            .nombreJugador("Test")
            .nivelActual(1)
            .salaActual(1)
            .build();
            
        SaveDataCorruptionException ex = assertThrows(SaveDataCorruptionException.class, () -> {
            GameSessionMementoMapper.restoreStrict(session, mementoLegacy);
        });
        
        assertTrue(ex.getMessage().contains("Incompatible schema version: 0.9"));
    }

    @Test
    public void testSaveLoadUseCaseWithCorruption() throws IOException {
        SaveGameUseCase saveUC = new SaveGameUseCase(session);
        LoadGameUseCase loadUC = new LoadGameUseCase(session);
        
        saveUC.execute(1); // Guarda en Slot_1.save en TEST_SAVE_DIR
        
        // Corromper el archivo en disco manualmente
        File file = new File(TEST_SAVE_DIR + "Slot_1.save");
        assertTrue(file.exists());

        GameMemento corruptedMemento = new GameMemento.Builder()
            .schemaVersion("corrupt")
            .nombreJugador("Corrupto")
            .nivelActual(1)
            .salaActual(1)
            .build();
            
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(corruptedMemento);
        }
        
        assertThrows(SaveDataCorruptionException.class, () -> {
            loadUC.execute(1);
        });
    }

    @Test
    public void testSlotVacioLanzaExcepcion() {
        LoadGameUseCase loadUC = new LoadGameUseCase(session);
        assertThrows(SaveSlotNotFoundException.class, () -> {
            loadUC.execute(99);
        });
    }
}
