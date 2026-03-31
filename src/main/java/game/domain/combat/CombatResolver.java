package game.domain.combat;

import game.domain.character.Enemy;
import game.domain.character.Player;

/**
 * Servicio de dominio para resolver desenlaces de combate.
 */
public class CombatResolver {

    public void resolveVictory(Player player, Enemy enemy, CombatResult result) {
        int xpGained = Math.max(20, enemy.getExperienceReward());
        int levels = player.gainExperience(xpGained);

        result.enemyDefeated = true;
        result.gainedXp = xpGained;
        result.gainedLevels = levels;
    }

    public void resolveDefeat(Player player, CombatResult result) {
        if (!player.isAlive()) {
            result.playerDefeated = true;
        }
    }
}
