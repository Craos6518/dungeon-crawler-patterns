package game.dungeon.builder;

import game.dungeon.model.Dungeon;
import game.dungeon.theme.DungeonThemeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private static final class RoomSeed {
        private final String name;
        private final String description;
        private final int difficultyOffset;
        private final boolean hasTreasure;
        private final boolean hasEnemy;

        private RoomSeed(
            String name,
            String description,
            int difficultyOffset,
            boolean hasTreasure,
            boolean hasEnemy
        ) {
            this.name = name;
            this.description = description;
            this.difficultyOffset = difficultyOffset;
            this.hasTreasure = hasTreasure;
            this.hasEnemy = hasEnemy;
        }
    }

    private static final class DungeonProfile {
        private final String dungeonName;
        private final int baseDifficultyMin;
        private final int baseDifficultyMax;
        private final int minRooms;
        private final int maxRooms;
        private final RoomSeed[] roomTemplates;
        private final int enemyChanceMin;
        private final int enemyChanceMax;
        private final int treasureChanceMin;
        private final int treasureChanceMax;
        private final String bossRoomName;
        private final String bossRoomDescription;

        private DungeonProfile(
            String dungeonName,
            int baseDifficultyMin,
            int baseDifficultyMax,
            int minRooms,
            int maxRooms,
            RoomSeed[] roomTemplates,
            int enemyChanceMin,
            int enemyChanceMax,
            int treasureChanceMin,
            int treasureChanceMax,
            String bossRoomName,
            String bossRoomDescription
        ) {
            this.dungeonName = dungeonName;
            this.baseDifficultyMin = baseDifficultyMin;
            this.baseDifficultyMax = baseDifficultyMax;
            this.minRooms = minRooms;
            this.maxRooms = maxRooms;
            this.roomTemplates = roomTemplates;
            this.enemyChanceMin = enemyChanceMin;
            this.enemyChanceMax = enemyChanceMax;
            this.treasureChanceMin = treasureChanceMin;
            this.treasureChanceMax = treasureChanceMax;
            this.bossRoomName = bossRoomName;
            this.bossRoomDescription = bossRoomDescription;
        }
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
        DungeonProfile profile = predefinedProfile(nombreTema);
        if (profile != null) {
            return buildFromProfile(builder, nombreTema, profile, random);
        }

        return buildProceduralFallback(builder, nombreTema, random);
    }

    private static Dungeon buildFromProfile(
        DungeonBuilder builder,
        String nombreTema,
        DungeonProfile profile,
        Random random
    ) {
        int dificultadBase = ranged(random, profile.baseDifficultyMin, profile.baseDifficultyMax);
        List<RoomSeed> selectedRooms = selectRooms(profile, random);

        builder
            .setNombre(profile.dungeonName)
            .setTema(nombreTema)
            .setNivelDificultad(dificultadBase);

        for (int i = 0; i < selectedRooms.size(); i++) {
            RoomSeed room = selectedRooms.get(i);
            boolean anchorRoom = i == 0 || i == selectedRooms.size() - 1;
            int dificultadSala = Math.max(
                1,
                dificultadBase + room.difficultyOffset + (anchorRoom ? 0 : random.nextInt(2))
            );

            boolean tieneEnemigo = anchorRoom
                ? room.hasEnemy
                : varyFlag(room.hasEnemy, profile.enemyChanceMin, profile.enemyChanceMax, random);
            boolean tieneTesoro = anchorRoom
                ? room.hasTreasure
                : varyFlag(room.hasTreasure, profile.treasureChanceMin, profile.treasureChanceMax, random);

            builder.agregarSala(
                room.name,
                room.description,
                dificultadSala,
                tieneTesoro,
                tieneEnemigo
            );
        }

        int dificultadJefe = dificultadBase + Math.max(3, selectedRooms.size() / 2);
        builder.setSalaJefe(profile.bossRoomName, profile.bossRoomDescription, dificultadJefe);
        return builder.build();
    }

    private static List<RoomSeed> selectRooms(DungeonProfile profile, Random random) {
        RoomSeed[] templates = profile.roomTemplates;
        if (templates.length <= 2) {
            return List.of(templates);
        }

        int targetRooms = ranged(random, profile.minRooms, profile.maxRooms);
        int optionalAvailable = templates.length - 2;
        int optionalTarget = Math.max(0, Math.min(optionalAvailable, targetRooms - 2));

        List<Integer> optionalIndices = new ArrayList<>();
        for (int i = 1; i < templates.length - 1; i++) {
            optionalIndices.add(i);
        }

        Collections.shuffle(optionalIndices, random);
        optionalIndices = new ArrayList<>(optionalIndices.subList(0, optionalTarget));
        Collections.sort(optionalIndices);

        List<RoomSeed> selected = new ArrayList<>();
        selected.add(templates[0]);
        for (Integer index : optionalIndices) {
            selected.add(templates[index]);
        }
        selected.add(templates[templates.length - 1]);
        return selected;
    }

    private static boolean varyFlag(boolean defaultValue, int chanceMin, int chanceMax, Random random) {
        int trueChance = defaultValue
            ? ranged(random, chanceMin, chanceMax)
            : Math.max(0, ranged(random, chanceMin, chanceMax) - 35);
        return random.nextInt(100) < trueChance;
    }

    private static int ranged(Random random, int min, int max) {
        if (max <= min) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    private static Dungeon buildProceduralFallback(
        DungeonBuilder builder,
        String nombreTema,
        Random random
    ) {
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

    private static DungeonProfile predefinedProfile(String tema) {
        return switch (tema) {
            case "Fuego" -> new DungeonProfile(
                "Volcan de Ignareth",
                3,
                3,
                5,
                7,
                new RoomSeed[] {
                    new RoomSeed("Entrada de Ignareth", "Acceso principal al volcan ancestral.", 0, false, false),
                    new RoomSeed("Galeria de Obsidiana", "Pasillo de roca negra con fisuras termicas.", 0, false, true),
                    new RoomSeed("Rio de Lava", "Un cauce de magma corta el camino central.", 1, false, true),
                    new RoomSeed("Camara de Magma", "Camara de presion con brasas activas.", 1, true, true),
                    new RoomSeed("Forja Infernal", "Antigua forja custodiada por bestias de fuego.", 2, true, true),
                    new RoomSeed("Santuario de Brasas", "Sala ritual antes del trono del guardian.", 2, false, true),
                    new RoomSeed("Trono de Pyraxis", "Nucleo volcanico donde espera Pyraxis.", 3, true, true)
                },
                72,
                94,
                26,
                64,
                "Nucleo de Ignareth",
                "Camara final donde arde la Piedra del Fuego Eterno."
            );
            case "Hielo" -> new DungeonProfile(
                "Catacumbas de Glaciurvh",
                2,
                2,
                6,
                8,
                new RoomSeed[] {
                    new RoomSeed("Atrio Congelado", "Entrada cubierta de escarcha perpetua.", 0, false, false),
                    new RoomSeed("Corredor de Escarcha", "Pasillo con rafagas heladas y visibilidad baja.", 0, false, true),
                    new RoomSeed("Cripta del Viento Blanco", "Cripta resonante con frio cortante.", 1, false, true),
                    new RoomSeed("Camara de Reliquias", "Deposito de artefactos congelados.", 1, true, true),
                    new RoomSeed("Sala Sellada", "Compuerta antigua que exige precision para avanzar.", 2, false, true),
                    new RoomSeed("Galeria del Silencio", "Zona de hielo compacto y eco apagado.", 2, true, true),
                    new RoomSeed("Santuario de Glaciurvh", "Antecamera del dragon de invierno.", 3, false, true),
                    new RoomSeed("Trono de Kryovaleth", "Camara del cristal primordial y su guardian.", 3, true, true)
                },
                70,
                92,
                24,
                58,
                "Corazon de Glaciurvh",
                "Camara final del Cristal del Hielo Primordial."
            );
            case "Veneno" -> new DungeonProfile(
                "Pantanos de Viridax",
                3,
                3,
                6,
                8,
                new RoomSeed[] {
                    new RoomSeed("Entrada de Viridax", "Umbral de agua turbia y neblina toxica.", 0, false, false),
                    new RoomSeed("Sendero de Esporas", "Camino inestable cubierto de esporas corrosivas.", 0, false, true),
                    new RoomSeed("Humedal Corrosivo", "Estanque acido que desgasta equipo y cuerpo.", 1, false, true),
                    new RoomSeed("Nido de Capullos", "Zona infestada con huevos y telas venenosas.", 1, true, true),
                    new RoomSeed("Camara de Residuos", "Camara con vapores toxicos acumulados.", 2, false, true),
                    new RoomSeed("Galeria de Miasma", "Galeria envenenada de visibilidad reducida.", 2, true, true),
                    new RoomSeed("Guarida Tejida", "Antecamera biologica de la reina aracnida.", 3, false, true),
                    new RoomSeed("Trono de Arachnovex", "Guarida final donde domina Arachnovex.", 3, true, true)
                },
                74,
                96,
                22,
                56,
                "Semilla Corrupta",
                "Camara final de la Semilla de la Vida Corrupta."
            );
            case "Oscuridad" -> new DungeonProfile(
                "Ciudadela de Umbrakar",
                4,
                4,
                8,
                10,
                new RoomSeed[] {
                    new RoomSeed("Portal de Umbrakar", "Acceso principal envuelto en sombras densas.", 0, false, false),
                    new RoomSeed("Corredor del Olvido", "Pasillo oscuro con ecos que confunden la memoria.", 0, false, true),
                    new RoomSeed("Biblioteca Prohibida", "Estanterias malditas con runas en penumbra.", 1, true, true),
                    new RoomSeed("Calabozos de Penumbra", "Celdas antiguas donde acechan vigias oscuros.", 1, false, true),
                    new RoomSeed("Salon de Huesos", "Camara ceremonial cubierta de restos ancestrales.", 2, true, true),
                    new RoomSeed("Patio de Ecos", "Patio interno donde las sombras replican pasos.", 2, false, true),
                    new RoomSeed("Camara del Laberinto", "Zona mutable que altera la orientacion.", 3, true, true),
                    new RoomSeed("Galeria Sin Memoria", "Galeria donde el tiempo parece fragmentado.", 3, false, true),
                    new RoomSeed("Santuario del Fragmento", "Antecamera del Fragmento de la Oscuridad Absoluta.", 4, true, true),
                    new RoomSeed("Trono de Malachar", "Camara final donde aguarda Malachar.", 4, true, true)
                },
                68,
                92,
                28,
                62,
                "Corazon de Umbrakar",
                "Camara final del Fragmento de la Oscuridad Absoluta."
            );
            default -> null;
        };
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