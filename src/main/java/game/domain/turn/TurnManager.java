package game.domain.turn;

/**
 * Coordina estado de turnos y efectos transversales del jugador durante combate.
 */
public class TurnManager {

    private boolean defenseActive;
    private int poisonTurns;
    private int poisonDamage;

    public TurnManager() {
        resetForCombat();
    }

    public void resetForCombat() {
        defenseActive = false;
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

    public PoisonTick tickPoison() {
        if (poisonTurns <= 0 || poisonDamage <= 0) {
            return PoisonTick.none();
        }

        poisonTurns--;
        return new PoisonTick(poisonDamage, poisonTurns);
    }

    public boolean hasPoison() {
        return poisonTurns > 0 && poisonDamage > 0;
    }

    public void clearPoison() {
        poisonTurns = 0;
        poisonDamage = 0;
    }

    public boolean isDefenseActive() {
        return defenseActive;
    }

    public void restoreState(boolean defenseActive, int poisonTurns, int poisonDamage) {
        this.defenseActive = defenseActive;
        this.poisonTurns = Math.max(0, poisonTurns);
        this.poisonDamage = Math.max(0, poisonDamage);
    }

    public int getPoisonTurns() {
        return poisonTurns;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }

    public record PoisonTick(int damage, int remainingTurns) {
        public static PoisonTick none() {
            return new PoisonTick(0, 0);
        }

        public boolean active() {
            return damage > 0;
        }
    }
}
