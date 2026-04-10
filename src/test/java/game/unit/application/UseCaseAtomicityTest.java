package game.unit.application;

import game.application.dto.AttackCommandRequest;
import game.application.state.GameSession;
import game.application.usecase.AttackUseCase;
import game.application.usecase.ForceCombatUseCase;
import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.exploration.Dungeon;
import game.domain.turn.TurnManager;
import game.infrastructure.events.observer.EventManager;
import game.infrastructure.persistence.memento.GameCaretaker;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCaseAtomicityTest {

    @Test
    void attackRollbackRestoresStateWhenFailureOccursMidUseCase() {
        GameSession session = newSessionWithExplodingCombat();
        new ForceCombatUseCase(session).execute();

        int enemyHpBefore = session.combat().currentEnemy().hp();
        int combatLogSizeBefore = session.combatLog().size();
        int eventLogSizeBefore = session.eventLog().size();
        String screenBefore = session.activeScreen();

        AttackUseCase attack = new AttackUseCase(session);
        AttackCommandRequest request = new AttackCommandRequest();
        request.targetId = "current";

        RuntimeException ex = assertThrows(RuntimeException.class, () -> attack.execute(request));
        assertTrue(ex.getMessage().contains("Fallo inducido"));

        assertTrue(session.combat().isActive());
        assertEquals(enemyHpBefore, session.combat().currentEnemy().hp());
        assertEquals(combatLogSizeBefore, session.combatLog().size());
        assertEquals(eventLogSizeBefore, session.eventLog().size());
        assertEquals(screenBefore, session.activeScreen());
    }

    private static GameSession newSessionWithExplodingCombat() {
        Random random = new Random(7);
        Player player = Player.demo();
        Dungeon dungeon = Dungeon.demo(random);
        TurnManager turnManager = new TurnManager();
        Combat combat = new ExplodingCombat(player, turnManager, random);

        EventManager eventManager = EventManager.getInstance();
        eventManager.limpiar();

        GameCaretaker caretaker = new GameCaretaker("./test-saves/atomicity/");
        return new GameSession(player, dungeon, combat, eventManager, caretaker);
    }

    private static final class ExplodingCombat extends Combat {

        private ExplodingCombat(Player player, TurnManager turnManager, Random random) {
            super(player, turnManager, random);
        }

        @Override
        public void resolveTurn() {
            throw new RuntimeException("Fallo inducido para probar rollback.");
        }
    }
}