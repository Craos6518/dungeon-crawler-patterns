package game.dungeon.builder;

import game.dungeon.model.Dungeon;
import game.dungeon.theme.DungeonThemeFactory;
import java.util.Random;

/**
 * Builder Pattern - Director que construye mazmorras predefinidas
 * 
 * Opcional: simplifica la construcción de mazmorras comunes.
 */
public class DungeonDirector {
    private final DungeonBuilder builder;

    public DungeonDirector(DungeonBuilder builder) {
        this.builder = builder;
    }

    /**
     * Construye una mazmorra simple para principiantes
     */
    public Dungeon construirMazmorraBasica() {
        return builder
            .setNombre("Cueva del Inicio")
            .setTema("Cueva")
            .setNivelDificultad(1)
            .agregarSala("Entrada", "Una cueva oscura y húmeda", 1, false, true)
            .agregarSala("Pasillo", "Un estrecho corredor de piedra", 1, true, false)
            .agregarSala("Cámara", "Una amplia sala con columnas", 2, false, true)
            .setSalaJefe("Trono del Goblin", "Sala del jefe Goblin Rey", 3)
            .build();
    }

    /**
     * Construye una mazmorra de fuego de dificultad media
     */
    public Dungeon construirMazmorraFuego() {
        return builder
            .setNombre("Volcán Ardiente")
            .setTema("Fuego")
            .setNivelDificultad(3)
            .agregarSala("Entrada Caliente", "Rocas volcánicas y lava", 3, false, true)
            .agregarSala("Río de Lava", "Puente sobre lava hirviente", 3, true, true)
            .agregarSala("Cámara de Magma", "Sala llena de fuego", 4, true, true)
            .agregarSala("Forja Infernal", "Antigua forja de dragones", 4, true, true)
            .setSalaJefe("Nido del Dragón", "Guarida del Dragón de Fuego", 5)
            .build();
    }

    /**
     * Construye una mazmorra oscura difícil
     */
    public Dungeon construirMazmorraOscura() {
        return builder
            .setNombre("Fortaleza Sombría")
            .setTema("Oscuridad")
            .setNivelDificultad(5)
            .agregarSala("Portal Oscuro", "Entrada envuelta en tinieblas", 5, false, true)
            .agregarSala("Corredor Maldito", "Paredes que susurran", 5, true, true)
            .agregarSala("Biblioteca Prohibida", "Tomos de magia oscura", 6, true, true)
            .agregarSala("Calabozos", "Celdas de antiguos prisioneros", 6, true, true)
            .agregarSala("Salón de Huesos", "Montañas de esqueletos", 6, false, true)
            .setSalaJefe("Trono de las Sombras", "Sede del Señor Oscuro", 7)
            .build();
    }

    /**
     * Construye una mazmorra procedural según el tema y la semilla
     */
    public game.domain.exploration.Dungeon buildForTheme(DungeonThemeFactory theme, long seed) {
        Random random = new Random(seed);
        game.dungeon.model.Dungeon generated = ProceduralDungeonGenerator.generar(builder, theme, random);
        return new game.domain.exploration.Dungeon(random, theme, generated, seed);
    }
}
