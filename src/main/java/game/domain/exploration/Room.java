package game.domain.exploration;

/**
 * Value object de sala para exponer datos de exploracion.
 */
public class Room {

    private final game.dungeon.model.Room model;

    public Room(game.dungeon.model.Room model) {
        this.model = model;
    }

    public game.dungeon.model.Room model() {
        return model;
    }

    public String name() {
        return model.getNombre();
    }

    public String description() {
        return model.getDescripcion();
    }

    public int difficulty() {
        return model.getDificultad();
    }

    public boolean hasTreasure() {
        return model.tieneTesoro();
    }

    public boolean hasEnemy() {
        return model.tieneEnemigo();
    }
}
