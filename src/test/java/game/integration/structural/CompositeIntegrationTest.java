package game.integration.structural;

import game.application.state.GameSession;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.SaveGameUseCase;
import game.domain.inventory.Inventory;
import game.items.model.ContainerItem;
import game.items.model.SimpleItem;
import game.application.state.GameSessionFactory;
import game.infrastructure.persistence.memento.GameCaretaker;
import game.application.ports.persistence.SessionSnapshotStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeIntegrationTest {

    private GameSession session;
    private SaveGameUseCase saveUseCase;
    private LoadGameUseCase loadUseCase;
    private Path tempSaveDir;

    @BeforeEach
    void setUp() throws IOException {
        tempSaveDir = Files.createTempDirectory("dungeon-save-test-composite");
        SessionSnapshotStore caretaker = new GameCaretaker(tempSaveDir.toString());
        
        // Crear sesión manual para inyectar el caretaker de prueba
        session = GameSessionFactory.createSessionForTheme("fire");
        session = new GameSession(
            session.player(), 
            session.dungeon(), 
            session.combat(), 
            session.eventManager(), 
            caretaker
        );

        saveUseCase = new SaveGameUseCase(session);
        loadUseCase = new LoadGameUseCase(session);
    }

    @Test
    void testInventoryHierarchyPersistence() {
        // 1. Crear árbol anidado
        ContainerItem mochila = new ContainerItem("Mochila", "Principal", 10, 2);
        SimpleItem pocion = new SimpleItem("Pocion", "Vida", "Consumible", 10, 1);
        ContainerItem bolsaSecreta = new ContainerItem("Bolsa Secreta", "Oculta", 5, 1);
        SimpleItem gema = new SimpleItem("Gema", "Valiosa", "Tesoro", 100, 1);
        SimpleItem llave = new SimpleItem("Llave", "Puerta", "Mision", 0, 1);

        bolsaSecreta.agregar(gema);
        bolsaSecreta.agregar(llave);
        mochila.agregar(pocion);
        mochila.agregar(bolsaSecreta);

        // 2. Asignar ese inventario al jugador en una sesión
        session.inventory().importTree(mochila, 0);

        // 3. Llamar SaveGameUseCase.save(slot=1)
        saveUseCase.execute(1);

        // 4. Modificar el inventario (añadir un ítem al nivel raíz)
        session.inventory().add(new SimpleItem("Relleno", "Extra", "Basura", 1, 1));
        
        // 5. Llamar LoadGameUseCase.load(slot=1)
        loadUseCase.execute(1);

        // 6. Verificar que la jerarquía original se restauró
        Inventory inventory = session.inventory();
        ContainerItem root = inventory.exportTree();
        
        assertEquals("Mochila", root.getNombre());
        assertEquals(2, root.obtenerItems().size(), "Mochila debe tener 2 hijos directos");
        
        boolean foundBolsa = false;
        for (var item : root.obtenerItems()) {
            if (item instanceof ContainerItem nested && nested.getNombre().equals("Bolsa Secreta")) {
                foundBolsa = true;
                assertEquals(2, nested.obtenerItems().size(), "Bolsa Secreta debe tener 2 hijos");
                assertTrue(nested.obtenerItems().stream().anyMatch(i -> i.getNombre().equals("Gema")));
                assertTrue(nested.obtenerItems().stream().anyMatch(i -> i.getNombre().equals("Llave")));
            }
        }
        assertTrue(foundBolsa, "No se encontró la Bolsa Secreta en el inventario restaurado");
        assertTrue(root.obtenerItems().stream().anyMatch(i -> i.getNombre().equals("Pocion")));
        assertFalse(root.obtenerItems().stream().anyMatch(i -> i.getNombre().equals("Relleno")), "El item de relleno no debería estar");
    }
    @Test
    void testLoadCorruptedInventory() throws IOException {
        session.inventory().add(new SimpleItem("Pocion", "Vida", "Consumible", 10, 1));
        saveUseCase.execute(1);

        // Corromper el archivo físicamente
        Path saveFile = tempSaveDir.resolve("Slot_1.save");
        Files.write(saveFile, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}); // Basura binaria

        // Intentar cargar debe lanzar SaveDataCorruptionException
        assertThrows(game.infrastructure.persistence.memento.SaveDataCorruptionException.class, () -> {
            loadUseCase.execute(1);
        });
    }
}
