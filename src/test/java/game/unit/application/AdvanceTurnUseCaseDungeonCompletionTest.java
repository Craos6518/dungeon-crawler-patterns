package game.unit.application;

import game.application.state.GameSession;
import game.application.state.GameSessionFactory;
import game.application.usecase.AdvanceTurnUseCase;
import game.domain.DomainRuleViolationException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdvanceTurnUseCaseDungeonCompletionTest {

    @Test
    void advanceFromLastBossRoomAfterDefeatTransitionsToHeroSelection() {
        GameSession session = GameSessionFactory.createSessionForTheme("fire", "mago");
        moveToLastRoom(session);
        session.dungeon().markCurrentRoomEnemyResolved();
        session.setActiveScreen("exploration");

        assertEquals("default", session.buttonsState().get("btn-avanzar"));

        new AdvanceTurnUseCase(session).execute();

        assertEquals("hero", session.activeScreen());
        assertTrue(session.isHeroSelectionLocked());
        assertTrue(session.isThemeCompleted("fire"));
    }

    @Test
    void advanceFromLastRoomWithoutBossDefeatStillFails() {
        GameSession session = GameSessionFactory.createSessionForTheme("fire", "guerrero");
        moveToLastRoom(session);
        session.setActiveScreen("exploration");

        DomainRuleViolationException ex = assertThrows(
            DomainRuleViolationException.class,
            () -> new AdvanceTurnUseCase(session).execute()
        );

        assertEquals("Ya estas en la ultima sala de la mazmorra.", ex.getMessage());
    }

    private static void moveToLastRoom(GameSession session) {
        int lastIndex = Math.max(0, session.dungeon().totalRooms() - 1);
        session.dungeon().restoreProgress(lastIndex, Set.of(), Set.of());
    }
}
