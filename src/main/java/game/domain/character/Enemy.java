package game.domain.character;

import game.domain.personaje.Personaje;
import game.domain.personaje.Dragon;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Orco;

/**
 * Entidad de dominio para enemigo activo en combate.
 */
public class Enemy {

    private final Personaje character;
    private final int attackStat;
    private final int defenseStat;
    private final int speedStat;

    public Enemy(Personaje character) {
        this(
            character,
            inferAttack(character),
            inferDefense(character),
            inferSpeed(character)
        );
    }

    public Enemy(Personaje character, int attackStat, int defenseStat, int speedStat) {
        this.character = character;
        this.attackStat = Math.max(1, attackStat);
        this.defenseStat = Math.max(0, defenseStat);
        this.speedStat = Math.max(1, speedStat);
    }

    public Personaje character() {
        return character;
    }

    public String name() {
        return character.getNombre();
    }

    public int hp() {
        return character.getVida();
    }

    public int maxHp() {
        return character.getVidaMaxima();
    }

    public boolean isAlive() {
        return character.estaVivo();
    }

    public int level() {
        return character.getNivel();
    }

    public int attackStat() {
        return attackStat;
    }

    public int defenseStat() {
        return defenseStat;
    }

    public int speedStat() {
        return speedStat;
    }

    public void setExperienceReward(int xp) {
        character.setExperienciaOtorgada(xp);
    }

    public int getExperienceReward() {
        return character.getExperienciaOtorgada();
    }

    public void receiveDamage(int damage) {
        character.recibirDanio(damage);
    }

    public void restoreHp(int hp) {
        int targetHp = Math.max(0, Math.min(hp, character.getVidaMaxima()));
        int hpDelta = targetHp - character.getVida();
        if (hpDelta >= 0) {
            character.curar(hpDelta);
        } else {
            character.recibirDanio(-hpDelta);
        }
    }

    private static int inferAttack(Personaje character) {
        if (character instanceof EnemigoBasico basic) {
            return basic.getAtaqueBase();
        }
        if (character instanceof Orco orc) {
            return orc.getFuerza();
        }
        if (character instanceof Dragon dragon) {
            return dragon.getFuegoDragon();
        }
        return Math.max(6, character.getVidaMaxima() / 12);
    }

    private static int inferDefense(Personaje character) {
        int attack = inferAttack(character);
        if (character instanceof Dragon) {
            return Math.max(12, attack + 8);
        }
        if (character instanceof Orco) {
            return Math.max(10, attack + 6);
        }
        if (character instanceof EnemigoBasico) {
            return Math.max(6, attack + 3);
        }
        return Math.max(8, attack + 4);
    }

    private static int inferSpeed(Personaje character) {
        if (character instanceof Dragon) {
            return 16;
        }
        if (character instanceof Orco) {
            return 12;
        }
        if (character instanceof EnemigoBasico) {
            return 18;
        }
        return 14;
    }
}
