package game.domain.character;

import game.balance.GameBalance;
import game.patterns.command.actions.LevelUpCommand;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.inventory.Inventory;

import java.util.Locale;

/**
 * Agregado Player. Incluye personaje e inventario.
 */
public class Player {

    private final Personaje character;
    private final Inventory inventory;
    private final String heroType;
    private final String resourceType;
    private final int baseAttack;
    private final int baseDefense;
    private final int baseSpeed;
    private final int maxResource;
    private final int attackResourceRecovery;
    private final int defendResourceRecovery;
    private final int skillCost;
    private final int buffCost;
    private final int checkpointCost;
    private final int styleChangeCost;

    private int resource;
    private int gold;
    private int defeatedEnemies;

    public Player(Personaje character, Inventory inventory) {
        this(character, inventory, "guerrero");
    }

    public Player(Personaje character, Inventory inventory, String heroType) {
        this.character = character;
        this.inventory = inventory;
        this.heroType = normalizeHeroType(heroType);

        GameBalance.HeroProfile heroProfile = GameBalance.hero(this.heroType);
        this.baseAttack = heroProfile.attack();
        this.baseDefense = heroProfile.defense();
        this.baseSpeed = heroProfile.speed();

        GameBalance.ResourceProfile resourceProfile = GameBalance.resource(this.heroType);
        this.resourceType = resourceProfile.resourceName();
        this.maxResource = resourceProfile.max();
        this.resource = this.maxResource;
        this.attackResourceRecovery = resourceProfile.attackRecovery();
        this.defendResourceRecovery = resourceProfile.defendRecovery();
        this.skillCost = resourceProfile.skillCost();
        this.buffCost = resourceProfile.buffCost();
        this.checkpointCost = resourceProfile.checkpointCost();
        this.styleChangeCost = resourceProfile.styleChangeCost();

        this.gold = 0;
        this.defeatedEnemies = 0;
    }

    public static Player demo() {
        GameBalance.HeroProfile profile = GameBalance.hero("guerrero");
        Personaje hero = new GuerreroFactory(profile.hp(), profile.attack()).crearPersonaje("Aventurero");
        return new Player(hero, Inventory.demo(), "guerrero");
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

    public void rename(String newName) {
        character.renombrar(newName);
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

    public boolean hasGold(int amount) {
        return gold >= Math.max(0, amount);
    }

    public String heroType() {
        return heroType;
    }

    public String resourceType() {
        return resourceType;
    }

    public int resource() {
        return resource;
    }

    public int maxResource() {
        return maxResource;
    }

    public int attackResourceRecovery() {
        return attackResourceRecovery;
    }

    public int defendResourceRecovery() {
        return defendResourceRecovery;
    }

    public int skillCost() {
        return skillCost;
    }

    public int buffCost() {
        return buffCost;
    }

    public int checkpointCost() {
        return checkpointCost;
    }

    public int styleChangeCost() {
        return styleChangeCost;
    }

    public int attackStat() {
        int levelScaling = Math.max(0, level() - 1) * 2;
        return baseAttack + levelScaling;
    }

    public int defenseStat() {
        int levelScaling = Math.max(0, level() - 1) * 2;
        return baseDefense + levelScaling;
    }

    public int speedStat() {
        int levelScaling = Math.max(0, level() - 1);
        return baseSpeed + levelScaling;
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

    public boolean hasResourceFor(int amount) {
        int cost = Math.max(0, amount);
        return resource >= cost;
    }

    public boolean spendResource(int amount) {
        int cost = Math.max(0, amount);
        if (resource < cost) {
            return false;
        }
        resource -= cost;
        return true;
    }

    public void recoverResource(int amount) {
        int gain = Math.max(0, amount);
        resource = Math.min(maxResource, resource + gain);
    }

    public void restoreCombatState(int hp, int resource) {
        int targetHp = Math.max(0, Math.min(hp, character.getVidaMaxima()));
        int hpDelta = targetHp - character.getVida();
        if (hpDelta >= 0) {
            character.curar(hpDelta);
        } else {
            character.recibirDanio(-hpDelta);
        }

        this.resource = Math.max(0, Math.min(resource, maxResource));
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

    public boolean spendGold(int amount) {
        int cost = Math.max(0, amount);
        if (gold < cost) {
            return false;
        }
        gold -= cost;
        return true;
    }

    public void registerDefeatedEnemy() {
        defeatedEnemies++;
    }

    public void restoreProgress(int level, int experience, int hp, int gold, int defeatedEnemies) {
        int calculatedMaxHp = character.getVidaMaxima(); // Fallback si no podemos calcular
        // Si el nivel es diferente al actual, intentamos estimar la vida máxima (+20 por nivel desde nivel 1)
        // Pero lo más robusto es que restoreStats maneje el nivel primero.
        restoreProgress(level, experience, hp, gold, defeatedEnemies, maxResource);
    }

    public void restoreProgress(int level, int experience, int hp, int gold, int defeatedEnemies, int resource) {
        // Al usar firmas antiguas, recalculamos la vida máxima basada en el nivel para evitar degradación de HP
        // Asumiendo +20 de vida por cada nivel superior al 1.
        int baseHp = character.getVidaMaxima() - (Math.max(1, character.getNivel()) - 1) * 20;
        int targetMaxHp = baseHp + (Math.max(1, level) - 1) * 20;
        restoreProgress(level, experience, hp, targetMaxHp, gold, defeatedEnemies, resource);
    }

    public void restoreProgress(int level, int experience, int hp, int maxHp, int gold, int defeatedEnemies, int resource) {
        character.restoreStats(level, experience, hp, maxHp);
        this.gold = Math.max(0, gold);
        this.defeatedEnemies = Math.max(0, defeatedEnemies);
        this.resource = Math.max(0, Math.min(resource, maxResource));
    }


    private static String normalizeHeroType(String heroType) {
        if (heroType == null) {
            return "guerrero";
        }

        String normalized = heroType.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "mago", "arquero" -> normalized;
            default -> "guerrero";
        };
    }
}
