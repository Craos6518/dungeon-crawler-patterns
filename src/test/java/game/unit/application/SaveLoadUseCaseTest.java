package game.unit.application;

import game.application.state.GameSessionFactory;
import game.application.usecase.ForceCombatUseCase;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.SaveGameUseCase;
import game.domain.DomainRuleViolationException;
import game.persistence.memento.GameMemento;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaveLoadUseCaseTest {

    @Test
    void savePersistsExtendedDomainState() {
        var session = GameSessionFactory.createDemoSession();
        session.dungeon().restoreProgress(1, Set.of(0), Set.of(0));
        session.combat().restoreTurnState(true, 2, 4);
        session.player().addGold(80);
        session.inventory().moveSelectionDown();
        session.setActiveScreen("inventory");

        SaveGameUseCase save = new SaveGameUseCase(session);
        save.execute(3);

        GameMemento memento = session.caretaker().obtenerUltimoMemento();
        Map<String, Object> characterState = memento.getEstadoPersonaje();
        Map<String, Object> inventoryState = memento.getEstadoInventario();
        Map<String, Object> dungeonState = memento.getEstadoMazmorra();

        assertTrue(characterState.containsKey("venenoTurnos"));
        assertTrue(characterState.containsKey("venenoDanio"));
        assertTrue(characterState.containsKey("defensaActiva"));
        assertTrue(inventoryState.containsKey("selectedIndex"));
        assertTrue(dungeonState.containsKey("combateActivo"));
        assertTrue(dungeonState.containsKey("enemigoTipo"));
        assertTrue(dungeonState.containsKey("enemigoAtaque"));
        assertTrue(dungeonState.containsKey("salasTesoroResuelto"));
        assertTrue(dungeonState.containsKey("salasEnemigoResuelto"));
        assertTrue(dungeonState.containsKey("eventLog"));
        assertTrue(dungeonState.containsKey("combatLog"));
    }

    @Test
    void saveRejectsActiveCombatState() {
        var session = GameSessionFactory.createDemoSession();
        new ForceCombatUseCase(session).execute();

        SaveGameUseCase save = new SaveGameUseCase(session);

        assertThrows(DomainRuleViolationException.class, () -> save.execute(1));
        assertEquals(0, session.caretaker().getCantidadMementos());
    }

    @Test
    void saveRejectsBootstrapMenuSession() {
        var session = GameSessionFactory.createInitialMenuSession();
        session.setActiveScreen("saves");

        SaveGameUseCase save = new SaveGameUseCase(session);

        DomainRuleViolationException ex = assertThrows(DomainRuleViolationException.class, () -> save.execute(1));
        assertTrue(ex.getMessage().contains("antes de iniciar o cargar"));
        assertEquals(0, session.caretaker().getCantidadMementos());
    }

    @Test
    void loadRestoresSavedSessionState() {
        var source = GameSessionFactory.createSessionForTheme("fire", "guerrero", "Nyx");
        source.dungeon().restoreProgress(1, Set.of(0), Set.of(0));
        source.combat().restoreTurnState(true, 2, 4);
        source.player().addGold(55);
        source.inventory().moveSelectionDown();
        source.setActiveScreen("inventory");

        int expectedGold = source.player().gold();
        int expectedRoom = source.dungeon().currentRoomIndex();
        int expectedInventorySize = source.inventory().size();
        int expectedSelectedIndex = source.inventory().selectedIndex();
        int expectedPoisonTurns = source.combat().poisonTurns();
        int expectedPoisonDamage = source.combat().poisonDamage();
        boolean expectedDefense = source.combat().isDefenseActive();
        String expectedHeroName = source.player().name();
        new SaveGameUseCase(source).execute(2);

        var target = GameSessionFactory.createSessionForTheme("fire", "guerrero", "Otro");
        LoadGameUseCase load = new LoadGameUseCase(target);
        load.execute(2);

        assertEquals(expectedGold, target.player().gold());
        assertEquals(expectedRoom, target.dungeon().currentRoomIndex());
        assertEquals(expectedInventorySize, target.inventory().size());
        assertEquals(expectedSelectedIndex, target.inventory().selectedIndex());
        assertEquals(expectedPoisonTurns, target.combat().poisonTurns());
        assertEquals(expectedPoisonDamage, target.combat().poisonDamage());
        assertEquals(expectedDefense, target.combat().isDefenseActive());
        assertEquals(expectedHeroName, target.player().name());
        assertTrue(!target.combat().isActive());
        assertEquals("inventory", target.activeScreen());
    }

    @Test
    void loadRejectsMissingSlotAndKeepsSessionUntouched() {
        var session = GameSessionFactory.createDemoSession();
        session.caretaker().eliminarGuardado("Slot_3");

        int roomBefore = session.dungeon().currentRoomIndex();
        int goldBefore = session.player().gold();
        String screenBefore = session.activeScreen();

        LoadGameUseCase load = new LoadGameUseCase(session);
        DomainRuleViolationException ex = assertThrows(DomainRuleViolationException.class, () -> load.execute(3));

        assertTrue(ex.getMessage().contains("Slot vacio"));
        assertEquals(roomBefore, session.dungeon().currentRoomIndex());
        assertEquals(goldBefore, session.player().gold());
        assertEquals(screenBefore, session.activeScreen());
    }

    @Test
    void loadRejectsCorruptMementoAndKeepsSessionUntouched() {
        var session = GameSessionFactory.createDemoSession();

        int roomBefore = session.dungeon().currentRoomIndex();
        int goldBefore = session.player().gold();
        String screenBefore = session.activeScreen();

        GameMemento corrupt = new GameMemento.Builder()
            .nombreJugador(session.player().name())
            .nivelActual(session.player().level())
            .salaActual(1)
            .agregarEstadoPersonaje("nivel", session.player().level())
            .agregarEstadoPersonaje("vida", session.player().hp())
            .agregarEstadoPersonaje("vidaMaxima", session.player().maxHp())
            .agregarEstadoMazmorra("estadoActual", "combat")
            .agregarEstadoMazmorra("combateActivo", true)
            .agregarEstadoMazmorra("enemigoNombre", "Orco Corrupto")
            .build();

        session.caretaker().guardarEnDisco(corrupt, "Slot_1");

        LoadGameUseCase load = new LoadGameUseCase(session);
        assertThrows(DomainRuleViolationException.class, () -> load.execute(1));

        assertEquals(roomBefore, session.dungeon().currentRoomIndex());
        assertEquals(goldBefore, session.player().gold());
        assertEquals(screenBefore, session.activeScreen());
    }
}
