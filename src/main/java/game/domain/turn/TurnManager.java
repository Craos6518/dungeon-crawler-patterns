package game.domain.turn;

/**
 * Coordina estado de turnos y efectos transversales del jugador durante combate.
 */
public class TurnManager {

    private boolean defenseActive;
    private int poisonTurns;
    private int poisonDamage;
    private int burnTurns;
    private int burnDamage;
    private int stunTurns;

    public TurnManager() {
        resetForCombat();
    }

    public void resetForCombat() {
        defenseActive = false;
        // Se mantienen los estados persistentes si el diseño lo requiere, 
        // pero Combat.java los resetea explícitamente si es un nuevo combate.
    }

    public void activateDefense() {
        defenseActive = true;
    }

    public int mitigateIncomingDamage(int incomingDamage) {
        if (!defenseActive) {
            return 0;
        }
        defenseActive = false;
        return Math.max(1, incomingDamage / 2);
    }

    public void applyPoison(int turns, int damage) {
        poisonTurns = Math.max(0, turns);
        poisonDamage = Math.max(0, damage);
    }

    public void applyBurn(int turns, int damage) {
        burnTurns = Math.max(0, turns);
        burnDamage = Math.max(0, damage);
    }

    public void applyStun(int turns) {
        stunTurns = Math.max(0, turns);
    }

    public PoisonTick tickPoison() {
        if (poisonTurns <= 0 || poisonDamage <= 0) {
            return PoisonTick.none();
        }

        poisonTurns--;
        return new PoisonTick(poisonDamage, poisonTurns);
    }

    public BurnTick tickBurn() {
        if (burnTurns <= 0 || burnDamage <= 0) {
            return BurnTick.none();
        }

        burnTurns--;
        return new BurnTick(burnDamage, burnTurns);
    }

    public StunStatus checkStun() {
        if (stunTurns <= 0) {
            return StunStatus.none();
        }

        stunTurns--;
        return new StunStatus(true, stunTurns);
    }

    public boolean hasPoison() {
        return poisonTurns > 0 && poisonDamage > 0;
    }

    public boolean hasBurn() {
        return burnTurns > 0 && burnDamage > 0;
    }

    public boolean hasStun() {
        return stunTurns > 0;
    }

    public void clearPoison() {
        poisonTurns = 0;
        poisonDamage = 0;
    }

    public void clearBurn() {
        burnTurns = 0;
        burnDamage = 0;
    }

    public void clearStun() {
        stunTurns = 0;
    }

    public boolean isDefenseActive() {
        return defenseActive;
    }

    public void restoreState(boolean defenseActive, int poisonTurns, int poisonDamage) {
        this.defenseActive = defenseActive;
        this.poisonTurns = Math.max(0, poisonTurns);
        this.poisonDamage = Math.max(0, poisonDamage);
    }

    public void restoreFullState(boolean defenseActive, int poisonTurns, int poisonDamage, int burnTurns, int burnDamage, int stunTurns) {
        this.defenseActive = defenseActive;
        this.poisonTurns = Math.max(0, poisonTurns);
        this.poisonDamage = Math.max(0, poisonDamage);
        this.burnTurns = Math.max(0, burnTurns);
        this.burnDamage = Math.max(0, burnDamage);
        this.stunTurns = Math.max(0, stunTurns);
    }

    public int getPoisonTurns() {
        return poisonTurns;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }

    public int getBurnTurns() {
        return burnTurns;
    }

    public int getBurnDamage() {
        return burnDamage;
    }

    public int getStunTurns() {
        return stunTurns;
    }

    public record PoisonTick(int damage, int remainingTurns) {
        public static PoisonTick none() {
            return new PoisonTick(0, 0);
        }

        public boolean active() {
            return damage > 0;
        }
    }

    public record BurnTick(int damage, int remainingTurns) {
        public static BurnTick none() {
            return new BurnTick(0, 0);
        }

        public boolean active() {
            return damage > 0;
        }
    }

    public record StunStatus(boolean active, int remainingTurns) {
        public static StunStatus none() {
            return new StunStatus(false, 0);
        }
    }
}
