package game.domain.combat;

import game.ai.strategy.AIController;
import game.patterns.command.actions.CommandInvoker;
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

    private static final int MAX_BUFF_STACKS = 3;

    private final Player player;
    private final TurnManager turnManager;
    private final CombatSystem system;
    private final CombatStatusDecoratorPipeline decoratorPipeline;
    private final CombatResolver resolver;
    private final CommandInvoker invoker;
    private final Random random;

    private Enemy currentEnemy;
    private AIController enemyAI;
    private boolean bossFight;
    private PlayerCombatStyle playerStyle;
    private int offensiveBuffStacks;
    private int guardBuffStacks;
    private TacticalCheckpoint tacticalCheckpoint;
    private boolean tacticalCheckpointConsumed;

    public Combat(Player player, TurnManager turnManager, Random random) {
        this.player = player;
        this.turnManager = turnManager;
        this.system = new CombatSystem();
        this.decoratorPipeline = new CombatStatusDecoratorPipeline();
        this.resolver = new CombatResolver();
        this.invoker = new CommandInvoker();
        this.random = random;
        this.playerStyle = PlayerCombatStyle.BALANCED;
        this.offensiveBuffStacks = 0;
        this.guardBuffStacks = 0;
        this.tacticalCheckpoint = null;
        this.tacticalCheckpointConsumed = false;
    }

    public void start(Enemy enemy, boolean bossFight) {
        this.currentEnemy = enemy;
        this.enemyAI = system.createEnemyAI(enemy, bossFight);
        this.bossFight = bossFight;
        this.playerStyle = PlayerCombatStyle.BALANCED;
        this.offensiveBuffStacks = 0;
        this.guardBuffStacks = 0;
        this.tacticalCheckpoint = null;
        this.tacticalCheckpointConsumed = false;
        this.turnManager.resetForCombat();
    }

    public void finish() {
        this.currentEnemy = null;
        this.enemyAI = null;
        this.bossFight = false;
        this.playerStyle = PlayerCombatStyle.BALANCED;
        this.offensiveBuffStacks = 0;
        this.guardBuffStacks = 0;
        this.tacticalCheckpoint = null;
        this.tacticalCheckpointConsumed = false;
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

    public PlayerCombatStyle playerStyle() {
        return playerStyle;
    }

    public int offensiveBuffStacks() {
        return offensiveBuffStacks;
    }

    public int guardBuffStacks() {
        return guardBuffStacks;
    }

    public boolean hasTacticalCheckpoint() {
        return tacticalCheckpoint != null;
    }

    public boolean tacticalCheckpointConsumed() {
        return tacticalCheckpointConsumed;
    }

    public TacticalCheckpoint tacticalCheckpoint() {
        return tacticalCheckpoint;
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

    public void restoreTacticalState(
        String styleKey,
        int offensiveStacks,
        int guardStacks,
        TacticalCheckpoint checkpoint,
        boolean checkpointConsumed
    ) {
        this.playerStyle = PlayerCombatStyle.fromRaw(styleKey);
        this.offensiveBuffStacks = clampStacks(offensiveStacks);
        this.guardBuffStacks = clampStacks(guardStacks);
        this.tacticalCheckpoint = checkpoint;
        this.tacticalCheckpointConsumed = checkpoint != null && checkpointConsumed;
    }

    public CombatResult attack(String targetId, String themeKey) {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (targetId != null && !targetId.isBlank() && !"current".equalsIgnoreCase(targetId)) {
            throw new DomainRuleViolationException("Objetivo de ataque invalido: " + targetId + ".");
        }

        if (!startPlayerTurn(result)) {
            captureResourceAfter(result);
            return result;
        }

        boolean enemyOpenedTurn = resolveEnemyOpeningTurn(themeKey, result);
        if (!player.isAlive()) {
            resolver.resolveDefeat(player, result);
            captureResourceAfter(result);
            return result;
        }

        int actionCount = system.playerActionCount(player, currentEnemy);
        int totalDamage = 0;
        for (int i = 0; i < actionCount && currentEnemy.isAlive(); i++) {
            int baseDamage = system.playerAttack(player, currentEnemy, invoker);
            int adjustedDamage = applyOutgoingModifiers(baseDamage);
            if (adjustedDamage > baseDamage) {
                currentEnemy.receiveDamage(adjustedDamage - baseDamage);
            }
            totalDamage += adjustedDamage;
        }
        result.playerDamage = totalDamage;

        consumeOffensiveBuffStack();
        player.recoverResource(player.attackResourceRecovery());
        result.actionExecuted = true;
        captureResourceAfter(result);

        if (!currentEnemy.isAlive()) {
            resolver.resolveVictory(player, currentEnemy, result);
            return result;
        }

        resolveEnemyResponses(themeKey, result, enemyOpenedTurn);
        resolver.resolveDefeat(player, result);
        captureResourceAfter(result);
        return result;
    }

    public CombatResult defend(String themeKey) {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            captureResourceAfter(result);
            return result;
        }

        boolean enemyOpenedTurn = resolveEnemyOpeningTurn(themeKey, result);
        if (!player.isAlive()) {
            resolver.resolveDefeat(player, result);
            captureResourceAfter(result);
            return result;
        }

        system.playerDefend(player, turnManager, invoker);
        player.recoverResource(player.defendResourceRecovery());
        result.actionExecuted = true;
        captureResourceAfter(result);

        resolveEnemyResponses(themeKey, result, enemyOpenedTurn);
        resolver.resolveDefeat(player, result);
        captureResourceAfter(result);
        return result;
    }

    public CombatResult useSkill(String requestedSkillName, String themeKey) {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            captureResourceAfter(result);
            return result;
        }

        int skillCost = adjustedCost(player.skillCost());
        if (!player.spendResource(skillCost)) {
            result.warning = "No tienes suficiente " + player.resourceType() + " para usar tu habilidad.";
            captureResourceAfter(result);
            return result;
        }

        boolean enemyOpenedTurn = resolveEnemyOpeningTurn(themeKey, result);
        if (!player.isAlive()) {
            resolver.resolveDefeat(player, result);
            captureResourceAfter(result);
            return result;
        }

        String skillName = requestedSkillName == null || requestedSkillName.isBlank()
            ? defaultSkillName()
            : requestedSkillName;

        int totalDamage = 0;
        int baseSkillDamage = system.playerSkill(player, currentEnemy, skillName, invoker);
        int adjustedSkillDamage = applyOutgoingModifiers(baseSkillDamage);
        if (adjustedSkillDamage > baseSkillDamage) {
            currentEnemy.receiveDamage(adjustedSkillDamage - baseSkillDamage);
        }
        totalDamage += adjustedSkillDamage;

        int actionCount = system.playerActionCount(player, currentEnemy);
        for (int i = 1; i < actionCount && currentEnemy.isAlive(); i++) {
            int baseFollowUp = system.playerAttack(player, currentEnemy, invoker);
            int adjustedFollowUp = applyOutgoingModifiers(baseFollowUp);
            if (adjustedFollowUp > baseFollowUp) {
                currentEnemy.receiveDamage(adjustedFollowUp - baseFollowUp);
            }
            totalDamage += adjustedFollowUp;
        }

        result.playerDamage = totalDamage;

        consumeOffensiveBuffStack();
        result.skillName = skillName;
        result.actionExecuted = true;
        captureResourceAfter(result);

        if (!currentEnemy.isAlive()) {
            resolver.resolveVictory(player, currentEnemy, result);
            return result;
        }

        resolveEnemyResponses(themeKey, result, enemyOpenedTurn);
        resolver.resolveDefeat(player, result);
        captureResourceAfter(result);
        return result;
    }

    public CombatResult useItem(Item item, String themeKey) {
        CombatResult result = createResult();
        if (item == null) {
            throw new DomainRuleViolationException("No hay item para usar.");
        }

        boolean enemyOpenedTurn = false;
        if (isActive() && !startPlayerTurn(result)) {
            captureResourceAfter(result);
            return result;
        }

        if (isActive()) {
            enemyOpenedTurn = resolveEnemyOpeningTurn(themeKey, result);
            if (!player.isAlive()) {
                resolver.resolveDefeat(player, result);
                captureResourceAfter(result);
                return result;
            }
        }

        if (item.isPotion()) {
            int hpBefore = player.hp();
            int scaledHeal = Math.max(50, (int) Math.round(player.maxHp() * 0.45));
            player.heal(scaledHeal);
            result.healedHp = Math.max(0, player.hp() - hpBefore);
            player.recoverResource(Math.max(4, player.attackResourceRecovery() / 2));
            result.potionUsed = true;
            result.actionExecuted = true;
            if (result.healedHp == 0) {
                result.warning = "La pocion no tuvo efecto: ya estabas con la vida al maximo.";
            }
        } else if (item.isAntidote()) {
            if (turnManager.hasPoison()) {
                turnManager.clearPoison();
                player.recoverResource(Math.max(3, player.defendResourceRecovery() / 3));
                result.antidoteUsed = true;
                result.actionExecuted = true;
            } else {
                result.warning = "Usaste un antidoto, pero no estabas envenenado.";
                result.actionExecuted = true;
            }
        } else {
            throw new DomainRuleViolationException("El objeto " + item.getName() + " no tiene uso activo inmediato.");
        }

        captureResourceAfter(result);

        if (isActive()) {
            resolveEnemyResponses(themeKey, result, enemyOpenedTurn);
            resolver.resolveDefeat(player, result);
            captureResourceAfter(result);
        }

        return result;
    }

    public CombatResult retreatAttempt(String heroType, String themeKey) {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            captureResourceAfter(result);
            return result;
        }

        boolean enemyOpenedTurn = resolveEnemyOpeningTurn(themeKey, result);
        if (!player.isAlive()) {
            resolver.resolveDefeat(player, result);
            captureResourceAfter(result);
            return result;
        }

        result.actionExecuted = true;
        int chance = computeRetreatChance(heroType);
        if (random.nextInt(100) < chance) {
            result.retreatSuccessful = true;
            captureResourceAfter(result);
            finish();
            return result;
        }

        resolveEnemyResponses(themeKey, result, enemyOpenedTurn);
        resolver.resolveDefeat(player, result);
        result.warning = "No pudiste escapar.";
        captureResourceAfter(result);
        return result;
    }

    public CombatResult setCombatStyle(String requestedStyle, String themeKey) {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            captureResourceAfter(result);
            return result;
        }

        String normalized = normalizeStyleKey(requestedStyle);
        if (!isSupportedStyle(normalized)) {
            result.warning = "Estilo invalido. Usa: balanced, aggressive o defensive.";
            captureResourceAfter(result);
            return result;
        }

        PlayerCombatStyle requested = PlayerCombatStyle.fromRaw(normalized);
        if (requested == playerStyle) {
            result.warning = "Ya estabas en estilo " + requested.displayName() + ".";
            captureResourceAfter(result);
            return result;
        }

        boolean enemyOpenedTurn = resolveEnemyOpeningTurn(themeKey, result);
        if (!player.isAlive()) {
            resolver.resolveDefeat(player, result);
            captureResourceAfter(result);
            return result;
        }

        playerStyle = requested;
        result.actionExecuted = true;
        result.styleChanged = true;
        result.styleName = requested.displayName();
        captureResourceAfter(result);

        resolveEnemyResponses(themeKey, result, enemyOpenedTurn);
        resolver.resolveDefeat(player, result);
        captureResourceAfter(result);
        return result;
    }

    public CombatResult applyStackingBuff(String requestedBuffType, String themeKey) {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (!startPlayerTurn(result)) {
            captureResourceAfter(result);
            return result;
        }

        String buffType = normalizeBuffType(requestedBuffType);
        if ("power".equals(buffType) && offensiveBuffStacks >= MAX_BUFF_STACKS) {
            result.warning = "El buff ofensivo ya esta al maximo.";
            captureResourceAfter(result);
            return result;
        }
        if ("guard".equals(buffType) && guardBuffStacks >= MAX_BUFF_STACKS) {
            result.warning = "El buff defensivo ya esta al maximo.";
            captureResourceAfter(result);
            return result;
        }

        int cost = adjustedCost(player.buffCost());
        if (!player.spendResource(cost)) {
            result.warning = "No tienes suficiente " + player.resourceType() + " para potenciarte.";
            captureResourceAfter(result);
            return result;
        }

        boolean enemyOpenedTurn = resolveEnemyOpeningTurn(themeKey, result);
        if (!player.isAlive()) {
            resolver.resolveDefeat(player, result);
            captureResourceAfter(result);
            return result;
        }

        if ("guard".equals(buffType)) {
            guardBuffStacks++;
            result.buffType = "guardia";
            result.buffStacks = guardBuffStacks;
        } else {
            offensiveBuffStacks++;
            result.buffType = "poder";
            result.buffStacks = offensiveBuffStacks;
        }

        result.buffApplied = true;
        result.actionExecuted = true;
        captureResourceAfter(result);

        resolveEnemyResponses(themeKey, result, enemyOpenedTurn);
        resolver.resolveDefeat(player, result);
        captureResourceAfter(result);
        return result;
    }

    public CombatResult saveTacticalCheckpoint() {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        int cost = adjustedCost(player.checkpointCost());
        if (!player.spendResource(cost)) {
            result.warning = "No tienes suficiente " + player.resourceType() + " para fijar un checkpoint.";
            captureResourceAfter(result);
            return result;
        }

        tacticalCheckpoint = snapshotCurrentState();
        tacticalCheckpointConsumed = false;

        result.actionExecuted = true;
        result.checkpointSaved = true;
        captureResourceAfter(result);
        return result;
    }

    public CombatResult rollbackTacticalCheckpoint() {
        CombatResult result = createResult();
        if (!isActive()) {
            throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
        }

        if (tacticalCheckpoint == null) {
            result.warning = "No hay checkpoint tactico guardado.";
            captureResourceAfter(result);
            return result;
        }

        if (tacticalCheckpointConsumed) {
            result.warning = "El checkpoint tactico ya fue consumido en este combate.";
            captureResourceAfter(result);
            return result;
        }

        restoreFromCheckpoint(tacticalCheckpoint);
        tacticalCheckpointConsumed = true;

        result.actionExecuted = true;
        result.checkpointRolledBack = true;
        captureResourceAfter(result);
        return result;
    }

    public void resolveTurn() {
        // Punto de extension para secuencias multi-actor; por ahora, cada accion resuelve su propio turno.
    }

    private boolean startPlayerTurn(CombatResult result) {
        TurnManager.PoisonTick poisonTick = decoratorPipeline.applyPoisonTick(player, turnManager);
        if (poisonTick.active()) {
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

        applyIncomingMitigation(enemyTurn);
        consumeGuardBuffStack(enemyTurn.rawDamage > 0 || enemyTurn.enemyDefended);

        if (enemyTurn.rawDamage > 0 && player.isAlive()) {
            player.recoverResource(passiveRecoveryByStyle());
        }

        result.enemyDamage += enemyTurn.finalDamage;
        result.mitigatedDamage += enemyTurn.mitigatedDamage;
        result.poisonApplied = result.poisonApplied || enemyTurn.poisonApplied;
    }

    private int computeRetreatChance(String heroType) {
        String normalized = heroType == null ? "" : heroType.trim().toLowerCase(Locale.ROOT);
        int baseChance = switch (normalized) {
            case "arquero" -> 80;
            case "mago" -> 70;
            default -> 60;
        };

        if (currentEnemy != null) {
            int speedDelta = player.speedStat() - currentEnemy.speedStat();
            baseChance += Math.max(-15, Math.min(15, speedDelta * 2));
        }

        if (playerStyle == PlayerCombatStyle.DEFENSIVE) {
            baseChance += 5;
        } else if (playerStyle == PlayerCombatStyle.AGGRESSIVE) {
            baseChance -= 5;
        }

        baseChance += guardBuffStacks * 2;
        return Math.max(25, Math.min(90, baseChance));
    }

    private boolean resolveEnemyOpeningTurn(String themeKey, CombatResult result) {
        if (!isActive() || !player.isAlive() || !system.enemyActsFirst(player, currentEnemy)) {
            return false;
        }

        resolveEnemyActions(themeKey, result, 1);
        return true;
    }

    private void resolveEnemyResponses(String themeKey, CombatResult result, boolean enemyOpenedTurn) {
        if (!isActive() || !player.isAlive()) {
            return;
        }

        int totalEnemyActions = system.enemyActionCount(player, currentEnemy);
        int pendingEnemyActions = Math.max(0, totalEnemyActions - (enemyOpenedTurn ? 1 : 0));
        resolveEnemyActions(themeKey, result, pendingEnemyActions);
    }

    private void resolveEnemyActions(String themeKey, CombatResult result, int actionCount) {
        for (int i = 0; i < actionCount && isActive() && player.isAlive(); i++) {
            resolveEnemyTurn(themeKey, result);
        }
    }

    private CombatResult createResult() {
        CombatResult result = new CombatResult();
        result.resourceType = player.resourceType();
        result.resourceBefore = player.resource();
        result.resourceAfter = player.resource();
        return result;
    }

    private void captureResourceAfter(CombatResult result) {
        result.resourceAfter = player.resource();
    }

    private int applyOutgoingModifiers(int baseDamage) {
        double buffMultiplier = decoratorPipeline.resolveOffensiveMultiplier(player, offensiveBuffStacks);
        double styleMultiplier = playerStyle.outgoingDamageMultiplier();
        int adjusted = (int) Math.round(baseDamage * buffMultiplier * styleMultiplier);
        return Math.max(1, adjusted);
    }

    private void applyIncomingMitigation(CombatSystem.EnemyTurnOutcome enemyTurn) {
        if (enemyTurn.finalDamage <= 0 || !player.isAlive()) {
            return;
        }

        double styleMitigationRatio = Math.max(0.0, 1.0 - playerStyle.incomingDamageMultiplier());
        int styleMitigation = (int) Math.round(enemyTurn.finalDamage * styleMitigationRatio);
        int guardMitigation = decoratorPipeline.resolveGuardMitigation(player, enemyTurn.finalDamage, guardBuffStacks);
        int totalMitigation = Math.min(enemyTurn.finalDamage, Math.max(0, styleMitigation + guardMitigation));
        if (totalMitigation <= 0) {
            return;
        }

        player.heal(totalMitigation);
        enemyTurn.finalDamage = Math.max(0, enemyTurn.finalDamage - totalMitigation);
        enemyTurn.mitigatedDamage += totalMitigation;
    }

    private int adjustedCost(int baseCost) {
        int adjusted = (int) Math.round(baseCost * playerStyle.resourceCostMultiplier());
        return Math.max(1, adjusted);
    }

    private int passiveRecoveryByStyle() {
        return switch (playerStyle) {
            case AGGRESSIVE -> 1;
            case DEFENSIVE -> 4;
            default -> 2;
        };
    }

    private void consumeOffensiveBuffStack() {
        if (offensiveBuffStacks > 0) {
            offensiveBuffStacks--;
        }
    }

    private void consumeGuardBuffStack(boolean shouldConsume) {
        if (shouldConsume && guardBuffStacks > 0) {
            guardBuffStacks--;
        }
    }

    private TacticalCheckpoint snapshotCurrentState() {
        return new TacticalCheckpoint(
            player.hp(),
            currentEnemy == null ? 0 : currentEnemy.hp(),
            player.resource(),
            turnManager.isDefenseActive(),
            turnManager.getPoisonTurns(),
            turnManager.getPoisonDamage(),
            playerStyle,
            offensiveBuffStacks,
            guardBuffStacks
        );
    }

    private void restoreFromCheckpoint(TacticalCheckpoint checkpoint) {
        player.restoreCombatState(checkpoint.playerHp(), checkpoint.playerResource());
        if (currentEnemy != null) {
            currentEnemy.restoreHp(checkpoint.enemyHp());
        }

        turnManager.restoreState(
            checkpoint.defenseActive(),
            checkpoint.poisonTurns(),
            checkpoint.poisonDamage()
        );

        playerStyle = checkpoint.playerStyle();
        offensiveBuffStacks = clampStacks(checkpoint.offensiveBuffStacks());
        guardBuffStacks = clampStacks(checkpoint.guardBuffStacks());
    }

    private static int clampStacks(int stacks) {
        return Math.max(0, Math.min(MAX_BUFF_STACKS, stacks));
    }

    private String defaultSkillName() {
        return switch (player.heroType()) {
            case "mago" -> "Ruptura Arcana";
            case "arquero" -> "Disparo de Precision";
            default -> "Embate de Acero";
        };
    }

    private static String normalizeBuffType(String raw) {
        if (raw == null) {
            return "power";
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if ("guard".equals(normalized) || "defense".equals(normalized)) {
            return "guard";
        }
        return "power";
    }

    private static String normalizeStyleKey(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isSupportedStyle(String normalized) {
        return "balanced".equals(normalized) || "aggressive".equals(normalized) || "defensive".equals(normalized);
    }

    public record TacticalCheckpoint(
        int playerHp,
        int enemyHp,
        int playerResource,
        boolean defenseActive,
        int poisonTurns,
        int poisonDamage,
        PlayerCombatStyle playerStyle,
        int offensiveBuffStacks,
        int guardBuffStacks
    ) {
    }
}
