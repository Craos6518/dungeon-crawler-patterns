package game.domain.combat;

/**
 * Resultado de una accion de combate del jugador.
 */
public class CombatResult {

    public boolean actionExecuted;
    public boolean enemyDefeated;
    public boolean playerDefeated;
    public boolean retreatSuccessful;

    public int playerDamage;
    public int enemyDamage;
    public int mitigatedDamage;

    public int gainedXp;
    public int gainedLevels;

    public boolean poisonApplied;
    public boolean burnApplied;
    public boolean playerStunned;
    public boolean antidoteUsed;
    public boolean potionUsed;
    public int healedHp;

    public boolean styleChanged;
    public String styleName;

    public boolean buffApplied;
    public String buffType;
    public int buffStacks;

    public boolean checkpointSaved;
    public boolean checkpointRolledBack;

    public String warning;
    public String skillName;

    public String resourceType;
    public int resourceBefore;
    public int resourceAfter;
}
