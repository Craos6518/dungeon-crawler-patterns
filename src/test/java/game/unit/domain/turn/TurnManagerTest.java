package game.unit.domain.turn;

import game.domain.turn.TurnManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurnManagerTest {

    @Test
    void defenseMitigatesOnlyOneIncomingHit() {
        TurnManager turnManager = new TurnManager();
        turnManager.activateDefense();

        int mitigatedFirst = turnManager.mitigateIncomingDamage(10);
        int mitigatedSecond = turnManager.mitigateIncomingDamage(10);

        assertEquals(5, mitigatedFirst);
        assertEquals(0, mitigatedSecond);
        assertFalse(turnManager.isDefenseActive());
    }

    @Test
    void poisonTicksDoNotSkipAndEventuallyStop() {
        TurnManager turnManager = new TurnManager();
        turnManager.applyPoison(2, 4);

        TurnManager.PoisonTick first = turnManager.tickPoison();
        TurnManager.PoisonTick second = turnManager.tickPoison();
        TurnManager.PoisonTick third = turnManager.tickPoison();

        assertTrue(first.active());
        assertEquals(4, first.damage());
        assertEquals(1, first.remainingTurns());

        assertTrue(second.active());
        assertEquals(4, second.damage());
        assertEquals(0, second.remainingTurns());

        assertFalse(third.active());
        assertEquals(0, third.damage());
    }
}
