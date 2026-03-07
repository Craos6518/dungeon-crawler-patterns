package game.dungeon.builder;

import game.dungeon.model.Dungeon;

/**
 * Builder Pattern - Interfaz para construir mazmorras paso a paso
 * 
 * Permite separar la construcción de la representación final.
 */
public interface DungeonBuilder {
    /**
     * Establece información básica de la mazmorra
     */
    DungeonBuilder setNombre(String nombre);
    
    /**
     * Establece el tema de la mazmorra
     */
    DungeonBuilder setTema(String tema);
    
    /**
     * Establece el nivel de dificultad
     */
    DungeonBuilder setNivelDificultad(int nivel);
    
    /**
     * Agrega una sala normal a la mazmorra
     */
    DungeonBuilder agregarSala(String nombre, String descripcion, 
                                int dificultad, boolean tieneTesoro, 
                                boolean tieneEnemigo);
    
    /**
     * Establece la sala del jefe final
     */
    DungeonBuilder setSalaJefe(String nombre, String descripcion, int dificultad);
    
    /**
     * Construye y retorna la mazmorra final
     */
    Dungeon build();
    
    /**
     * Reinicia el builder para construir una nueva mazmorra
     */
    void reset();
}
