package game.domain.combat;

import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.ai.strategy.AggressiveStrategy;
import game.ai.strategy.DefensiveStrategy;
import game.ai.strategy.RandomStrategy;
import game.patterns.command.actions.AttackCommand;
import game.patterns.command.actions.Command;
import game.patterns.command.actions.CommandInvoker;
import game.patterns.command.actions.DefendCommand;
import game.patterns.command.actions.SkillCommand;
import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.turn.TurnManager;

import java.util.List;
import java.util.Random;

/**
 * Servicio de dominio de combate por turnos.
 */
public class CombatSystem {

    private static final int INITIATIVE_GAP = 4;
    private static final int DOUBLE_TURN_GAP = 10;
    private static final int MAX_EVASION_CHANCE = 35;

    public int playerAttack(Player player, Enemy enemy, CommandInvoker invoker) {
        if (player == null || enemy == null || !player.isAlive() || !enemy.isAlive()) {
            throw new IllegalStateException("No se puede ejecutar el ataque ahora.");
        }

        AttackCommand attackCommand = new AttackCommand(
            player.character(),
            enemy.character(),
            (attacker, defender) -> {
                int attackPower = computePlayerAttackPower(player, enemy, false);
                int targetDefense = effectiveEnemyDefense(player, enemy, false);
                int damage = applyDefenseFormula(attackPower, targetDefense);
                defender.recibirDanio(damage);
                return damage;
            }
        );

        if (!attackCommand.canExecute()) {
            throw new IllegalStateException("No se puede ejecutar el ataque ahora.");
        }

        invoker.execute(attackCommand);
        return attackCommand.getDanioAplicado();
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

        int attackPower = computePlayerAttackPower(player, enemy, true);
        int targetDefense = effectiveEnemyDefense(player, enemy, true);
        int damage = applyDefenseFormula(attackPower, targetDefense);
        enemy.receiveDamage(damage);
        return damage;
    }

    public boolean enemyActsFirst(Player player, Enemy enemy) {
        return enemy.speedStat() - player.speedStat() >= INITIATIVE_GAP;
    }

    public int playerActionCount(Player player, Enemy enemy) {
        return player.speedStat() - enemy.speedStat() >= DOUBLE_TURN_GAP ? 2 : 1;
    }

    public int enemyActionCount(Player player, Enemy enemy) {
        return enemy.speedStat() - player.speedStat() >= DOUBLE_TURN_GAP ? 2 : 1;
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

        if (enemyAction instanceof AttackCommand) {
            int hpBeforeHit = player.hp();
            if (playerEvadesIncomingAttack(player, enemy, random)) {
                outcome.attackEvaded = true;
                outcome.rawDamage = 0;
                outcome.finalDamage = 0;
                return outcome;
            }

            AttackCommand executedEnemyAttack = new AttackCommand(
                enemy.character(),
                player.character(),
                (attacker, defender) -> {
                    int damage = applyDefenseFormula(
                        computeEnemyAttackPower(enemy),
                        player.defenseStat()
                    );
                    defender.recibirDanio(damage);
                    return damage;
                }
            );

            if (!executedEnemyAttack.canExecute()) {
                throw new IllegalStateException("No se puede ejecutar el ataque enemigo en este momento.");
            }

            invoker.execute(executedEnemyAttack);
            outcome.rawDamage = executedEnemyAttack.getDanioAplicado();
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

            if (player.isAlive() && outcome.rawDamage > 0 && "poison".equals(themeKey) && random.nextInt(100) < 35) {
                turnManager.applyPoison(3, 4);
                outcome.poisonApplied = true;
            }
        } else if (enemyAction instanceof DefendCommand) {
            invoker.execute(enemyAction);
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

    private int computePlayerAttackPower(Player player, Enemy enemy, boolean skillAttack) {
        int base = player.attackStat();
        return switch (player.heroType()) {
            case "guerrero" -> base + (player.defenseStat() / 3) + (skillAttack ? 8 : 0);
            case "mago" -> base + (skillAttack ? 16 : 10);
            case "arquero" -> {
                int speedGap = Math.max(0, player.speedStat() - enemy.speedStat());
                double multiplier = 1.0 + Math.min(0.50, speedGap * 0.03);
                if (skillAttack) {
                    multiplier += 0.20;
                }
                yield Math.max(1, (int) Math.round(base * multiplier));
            }
            default -> base + (skillAttack ? 6 : 0);
        };
    }

    private int effectiveEnemyDefense(Player player, Enemy enemy, boolean skillAttack) {
        int defense = enemy.defenseStat();

        if ("mago".equals(player.heroType())) {
            int penetration = Math.max(4, (int) Math.round(defense * 0.35));
            defense = Math.max(0, defense - penetration);
        }

        if (skillAttack) {
            defense = Math.max(0, defense - 6);
        }

        return defense;
    }

    private int computeEnemyAttackPower(Enemy enemy) {
        int speedBonus = Math.max(0, enemy.speedStat() - 10) / 3;
        return Math.max(1, enemy.attackStat() + speedBonus);
    }

    private boolean playerEvadesIncomingAttack(Player player, Enemy enemy, Random random) {
        int speedGap = player.speedStat() - enemy.speedStat();
        if (speedGap <= 0) {
            return false;
        }

        int evadeChance = Math.min(MAX_EVASION_CHANCE, 8 + speedGap * 2);
        return random.nextInt(100) < evadeChance;
    }

    private int applyDefenseFormula(int attack, int defense) {
        int safeAttack = Math.max(1, attack);
        int safeDefense = Math.max(0, defense);
        double reduced = (safeAttack * 100.0) / (100.0 + safeDefense);
        return Math.max(1, (int) Math.round(reduced));
    }

    public static class EnemyTurnOutcome {
        public int rawDamage;
        public int mitigatedDamage;
        public int finalDamage;

        public boolean enemyDefended;
        public boolean poisonApplied;
        public boolean attackEvaded;
        public boolean strategyChanged;
        public String newStrategyName;
    }
}
