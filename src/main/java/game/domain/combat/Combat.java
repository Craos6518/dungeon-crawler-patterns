package game.domain.combat;

import game.ai.strategy.AIController;
import game.command.actions.CommandInvoker;
import game.domain.DomainRuleViolationException;
import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.inventory.Item;
import game.domain.turn.TurnManager;

import java.util.Locale;
import java.util.Random;

/**
 * Agregado Combat. Encapsula estado y reglas de combate activo.
 */
public class Combat {

    private final Player player;
    private final TurnManager turnManager;
    private final CombatSystem system;
    private final CombatResolver resolver;
    private final CommandInvoker invoker;
    private final Random random;

    private Enemy currentEnemy;
    private AIController enemyAI;
    private boolean bossFight;

    public Combat(Player player, TurnManager turnManager, Random random) {
        this.player = player;
        this.turnManager = turnManager;
        this.system = new CombatSystem();
        this.resolver = new CombatResolver();
        this.invoker = new CommandInvoker();
        this.random = random;
    }

    public void start(Enemy enemy, boolean bossFight) {
        this.currentEnemy = enemy;
        this.enemyAI = system.createEnemyAI(enemy, bossFight);
        this.bossFight = bossFight;
        this.turnManager.resetForCombat();
    }

    public void finish() {
        this.currentEnemy = null;
        this.enemyAI = null;
        this.bossFight = false;
        this.turnManager.resetForCombat();
    }

    public boolean isActive() {
        return currentEnemy != null && currentEnemy.isAlive();
    }

    public Enemy currentEnemy() {
        return currentEnemy;
    }

    public boolean isBossFight() {
        return bossFight;
    }

    public boolean isDefenseActive() {
        return turnManager.isDefenseActive();
    }

    public int poisonTurns() {
        return turnManager.getPoisonTurns();
    }

    public int poisonDamage() {
        return turnManager.getPoisonDamage();
    }

    public void restoreTurnState(boolean defenseActive, int poisonTurns, int poisonDamage) {
        turnManager.restoreState(defenseActive, poisonTurns, poisonDamage);
    }

    public void restoreActiveEnemy(Enemy enemy, boolean bossFight) {
        if (enemy == null || !enemy.isAlive()) {
            finish();
            return;
        }
        this.currentEnemy = enemy;
        this.enemyAI = system.createEnemyAI(enemy, bossFight);
        this.bossFight = bossFight;
    }

    public CombatResult attack(String targetId, String themeKey) {
        CombatResult result = new CombatResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (targetId != null && !targetId.isBlank() && !"current".equalsIgnoreCase(targetId)) {
            throw new DomainRuleViolationException("Objetivo de ataque invalido: " + targetId + ".");
        }

        if (!startPlayerTurn(result)) {
            return result;
        }

        result.playerDamage = system.playerAttack(player, currentEnemy, invoker);
        result.actionExecuted = true;

        if (!currentEnemy.isAlive()) {
            resolver.resolveVictory(player, currentEnemy, result);
            return result;
        }

        resolveEnemyTurn(themeKey, result);
        resolver.resolveDefeat(player, result);
        return result;
    }

    public CombatResult defend(String themeKey) {
        CombatResult result = new CombatResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            return result;
        }

        system.playerDefend(player, turnManager, invoker);
        result.actionExecuted = true;

        resolveEnemyTurn(themeKey, result);
        resolver.resolveDefeat(player, result);
        return result;
    }

    public CombatResult useSkill(String requestedSkillName, String themeKey) {
        CombatResult result = new CombatResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            return result;
        }

        String skillName = requestedSkillName == null || requestedSkillName.isBlank()
            ? "Golpe Especial"
            : requestedSkillName;

        result.playerDamage = system.playerSkill(player, currentEnemy, skillName, invoker);
        result.skillName = skillName;
        result.actionExecuted = true;

        if (!currentEnemy.isAlive()) {
            resolver.resolveVictory(player, currentEnemy, result);
            return result;
        }

        resolveEnemyTurn(themeKey, result);
        resolver.resolveDefeat(player, result);
        return result;
    }

    public CombatResult useItem(Item item, String themeKey) {
        CombatResult result = new CombatResult();
        if (item == null) {
            throw new DomainRuleViolationException("No hay item para usar.");
        }

        if (isActive() && !startPlayerTurn(result)) {
            return result;
        }

        if (item.isPotion()) {
            player.heal(50);
            result.potionUsed = true;
            result.actionExecuted = true;
        } else if (item.isAntidote()) {
            if (turnManager.hasPoison()) {
                turnManager.clearPoison();
                result.antidoteUsed = true;
                result.actionExecuted = true;
            } else {
                result.warning = "Usaste un antidoto, pero no estabas envenenado.";
                result.actionExecuted = true;
            }
        } else {
            throw new DomainRuleViolationException("El objeto " + item.getName() + " no tiene uso activo inmediato.");
        }

        if (isActive()) {
            resolveEnemyTurn(themeKey, result);
            resolver.resolveDefeat(player, result);
        }

        return result;
    }

    public CombatResult retreatAttempt(String heroType, String themeKey) {
        CombatResult result = new CombatResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            return result;
        }

        result.actionExecuted = true;
        int chance = computeRetreatChance(heroType);
        if (random.nextInt(100) < chance) {
            result.retreatSuccessful = true;
            finish();
            return result;
        }

        resolveEnemyTurn(themeKey, result);
        resolver.resolveDefeat(player, result);
        result.warning = "No pudiste escapar.";
        return result;
    }

    public void resolveTurn() {
        // Punto de extension para secuencias multi-actor; por ahora, cada accion resuelve su propio turno.
    }

    private boolean startPlayerTurn(CombatResult result) {
        TurnManager.PoisonTick poisonTick = turnManager.tickPoison();
        if (poisonTick.active()) {
            player.receiveDamage(poisonTick.damage());
            if (!player.isAlive()) {
                result.playerDefeated = true;
                return false;
            }
        }
        return true;
    }

    private void resolveEnemyTurn(String themeKey, CombatResult result) {
        CombatSystem.EnemyTurnOutcome enemyTurn = system.enemyTurn(
            player,
            currentEnemy,
            enemyAI,
            turnManager,
            themeKey,
            random,
            invoker
        );

        result.enemyDamage = enemyTurn.finalDamage;
        result.mitigatedDamage = enemyTurn.mitigatedDamage;
        result.poisonApplied = enemyTurn.poisonApplied;
    }

    private int computeRetreatChance(String heroType) {
        String normalized = heroType == null ? "" : heroType.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "arquero" -> 80;
            case "mago" -> 70;
            default -> 60;
        };
    }
}
