package game.domain.character;

import game.command.actions.LevelUpCommand;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.inventory.Inventory;

/**
 * Agregado Player. Incluye personaje e inventario.
 */
public class Player {

    private final Personaje character;
    private final Inventory inventory;
    private int gold;
    private int defeatedEnemies;

    public Player(Personaje character, Inventory inventory) {
        this.character = character;
        this.inventory = inventory;
        this.gold = 0;
        this.defeatedEnemies = 0;
    }

    public static Player demo() {
        Personaje hero = new GuerreroFactory(150, 25).crearPersonaje("Aventurero");
        return new Player(hero, Inventory.demo());
    }

    public Personaje character() {
        return character;
    }

    public Inventory inventory() {
        return inventory;
    }

    public String name() {
        return character.getNombre();
    }

    public boolean isAlive() {
        return character.estaVivo();
    }

    public int hp() {
        return character.getVida();
    }

    public int maxHp() {
        return character.getVidaMaxima();
    }

    public int level() {
        return character.getNivel();
    }

    public int experience() {
        return character.getExperiencia();
    }

    public int gold() {
        return gold;
    }

    public int defeatedEnemies() {
        return defeatedEnemies;
    }

    public int attack(Enemy enemy) {
        return character.atacar(enemy.character()).danio();
    }

    public void heal(int amount) {
        character.curar(amount);
    }

    public void receiveDamage(int amount) {
        character.recibirDanio(amount);
    }

    public int gainExperience(int xp) {
        if (xp <= 0) {
            return 0;
        }

        int hpBeforeLevelUp = character.getVida();
        LevelUpCommand command = new LevelUpCommand(character, xp);
        command.execute();

        int targetHp = Math.max(0, Math.min(hpBeforeLevelUp, character.getVidaMaxima()));
        int hpDelta = targetHp - character.getVida();
        if (hpDelta >= 0) {
            character.curar(hpDelta);
        } else {
            character.recibirDanio(-hpDelta);
        }

        return command.getNivelesGanados();
    }

    public void addGold(int amount) {
        gold += Math.max(0, amount);
    }

    public void registerDefeatedEnemy() {
        defeatedEnemies++;
    }

    public void restoreProgress(int level, int experience, int hp, int gold, int defeatedEnemies) {
        int targetLevel = Math.max(1, level);
        while (character.getNivel() < targetLevel) {
            character.subirNivel();
        }

        int xpDelta = experience - character.getExperiencia();
        character.ganarExperiencia(xpDelta);

        int targetHp = Math.max(0, Math.min(hp, character.getVidaMaxima()));
        int hpDelta = targetHp - character.getVida();
        if (hpDelta >= 0) {
            character.curar(hpDelta);
        } else {
            character.recibirDanio(-hpDelta);
        }

        this.gold = Math.max(0, gold);
        this.defeatedEnemies = Math.max(0, defeatedEnemies);
    }
}
