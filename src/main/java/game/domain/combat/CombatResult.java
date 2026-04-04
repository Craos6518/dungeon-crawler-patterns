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
    public boolean antidoteUsed;
    public boolean potionUsed;

    public String warning;
    public String skillName;
}
