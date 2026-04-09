package game.unit.domain.combat;

import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.personaje.EnemigoBasico;
import game.domain.turn.TurnManager;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatDecoratorIntegrationTest {

    @Test
    void poisonAndOffensiveBuffDecoratorsAffectRealCombatStats() {
        CombatFixture decorated = newCombatFixture(17);
        CombatFixture baseline = newCombatFixture(17);

        decorated.combat().restoreTurnState(false, 1, 4);
        decorated.combat().restoreTacticalState("balanced", 2, 0, null, false);

        int hpBeforeDecorated = decorated.player().hp();
        int hpBeforeBaseline = baseline.player().hp();

        var decoratedResult = decorated.combat().attack("current", "ice");
        var baselineResult = baseline.combat().attack("current", "ice");

        int takenDecorated = hpBeforeDecorated - decorated.player().hp();
        int takenBaseline = hpBeforeBaseline - baseline.player().hp();

        assertEquals(4, takenDecorated - takenBaseline);
        assertTrue(decoratedResult.playerDamage > baselineResult.playerDamage);
        assertEquals(1, decorated.combat().offensiveBuffStacks());
        assertEquals(0, decorated.combat().poisonTurns());
    }

    @Test
    void guardDecoratorMitigatesIncomingDamageInRealCombatFlow() {
        CombatFixture guarded = newCombatFixture(23);
        CombatFixture baseline = newCombatFixture(23);

        guarded.combat().restoreTacticalState("balanced", 0, 2, null, false);

        int hpBeforeGuarded = guarded.player().hp();
        int hpBeforeBaseline = baseline.player().hp();

        guarded.combat().attack("current", "ice");
        baseline.combat().attack("current", "ice");

        int takenGuarded = hpBeforeGuarded - guarded.player().hp();
        int takenBaseline = hpBeforeBaseline - baseline.player().hp();

        assertEquals(6, takenBaseline - takenGuarded);
        assertEquals(1, guarded.combat().guardBuffStacks());
    }

    private static CombatFixture newCombatFixture(long seed) {
        Player player = Player.demo();
        Combat combat = new Combat(player, new TurnManager(), new Random(seed));
        Enemy enemy = new Enemy(
            new EnemigoBasico("Centinela", 900, 10),
            14,
            10,
            player.speedStat() + 4
        );
        combat.start(enemy, false);
        return new CombatFixture(player, combat);
    }

    private record CombatFixture(Player player, Combat combat) {
    }
}
