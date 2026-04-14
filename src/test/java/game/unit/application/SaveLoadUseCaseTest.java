package game.unit.application;

import game.application.state.GameSessionFactory;
import game.application.usecase.ForceCombatUseCase;
import game.application.usecase.LoadGameUseCase;
import game.application.usecase.SaveGameUseCase;
import game.domain.DomainRuleViolationException;
import game.application.state.GameMemento;
import game.infrastructure.persistence.memento.GameCaretaker;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

        GameMemento memento = session.caretaker().cargarDesdeDisco("Slot_3");
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
        assertEquals(0, ((GameCaretaker) session.caretaker()).getCantidadMementos());
    }

    @Test
    void saveRejectsBootstrapMenuSession() {
        var session = GameSessionFactory.createInitialMenuSession();
        session.setActiveScreen("saves");

        SaveGameUseCase save = new SaveGameUseCase(session);

        DomainRuleViolationException ex = assertThrows(DomainRuleViolationException.class, () -> save.execute(1));
        assertTrue(ex.getMessage().contains("antes de iniciar o cargar"));
        assertEquals(0, ((GameCaretaker) session.caretaker()).getCantidadMementos());
    }

    @Test
    void loadRestoresSavedSessionState() {
        var source = GameSessionFactory.createSessionForTheme("fire", "guerrero");
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
        String expectedHeroLabel = source.player().name();
        new SaveGameUseCase(source).execute(2);

        var target = GameSessionFactory.createSessionForTheme("fire", "guerrero");
        LoadGameUseCase load = new LoadGameUseCase(target);
        load.execute(2);

        assertEquals(expectedGold, target.player().gold());
        assertEquals(expectedRoom, target.dungeon().currentRoomIndex());
        assertEquals(expectedInventorySize, target.inventory().size());
        assertEquals(expectedSelectedIndex, target.inventory().selectedIndex());
        assertEquals(expectedPoisonTurns, target.combat().poisonTurns());
        assertEquals(expectedPoisonDamage, target.combat().poisonDamage());
        assertEquals(expectedDefense, target.combat().isDefenseActive());
        assertEquals(expectedHeroLabel, target.player().name());
        assertTrue(!target.combat().isActive());
        assertEquals("inventory", target.activeScreen());
    }

    @Test
    void loadRejectsMissingSlotAndKeepsSessionUntouched() {
        var session = GameSessionFactory.createDemoSession();
        ((GameCaretaker) session.caretaker()).eliminarGuardado("Slot_3");

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

    @Test
    void loadToleratesInvalidInventoryItemEntriesInLegacySaves() {
        var source = GameSessionFactory.createSessionForTheme("poison", "guerrero");
        new SaveGameUseCase(source).execute(1);

        GameMemento valid = source.caretaker().cargarDesdeDisco("Slot_1");
        Map<String, Object> characterState = new HashMap<>(valid.getEstadoPersonaje());
        Map<String, Object> inventoryState = new HashMap<>(valid.getEstadoInventario());
        Map<String, Object> dungeonState = new HashMap<>(valid.getEstadoMazmorra());

        Object rawItems = inventoryState.get("items");
        List<Map<String, Object>> items = new ArrayList<>();
        if (rawItems instanceof List<?> list) {
            for (Object entry : list) {
                if (entry instanceof Map<?, ?> map) {
                    Map<String, Object> cloned = new HashMap<>();
                    for (Map.Entry<?, ?> e : map.entrySet()) {
                        if (e.getKey() != null) {
                            cloned.put(String.valueOf(e.getKey()), e.getValue());
                        }
                    }
                    items.add(cloned);
                }
            }
        }

        Map<String, Object> invalidItem = new HashMap<>();
        invalidItem.put("nombre", "   ");
        invalidItem.put("descripcion", "entrada legacy invalida");
        invalidItem.put("tipo", "Consumible");
        invalidItem.put("valor", 50);
        invalidItem.put("peso", 0);
        items.add(invalidItem);
        inventoryState.put("items", items);

        GameMemento legacyWithCorruptItem = buildMementoFromMaps(
            valid,
            characterState,
            inventoryState,
            dungeonState
        );

        var target = GameSessionFactory.createSessionForTheme("poison", "guerrero");
        LoadGameUseCase load = new LoadGameUseCase(target);

        assertDoesNotThrow(() -> load.restoreFromMemento("Slot_legacy", legacyWithCorruptItem));
        assertEquals(source.player().name(), target.player().name());
        assertEquals(source.inventory().size(), target.inventory().size());
    }

    private static GameMemento buildMementoFromMaps(
        GameMemento base,
        Map<String, Object> characterState,
        Map<String, Object> inventoryState,
        Map<String, Object> dungeonState
    ) {
        GameMemento.Builder builder = new GameMemento.Builder()
            .nombreJugador(base.getNombreJugador())
            .nivelActual(base.getNivelActual())
            .salaActual(base.getSalaActual());

        characterState.forEach(builder::agregarEstadoPersonaje);
        inventoryState.forEach(builder::agregarEstadoInventario);
        dungeonState.forEach(builder::agregarEstadoMazmorra);
        return builder.build();
    }
}
