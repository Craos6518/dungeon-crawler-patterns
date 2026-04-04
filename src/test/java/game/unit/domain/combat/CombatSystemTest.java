package game.unit.domain.combat;

import game.ai.strategy.AIController;
import game.ai.strategy.AggressiveStrategy;
import game.command.actions.CommandInvoker;
import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.combat.CombatSystem;
import game.domain.personaje.EnemigoBasico;
import game.domain.turn.TurnManager;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatSystemTest {

    @Test
    void playerAttackReducesEnemyHealth() {
        Player player = Player.demo();
        Enemy enemy = new Enemy(new EnemigoBasico("Goblin de Prueba", 40, 6));
        CombatSystem system = new CombatSystem();

        int hpBefore = enemy.hp();
        int damage = system.playerAttack(player, enemy, new CommandInvoker());

        assertTrue(damage > 0);
        assertTrue(enemy.hp() < hpBefore);
    }

    @Test
    void defenseMitigatesNonLethalEnemyHit() {
        Player player = Player.demo();
        player.receiveDamage(140); // 10 HP restantes

        Enemy enemy = new Enemy(new EnemigoBasico("Lobo", 80, 6));
        CombatSystem system = new CombatSystem();
        TurnManager turnManager = new TurnManager();
        turnManager.activateDefense();

        var outcome = system.enemyTurn(
            player,
            enemy,
            new AIController(enemy.character(), new AggressiveStrategy()),
            turnManager,
            "ice",
            new Random(1),
            new CommandInvoker()
        );

        assertTrue(player.isAlive());
        assertEquals(7, player.hp());
        assertEquals(3, outcome.mitigatedDamage);
        assertEquals(3, outcome.finalDamage);
    }

    @Test
    void defenseDoesNotRevivePlayerAfterLethalHit() {
        Player player = Player.demo();
        player.receiveDamage(147); // 3 HP restantes

        Enemy enemy = new Enemy(new EnemigoBasico("Lobo", 80, 6));
        CombatSystem system = new CombatSystem();
        TurnManager turnManager = new TurnManager();
        turnManager.activateDefense();

        var outcome = system.enemyTurn(
            player,
            enemy,
            new AIController(enemy.character(), new AggressiveStrategy()),
            turnManager,
            "ice",
            new Random(1),
            new CommandInvoker()
        );

        assertFalse(player.isAlive());
        assertEquals(0, player.hp());
        assertEquals(0, outcome.mitigatedDamage);
        assertEquals(3, outcome.finalDamage);
    }
}
