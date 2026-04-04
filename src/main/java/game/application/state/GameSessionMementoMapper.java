package game.application.state;

import game.domain.DomainRuleViolationException;
import game.domain.character.Enemy;
import game.domain.inventory.Item;
import game.domain.personaje.Dragon;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;
import game.items.model.SimpleItem;
import game.persistence.memento.GameMemento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Mapper unico para snapshot/restore de GameSession via memento.
 */
public final class GameSessionMementoMapper {

    private static final Set<String> SUPPORTED_SCREENS = Set.of("menu", "exploration", "combat", "inventory", "saves");

    private GameSessionMementoMapper() {
    }

    public static GameMemento toMemento(GameSession session) {
        return toMemento(session, session.activeScreen());
    }

    public static GameMemento toMemento(GameSession session, String currentScreen) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Item item : session.inventory().items()) {
            Map<String, Object> data = new HashMap<>();
            data.put("nombre", item.getName());
            data.put("descripcion", item.getDescription());
            data.put("tipo", item.getType());
            data.put("valor", item.getValue());
            data.put("peso", item.getWeight());
            items.add(data);
        }

        boolean combatActive = session.combat().isActive();
        String enemyName = null;
        Integer enemyHp = null;
        Integer enemyHpMax = null;
        Integer enemyXpReward = null;
        String enemyType = null;
        Integer enemyAttack = null;
        if (combatActive) {
            var enemy = session.combat().currentEnemy();
            enemyName = enemy.name();
            enemyHp = enemy.hp();
            enemyHpMax = enemy.maxHp();
            enemyXpReward = enemy.getExperienceReward();

            var enemyCharacter = enemy.character();
            enemyType = enemyCharacter.getClass().getSimpleName();
            if (enemyCharacter instanceof EnemigoBasico basicEnemy) {
                enemyAttack = basicEnemy.getAtaqueBase();
            } else if (enemyCharacter instanceof Orco orc) {
                enemyAttack = orc.getFuerza();
            } else if (enemyCharacter instanceof Dragon dragon) {
                enemyAttack = dragon.getFuegoDragon();
            }
        }

        return new GameMemento.Builder()
            .nombreJugador(session.player().name())
            .nivelActual(Math.max(1, session.player().level()))
            .salaActual(session.dungeon().currentRoomIndex() + 1)
            .agregarEstadoPersonaje("vida", session.player().hp())
            .agregarEstadoPersonaje("vidaMaxima", session.player().maxHp())
            .agregarEstadoPersonaje("heroType", session.heroType())
            .agregarEstadoPersonaje("heroSelectionLocked", session.isHeroSelectionLocked())
            .agregarEstadoPersonaje("nivel", session.player().level())
            .agregarEstadoPersonaje("experiencia", session.player().experience())
            .agregarEstadoPersonaje("enemigosDerrotados", session.player().defeatedEnemies())
            .agregarEstadoPersonaje("oroAcumulado", session.player().gold())
            .agregarEstadoPersonaje("defensaActiva", session.combat().isDefenseActive())
            .agregarEstadoPersonaje("venenoTurnos", session.combat().poisonTurns())
            .agregarEstadoPersonaje("venenoDanio", session.combat().poisonDamage())
            .agregarEstadoInventario("items", items)
            .agregarEstadoInventario("selectedIndex", session.inventory().selectedIndex())
            .agregarEstadoMazmorra("tema", session.dungeon().themeName())
            .agregarEstadoMazmorra("schemaVersion", 1)
            .agregarEstadoMazmorra("totalRooms", session.dungeon().totalRooms())
            .agregarEstadoMazmorra("estadoActual", currentScreen)
            .agregarEstadoMazmorra("salaActualIndex", session.dungeon().currentRoomIndex())
            .agregarEstadoMazmorra("completedThemes", new ArrayList<>(session.completedThemes()))
            .agregarEstadoMazmorra("salasTesoroResuelto", new ArrayList<>(session.dungeon().treasureResolvedRooms()))
            .agregarEstadoMazmorra("salasEnemigoResuelto", new ArrayList<>(session.dungeon().enemyResolvedRooms()))
            .agregarEstadoMazmorra("combateActivo", combatActive)
            .agregarEstadoMazmorra("combateBoss", session.combat().isBossFight())
            .agregarEstadoMazmorra("enemigoNombre", enemyName)
            .agregarEstadoMazmorra("enemigoHp", enemyHp)
            .agregarEstadoMazmorra("enemigoHpMax", enemyHpMax)
            .agregarEstadoMazmorra("enemigoXp", enemyXpReward)
            .agregarEstadoMazmorra("enemigoTipo", enemyType)
            .agregarEstadoMazmorra("enemigoAtaque", enemyAttack)
            .agregarEstadoMazmorra("eventLog", new ArrayList<>(session.eventLog()))
            .agregarEstadoMazmorra("combatLog", new ArrayList<>(session.combatLog()))
            .build();
    }

    public static void restore(GameSession session, GameMemento memento) {
        restoreInternal(session, memento, false);
    }

    public static void restoreStrict(GameSession session, GameMemento memento) {
        restoreInternal(session, memento, true);
    }

    private static void restoreInternal(GameSession session, GameMemento memento, boolean strict) {
        if (memento == null) {
            throw corrupt("memento nulo");
        }

        Map<String, Object> characterState = memento.getEstadoPersonaje();
        Map<String, Object> inventoryState = memento.getEstadoInventario();
        Map<String, Object> dungeonState = memento.getEstadoMazmorra();

        if (strict && (characterState == null || inventoryState == null || dungeonState == null)) {
            throw corrupt("secciones de estado faltantes");
        }

        int level = readInt(characterState.get("nivel"), memento.getNivelActual());
        int experience = readInt(characterState.get("experiencia"), 0);
        String heroType = normalizeHeroType(readString(characterState.get("heroType"), session.heroType()));
        boolean heroSelectionLocked = readBoolean(characterState.get("heroSelectionLocked"), false);
        int maxHp = readInt(characterState.get("vidaMaxima"), session.player().maxHp());
        int hp = readInt(characterState.get("vida"), maxHp);
        int gold = readInt(characterState.get("oroAcumulado"), 0);
        int defeatedEnemies = readInt(characterState.get("enemigosDerrotados"), 0);

        if (strict && level < 1) {
            throw corrupt("nivel invalido");
        }
        if (strict && maxHp <= 0) {
            throw corrupt("vidaMaxima invalida");
        }
        if (strict && (hp < 0 || hp > maxHp)) {
            throw corrupt("vida fuera de rango");
        }
        if (strict && experience < 0) {
            throw corrupt("experiencia invalida");
        }
        if (strict && gold < 0) {
            throw corrupt("oroAcumulado invalido");
        }
        if (strict && defeatedEnemies < 0) {
            throw corrupt("enemigosDerrotados invalido");
        }

        session.player().restoreProgress(
            Math.max(1, level),
            Math.max(0, experience),
            Math.max(0, Math.min(hp, Math.max(1, maxHp))),
            Math.max(0, gold),
            Math.max(0, defeatedEnemies)
        );

        if (!heroType.isBlank()) {
            session.setHeroType(heroType);
        }
        session.setHeroSelectionLocked(heroSelectionLocked);

        List<SimpleItem> restoredItems = parseItems(inventoryState.get("items"), strict);
        Integer selectedIndex = readNullableInt(inventoryState.get("selectedIndex"));
        if (strict && !isValidSelectedIndex(selectedIndex, restoredItems.size())) {
            throw corrupt("selectedIndex invalido");
        }
        session.inventory().replaceItems(restoredItems, selectedIndex);

        int totalRooms = session.dungeon().totalRooms();
        Integer schemaVersion = readNullableInt(dungeonState.get("schemaVersion"));
        if (strict && schemaVersion != null && schemaVersion > 1) {
            throw corrupt("version de guardado no soportada");
        }
        if (strict && schemaVersion != null && schemaVersion < 1) {
            throw corrupt("schemaVersion invalido");
        }

        Integer savedTotalRooms = readNullableInt(dungeonState.get("totalRooms"));
        if (strict && savedTotalRooms != null && savedTotalRooms <= 0) {
            throw corrupt("totalRooms invalido");
        }
        if (strict && savedTotalRooms != null && savedTotalRooms != totalRooms) {
            throw corrupt("estructura de mazmorra incompatible");
        }

        int roomIndex = readInt(dungeonState.get("salaActualIndex"), Math.max(0, memento.getSalaActual() - 1));
        String estadoActual = String.valueOf(dungeonState.get("estadoActual"));
        boolean isMenuScreen = "menu".equalsIgnoreCase(estadoActual) || (estadoActual != null && estadoActual.toLowerCase().contains("menu"));
        if (strict && (roomIndex < 0 || roomIndex >= totalRooms) && !isMenuScreen) {
            throw corrupt("salaActualIndex fuera de rango");
        }

        Set<Integer> treasureResolved = parseIndexSet(
            dungeonState.get("salasTesoroResuelto"),
            totalRooms,
            strict,
            "salasTesoroResuelto"
        );
        Set<Integer> enemyResolved = parseIndexSet(
            dungeonState.get("salasEnemigoResuelto"),
            totalRooms,
            strict,
            "salasEnemigoResuelto"
        );
        Set<String> completedThemes = parseThemeSet(
            dungeonState.get("completedThemes"),
            strict,
            "completedThemes"
        );

        session.dungeon().restoreProgress(
            Math.max(0, Math.min(roomIndex, Math.max(0, totalRooms - 1))),
            treasureResolved,
            enemyResolved
        );
        session.replaceCompletedThemes(completedThemes);

        boolean combatActive = readBoolean(dungeonState.get("combateActivo"), false);
        if (combatActive) {
            Enemy restoredEnemy = parseEnemyState(dungeonState, strict);
            if (restoredEnemy == null || !restoredEnemy.isAlive()) {
                throw corrupt("combateActivo=true sin enemigo valido");
            }
            boolean bossFight = readBoolean(dungeonState.get("combateBoss"), false);
            session.combat().restoreActiveEnemy(restoredEnemy, bossFight);
        } else {
            session.combat().finish();
        }

        boolean defenseActive = readBoolean(characterState.get("defensaActiva"), false);
        int poisonTurns = readInt(characterState.get("venenoTurnos"), 0);
        int poisonDamage = readInt(characterState.get("venenoDanio"), 0);
        if (strict && poisonTurns < 0) {
            throw corrupt("venenoTurnos invalido");
        }
        if (strict && poisonDamage < 0) {
            throw corrupt("venenoDanio invalido");
        }
        session.combat().restoreTurnState(defenseActive, Math.max(0, poisonTurns), Math.max(0, poisonDamage));

        String screen = readString(dungeonState.get("estadoActual"), "exploration");
        if (strict && !SUPPORTED_SCREENS.contains(screen)) {
            throw corrupt("estadoActual invalido");
        }

        if (combatActive) {
            screen = "combat";
        } else if ("combat".equals(screen)) {
            if (strict) {
                throw corrupt("estadoActual inconsistente con combateActivo");
            }
            screen = "exploration";
        }

        if (!SUPPORTED_SCREENS.contains(screen)) {
            screen = "exploration";
        }
        session.setActiveScreen(screen);

        session.replaceEventLog(parseStringList(dungeonState.get("eventLog"), strict, "eventLog"));
        session.replaceCombatLog(parseStringList(dungeonState.get("combatLog"), strict, "combatLog"));
    }

    private static Enemy parseEnemyState(Map<String, Object> dungeonState, boolean strict) {
        String enemyName = readString(dungeonState.get("enemigoNombre"), null);
        Integer enemyHp = readNullableInt(dungeonState.get("enemigoHp"));
        Integer enemyHpMax = readNullableInt(dungeonState.get("enemigoHpMax"));

        if (enemyName == null || enemyName.isBlank() || enemyHp == null || enemyHpMax == null || enemyHpMax <= 0) {
            if (strict) {
                throw corrupt("datos de enemigo incompletos");
            }
            return null;
        }

        int hpMax = Math.max(1, enemyHpMax);
        if (strict && (enemyHp < 1 || enemyHp > hpMax)) {
            throw corrupt("vida de enemigo fuera de rango");
        }

        String enemyType = readString(dungeonState.get("enemigoTipo"), "EnemigoBasico");
        int attack = readInt(dungeonState.get("enemigoAtaque"), Math.max(1, hpMax / 10));
        if (strict && attack <= 0) {
            throw corrupt("ataque de enemigo invalido");
        }

        Personaje enemyCharacter = buildEnemyCharacter(enemyType, enemyName, hpMax, Math.max(1, attack));

        int hp = Math.max(0, Math.min(enemyHp, hpMax));
        if (hp < hpMax) {
            enemyCharacter.recibirDanio(hpMax - hp);
        }

        Enemy enemy = new Enemy(enemyCharacter);
        int xpReward = readInt(dungeonState.get("enemigoXp"), Math.max(20, hpMax * 2));
        if (strict && xpReward <= 0) {
            throw corrupt("enemigoXp invalido");
        }
        enemy.setExperienceReward(Math.max(1, xpReward));
        return enemy;
    }

    private static Personaje buildEnemyCharacter(String enemyType, String name, int hpMax, int attack) {
        return switch (enemyType) {
            case "Dragon" -> new Dragon(name, hpMax, attack);
            case "Orco" -> new Orco(name, hpMax, attack);
            default -> new EnemigoBasico(name, hpMax, attack);
        };
    }

    private static List<SimpleItem> parseItems(Object rawItems, boolean strict) {
        List<SimpleItem> restored = new ArrayList<>();
        if (rawItems == null) {
            return restored;
        }
        if (!(rawItems instanceof List<?> list)) {
            if (strict) {
                throw corrupt("items invalido");
            }
            return restored;
        }

        for (Object entry : list) {
            if (!(entry instanceof Map<?, ?> map)) {
                if (strict) {
                    throw corrupt("entrada de item invalida");
                }
                continue;
            }

            String name = readString(map.get("nombre"), "Item");
            String description = readString(map.get("descripcion"), "");
            String type = readString(map.get("tipo"), "Consumible");
            int value = readInt(map.get("valor"), 0);
            int weight = readInt(map.get("peso"), 1);

            if (strict && (name.isBlank() || weight <= 0)) {
                throw corrupt("item con datos invalidos");
            }

            restored.add(new SimpleItem(name, description, type, Math.max(0, value), Math.max(1, weight)));
        }

        return restored;
    }

    private static Set<Integer> parseIndexSet(Object raw, int totalRooms, boolean strict, String fieldName) {
        Set<Integer> resolved = new HashSet<>();
        if (raw == null) {
            return resolved;
        }
        if (!(raw instanceof List<?> list)) {
            if (strict) {
                throw corrupt(fieldName + " invalido");
            }
            return resolved;
        }

        for (Object value : list) {
            Integer idx = readNullableInt(value);
            if (idx == null) {
                if (strict) {
                    throw corrupt(fieldName + " contiene valor no entero");
                }
                continue;
            }
            if (idx < 0 || idx >= totalRooms) {
                if (strict) {
                    throw corrupt(fieldName + " contiene indice fuera de rango");
                }
                continue;
            }
            resolved.add(idx);
        }
        return resolved;
    }

    private static Set<String> parseThemeSet(Object raw, boolean strict, String fieldName) {
        Set<String> themes = new HashSet<>();
        if (raw == null) {
            return themes;
        }
        if (!(raw instanceof List<?> list)) {
            if (strict) {
                throw corrupt(fieldName + " invalido");
            }
            return themes;
        }

        for (Object value : list) {
            if (value == null) {
                continue;
            }
            String normalized = String.valueOf(value).trim().toLowerCase(Locale.ROOT);
            if ("fire".equals(normalized) || "ice".equals(normalized)
                || "poison".equals(normalized) || "dark".equals(normalized)) {
                themes.add(normalized);
                continue;
            }
            if (strict) {
                throw corrupt(fieldName + " contiene tema invalido");
            }
        }

        return themes;
    }

    private static List<String> parseStringList(Object raw, boolean strict, String fieldName) {
        List<String> lines = new ArrayList<>();
        if (raw == null) {
            return lines;
        }
        if (!(raw instanceof List<?> list)) {
            if (strict) {
                throw corrupt(fieldName + " invalido");
            }
            return lines;
        }

        for (Object value : list) {
            lines.add(value == null ? "" : String.valueOf(value));
        }
        return lines;
    }

    private static boolean isValidSelectedIndex(Integer selectedIndex, int itemCount) {
        if (selectedIndex == null) {
            return true;
        }
        if (itemCount <= 0) {
            return selectedIndex == -1 || selectedIndex == 0;
        }
        return selectedIndex >= 0 && selectedIndex < itemCount;
    }

    private static String readString(Object value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String text = String.valueOf(value);
        return text.isBlank() ? fallback : text;
    }

    private static int readInt(Object value, int fallback) {
        Integer parsed = readNullableInt(value);
        return parsed == null ? fallback : parsed;
    }

    private static Integer readNullableInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean readBoolean(Object value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        String raw = String.valueOf(value).trim().toLowerCase();
        if ("true".equals(raw)) {
            return true;
        }
        if ("false".equals(raw)) {
            return false;
        }
        return fallback;
    }

    private static String normalizeHeroType(String heroType) {
        if (heroType == null) {
            return "";
        }

        String normalized = heroType.trim().toLowerCase();
        if ("mago".equals(normalized) || "arquero".equals(normalized) || "guerrero".equals(normalized)) {
            return normalized;
        }
        return "";
    }

    private static DomainRuleViolationException corrupt(String detail) {
        return new DomainRuleViolationException("Guardado corrupto: " + detail + ".");
    }
}