package game.dungeon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una mazmorra completa con múltiples salas
 */
public class Dungeon {
    private final String nombre;
    private final String tema;
    private final int nivelDificultad;
    private final List<Room> salas;
    private final Room salaJefe;

    public Dungeon(String nombre, String tema, int nivelDificultad, 
                   List<Room> salas, Room salaJefe) {
        this.nombre = nombre;
        this.tema = tema;
        this.nivelDificultad = nivelDificultad;
        this.salas = new ArrayList<>(salas);
        this.salaJefe = salaJefe;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTema() {
        return tema;
    }

    public int getNivelDificultad() {
        return nivelDificultad;
    }

    public List<Room> getSalas() {
        return Collections.unmodifiableList(salas);
    }

    public Room getSalaJefe() {
        return salaJefe;
    }

    public int getCantidadSalas() {
        return salas.size();
    }

    @Override
    public String toString() {
        return String.format("Dungeon: %s [Tema: %s, Nivel: %d, Salas: %d]", 
            nombre, tema, nivelDificultad, salas.size() + 1);
    }
}
