package game.unit.domain.combat;

import game.command.actions.CommandInvoker;
import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.combat.CombatSystem;
import game.domain.personaje.EnemigoBasico;
import org.junit.jupiter.api.Test;

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
}
