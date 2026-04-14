package game.unit.application;

import game.application.dto.UseItemCommandRequest;
import game.application.state.GameSession;
import game.application.usecase.UseItemUseCase;
import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.exploration.Dungeon;
import game.domain.inventory.Inventory;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.turn.TurnManager;
import game.infrastructure.events.observer.EventManager;
import game.items.model.ContainerItem;
import game.items.model.SimpleItem;
import game.infrastructure.persistence.memento.GameCaretaker;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseItemUseCaseCompositeHierarchyTest {

    @Test
    void useItemByFlatIndexConsumesNestedCompositeItem() {
        GameSession session = newSessionWithNestedInventory();
        int before = session.inventory().size();

        UseItemCommandRequest request = new UseItemCommandRequest();
        request.itemIndex = 0;
        new UseItemUseCase(session).execute(request);

        assertEquals(before - 1, session.inventory().size());
        assertTrue(session.eventLog().stream().anyMatch(line -> line.contains("Usaste")));
    }

    private static GameSession newSessionWithNestedInventory() {
        ContainerItem backpack = new ContainerItem("Mochila", "Inventario principal", 10, 2);
        ContainerItem pocket = new ContainerItem("Bolso", "Compartimento interno", 5, 1);
        pocket.agregar(new SimpleItem("Pocion de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        backpack.agregar(pocket);

        Inventory inventory = new Inventory(backpack);
        Player player = new Player(
            new GuerreroFactory(110, 20).crearPersonaje("Heroe"),
            inventory,
            "guerrero"
        );

        Combat combat = new Combat(player, new TurnManager(), new Random(9));
        Dungeon dungeon = Dungeon.demo(new Random(9));
        EventManager eventManager = EventManager.getInstance();
        eventManager.limpiar();

        return new GameSession(
            player,
            dungeon,
            combat,
            eventManager,
            new GameCaretaker("./test-saves/composite-hierarchy/")
        );
    }
}
