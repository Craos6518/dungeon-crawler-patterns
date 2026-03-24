package game.dungeon.builder;

import game.dungeon.model.Dungeon;
import game.dungeon.theme.DungeonThemeFactory;

import java.util.Objects;
import java.util.Random;

/**
 * Generador procedural de mazmorras usando el Builder Pattern.
 *
 * Crea una estructura distinta en cada partida según tema y semilla aleatoria,
 * reutilizando la interfaz {@link DungeonBuilder} sin acoplarse a una UI concreta.
 */
public final class ProceduralDungeonGenerator {

    private static final String[] VARIANTES_FUEGO = {
        "Caldera", "Brasas", "Magma", "Forja", "Ceniza", "Lava"
    };
    private static final String[] VARIANTES_HIELO = {
        "Escarcha", "Glaciar", "Permafrost", "Tundra", "Cristal", "Ventisca"
    };
    private static final String[] VARIANTES_OSCURIDAD = {
        "Sombras", "Abismo", "Cripta", "Eco", "Penumbra", "Umbral"
    };
    private static final String[] VARIANTES_VENENO = {
        "Toxina", "Miasma", "Acido", "Espora", "Pantano", "Corrosion"
    };

    private ProceduralDungeonGenerator() {
    }

    public static Dungeon generar(
        DungeonBuilder builder,
        DungeonThemeFactory tema,
        Random random
    ) {
        Objects.requireNonNull(builder, "builder no puede ser null");
        Objects.requireNonNull(tema, "tema no puede ser null");
        Objects.requireNonNull(random, "random no puede ser null");

        builder.reset();

        String nombreTema = tema.getNombreTema();
        int dificultadBase = dificultadPorTema(nombreTema);
        int cantidadSalas = 4 + random.nextInt(5); // 4..8 salas normales

        builder
            .setNombre(nombreMazmorra(nombreTema, random))
            .setTema(nombreTema)
            .setNivelDificultad(dificultadBase);

        for (int i = 1; i <= cantidadSalas; i++) {
            int dificultadSala = dificultadBase + ((i - 1) / 2) + random.nextInt(2);
            boolean tieneTesoro = random.nextInt(100) < Math.min(60, 18 + (i * 7));
            boolean tieneEnemigo = random.nextInt(100) < 78;

            String variante = varianteTema(nombreTema, random);
            String nombreSala = "Sala " + i + " - " + variante;
            String descripcion = "Camara " + i + " del tema " + nombreTema.toLowerCase()
                + ", con trazas de " + variante.toLowerCase() + ".";

            builder.agregarSala(nombreSala, descripcion, dificultadSala, tieneTesoro, tieneEnemigo);
        }

        int dificultadJefe = dificultadBase + 3 + random.nextInt(2);
        builder.setSalaJefe(
            "Nucleo " + varianteTema(nombreTema, random),
            "Camara final donde espera el guardian del tema " + nombreTema.toLowerCase() + ".",
            dificultadJefe
        );

        return builder.build();
    }

    private static int dificultadPorTema(String tema) {
        return switch (tema) {
            case "Fuego" -> 3;
            case "Hielo" -> 2;
            case "Oscuridad" -> 4;
            case "Veneno" -> 3;
            default -> 2;
        };
    }

    private static String nombreMazmorra(String tema, Random random) {
        String prefijo = switch (tema) {
            case "Fuego" -> "Volcan";
            case "Hielo" -> "Bastion";
            case "Oscuridad" -> "Fortaleza";
            case "Veneno" -> "Catacumba";
            default -> "Mazmorra";
        };
        String sufijo = varianteTema(tema, random);
        return prefijo + " de " + sufijo;
    }

    private static String varianteTema(String tema, Random random) {
        String[] tabla = switch (tema) {
            case "Fuego" -> VARIANTES_FUEGO;
            case "Hielo" -> VARIANTES_HIELO;
            case "Oscuridad" -> VARIANTES_OSCURIDAD;
            case "Veneno" -> VARIANTES_VENENO;
            default -> new String[] {"Runas", "Ecos", "Guardianes"};
        };
        return tabla[random.nextInt(tabla.length)];
    }
}