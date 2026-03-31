package game.domain.character;

import game.domain.personaje.Personaje;

/**
 * Entidad de dominio para enemigo activo en combate.
 */
public class Enemy {

    private final Personaje character;

    public Enemy(Personaje character) {
        this.character = character;
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

    public void setExperienceReward(int xp) {
        character.setExperienciaOtorgada(xp);
    }

    public int getExperienceReward() {
        return character.getExperienciaOtorgada();
    }

    public void receiveDamage(int damage) {
        character.recibirDanio(damage);
    }
}
