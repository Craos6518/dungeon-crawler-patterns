package game.dungeon.model;

/**
 * Representa una sala dentro de una mazmorra
 */
public class Room {
    private final String nombre;
    private final String descripcion;
    private final int dificultad;
    private final boolean tieneTesoro;
    private final boolean tieneEnemigo;

    public Room(String nombre, String descripcion, int dificultad, 
                boolean tieneTesoro, boolean tieneEnemigo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.dificultad = dificultad;
        this.tieneTesoro = tieneTesoro;
        this.tieneEnemigo = tieneEnemigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDificultad() {
        return dificultad;
    }

    public boolean tieneTesoro() {
        return tieneTesoro;
    }

    public boolean tieneEnemigo() {
        return tieneEnemigo;
    }

    @Override
    public String toString() {
        return String.format("Sala: %s - %s (Dificultad: %d)", 
            nombre, descripcion, dificultad);
    }
}
