package game.dungeon.builder;

import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder Pattern - Implementación concreta del builder de mazmorras
 */
public class ConcreteDungeonBuilder implements DungeonBuilder {
    private String nombre;
    private String tema;
    private int nivelDificultad;
    private List<Room> salas;
    private Room salaJefe;

    public ConcreteDungeonBuilder() {
        this.reset();
    }

    @Override
    public void reset() {
        this.nombre = "";
        this.tema = "";
        this.nivelDificultad = 1;
        this.salas = new ArrayList<>();
        this.salaJefe = null;
    }

    @Override
    public DungeonBuilder setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    @Override
    public DungeonBuilder setTema(String tema) {
        this.tema = tema;
        return this;
    }

    @Override
    public DungeonBuilder setNivelDificultad(int nivel) {
        this.nivelDificultad = Math.max(1, nivel);
        return this;
    }

    @Override
    public DungeonBuilder agregarSala(String nombre, String descripcion, 
                                       int dificultad, boolean tieneTesoro, 
                                       boolean tieneEnemigo) {
        Room sala = new Room(nombre, descripcion, dificultad, 
                            tieneTesoro, tieneEnemigo);
        this.salas.add(sala);
        return this;
    }

    @Override
    public DungeonBuilder setSalaJefe(String nombre, String descripcion, int dificultad) {
        this.salaJefe = new Room(nombre, descripcion, dificultad, true, true);
        return this;
    }

    @Override
    public Dungeon build() {
        if (nombre.isEmpty() || tema.isEmpty()) {
            throw new IllegalStateException(
                "La mazmorra debe tener nombre y tema antes de construirse");
        }
        if (salaJefe == null) {
            throw new IllegalStateException(
                "La mazmorra debe tener una sala de jefe");
        }
        
        Dungeon dungeon = new Dungeon(nombre, tema, nivelDificultad, 
                                     salas, salaJefe);
        this.reset();
        return dungeon;
    }
}
