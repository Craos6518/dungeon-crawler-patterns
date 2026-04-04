package game.domain.combat;

import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.ai.strategy.AggressiveStrategy;
import game.ai.strategy.DefensiveStrategy;
import game.ai.strategy.RandomStrategy;
import game.command.actions.AttackCommand;
import game.command.actions.Command;
import game.command.actions.CommandInvoker;
import game.command.actions.DefendCommand;
import game.command.actions.SkillCommand;
import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.turn.TurnManager;

import java.util.List;
import java.util.Random;

/**
 * Servicio de dominio de combate por turnos.
 */
public class CombatSystem {

    public int playerAttack(Player player, Enemy enemy, CommandInvoker invoker) {
        AttackCommand attack = new AttackCommand(player.character(), enemy.character());
        if (!attack.canExecute()) {
            throw new IllegalStateException("No se puede ejecutar el ataque ahora.");
        }
        invoker.ejecutarComando(attack);
        return attack.getDanioAplicado();
    }

    public void playerDefend(Player player, TurnManager turnManager, CommandInvoker invoker) {
        DefendCommand defend = new DefendCommand(player.character());
        if (!defend.canExecute()) {
            throw new IllegalStateException("No se puede activar defensa en este momento.");
        }
        invoker.ejecutarComando(defend);
        turnManager.activateDefense();
    }

    public int playerSkill(Player player, Enemy enemy, String skillName, CommandInvoker invoker) {
        SkillCommand skill = new SkillCommand(player.character(), skillName, enemy.character());
        if (!skill.canExecute()) {
            throw new IllegalStateException("No se puede ejecutar la habilidad en este momento.");
        }

        invoker.ejecutarComando(skill);

        int damage = 35 + Math.max(0, player.level() - 1) * 5;
        enemy.receiveDamage(damage);
        return damage;
    }

    public EnemyTurnOutcome enemyTurn(
        Player player,
        Enemy enemy,
        AIController enemyAI,
        TurnManager turnManager,
        String themeKey,
        Random random,
        CommandInvoker invoker
    ) {
        EnemyTurnOutcome outcome = new EnemyTurnOutcome();

        AIStrategy next = selectEnemyStrategy(enemy);
        if (!enemyAI.getEstrategia().getNombreEstrategia().equals(next.getNombreEstrategia())) {
            enemyAI.setEstrategia(next);
            outcome.strategyChanged = true;
            outcome.newStrategyName = next.getNombreEstrategia();
        }

        Command enemyAction = enemyAI.decidirAccion(List.of(player.character()));
        int hpBeforeHit = player.hp();
        invoker.ejecutarComando(enemyAction);

        if (enemyAction instanceof AttackCommand attack) {
            outcome.rawDamage = attack.getDanioAplicado();
            outcome.mitigatedDamage = turnManager.mitigateIncomingDamage(outcome.rawDamage);
            boolean defeatedByRawHit = !player.isAlive();

            // La mitigación no debe revivir al jugador si el golpe ya fue letal.
            if (outcome.mitigatedDamage > 0 && !defeatedByRawHit) {
                player.heal(outcome.mitigatedDamage);
            }

            if (defeatedByRawHit) {
                outcome.mitigatedDamage = 0;
            }

            outcome.finalDamage = Math.max(0, hpBeforeHit - player.hp());

            if (player.isAlive() && "poison".equals(themeKey) && random.nextInt(100) < 35) {
                turnManager.applyPoison(3, 4);
                outcome.poisonApplied = true;
            }
        } else if (enemyAction instanceof DefendCommand) {
            outcome.enemyDefended = true;
        }

        return outcome;
    }

    public AIController createEnemyAI(Enemy enemy, boolean bossFight) {
        AIStrategy strategy = bossFight ? new AggressiveStrategy() : new RandomStrategy();
        return new AIController(enemy.character(), strategy);
    }

    private AIStrategy selectEnemyStrategy(Enemy enemy) {
        int hp = enemy.hp();
        if (hp > 70) {
            return new AggressiveStrategy();
        }
        if (hp > 35) {
            return new RandomStrategy();
        }
        return new DefensiveStrategy();
    }

    public static class EnemyTurnOutcome {
        public int rawDamage;
        public int mitigatedDamage;
        public int finalDamage;

        public boolean enemyDefended;
        public boolean poisonApplied;
        public boolean strategyChanged;
        public String newStrategyName;
    }
}
