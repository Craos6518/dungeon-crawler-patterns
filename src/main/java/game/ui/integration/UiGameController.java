package game.ui.integration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.ai.strategy.AggressiveStrategy;
import game.ai.strategy.DefensiveStrategy;
import game.ai.strategy.RandomStrategy;
import game.command.actions.AttackCommand;
import game.command.actions.Command;
import game.command.actions.CommandInvoker;
import game.command.actions.DefendCommand;
import game.command.actions.LevelUpCommand;
import game.command.actions.SkillCommand;
import game.command.actions.UseItemCommand;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.GuerreroFactory;
import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.DungeonBuilder;
import game.dungeon.builder.ProceduralDungeonGenerator;
import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import game.dungeon.theme.DungeonThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.events.observer.EventManager;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.items.model.ContainerItem;
import game.items.model.ItemComponent;
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Controlador de integracion UI<->Java para WebEngine.
 * Expone acciones atomicas para el bridge de JavaScript y mantiene
 * el estado de juego serializable por GamePresenter.
 */
public class UiGameController {

    private static final int EVENT_LOG_LIMIT = 12;
    private static final int COMBAT_LOG_LIMIT = 16;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Random random;
    private final CommandInvoker commandInvoker;
    private final GameCaretaker caretaker;
    private final EventManager eventManager;

    private Personaje heroe;
    private DungeonThemeFactory temaActual;
    private Dungeon mazmorra;
    private ContainerItem inventario;

    private int salaActual;
    private int oroAcumulado;
    private int enemigosDerrotados;

    private Personaje enemigoActual;
    private AIController enemyAI;

    private boolean defensaHeroeActiva;
    private int turnosVenenoHeroe;
    private int danioVenenoHeroe;

    private int selectedItemIndex;
    private String pantallaActiva;

    private final Set<Integer> salasTesoroResuelto;
    private final Set<Integer> salasEnemigoResuelto;

    private final List<String> eventLog;
    private final List<String> combatLog;

    public UiGameController() {
        this.random = new Random();
        this.commandInvoker = new CommandInvoker();
        this.caretaker = new GameCaretaker("./game-saves/");
        this.eventManager = EventManager.getInstance();

        this.salasTesoroResuelto = new HashSet<>();
        this.salasEnemigoResuelto = new HashSet<>();
        this.eventLog = new ArrayList<>();
        this.combatLog = new ArrayList<>();

        inicializarPartidaDemo();
    }

    public synchronized void handleAction(String action, JsonObject payload) {
        String accion = action == null ? "" : action.trim();

        switch (accion) {
            case "advanceRoom" -> advanceRoom();
            case "searchTreasure" -> searchTreasure();
            case "openInventory" -> openInventory();
            case "closeInventory" -> closeInventory();
            case "saveGame" -> saveGame(payload);
            case "forceCombat" -> forceCombat();
            case "attack" -> attack();
            case "defend" -> defend();
            case "useItem" -> useItem(payload);
            case "useSkill" -> useSkill(payload);
            default -> registrarMensajeSistema("Accion no soportada: " + accion);
        }

        clampSelectedItemIndex();
    }

    public synchronized void registrarMensajeSistema(String mensaje) {
        appendEvent("[SYS] " + mensaje);
    }

    public synchronized Personaje getHeroe() {
        return heroe;
    }

    public synchronized Dungeon getMazmorra() {
        return mazmorra;
    }

    public synchronized Room getSalaActualRoom() {
        int idx = Math.max(0, Math.min(salaActual, mazmorra.getSalas().size() - 1));
        return mazmorra.getSalas().get(idx);
    }

    public synchronized int getSalaActualIndex() {
        return salaActual;
    }

    public synchronized int getOroAcumulado() {
        return oroAcumulado;
    }

    public synchronized String getPantallaActiva() {
        return pantallaActiva;
    }

    public synchronized String getThemeName() {
        return temaActual.getNombreTema();
    }

    public synchronized String getThemeKey() {
        return themeNameToKey(getThemeName());
    }

    public synchronized List<String> getEventLog() {
        return List.copyOf(eventLog);
    }

    public synchronized List<String> getCombatLog() {
        return List.copyOf(combatLog);
    }

    public synchronized Personaje getEnemigoActual() {
        return enemigoActual;
    }

    public synchronized boolean hasActiveEnemy() {
        return enemigoActual != null && enemigoActual.estaVivo();
    }

    public synchronized boolean isEnemyPendingInCurrentRoom() {
        if (hasActiveEnemy()) {
            return true;
        }
        Room room = getSalaActualRoom();
        return room.tieneEnemigo() && !salasEnemigoResuelto.contains(salaActual);
    }

    public synchronized boolean isTreasurePendingInCurrentRoom() {
        Room room = getSalaActualRoom();
        return room.tieneTesoro() && !salasTesoroResuelto.contains(salaActual);
    }

    public synchronized List<String> getMinimapSymbols() {
        List<String> symbols = new ArrayList<>();
        int total = mazmorra.getSalas().size();

        for (int i = 0; i < total; i++) {
            if (i == salaActual) {
                symbols.add("current");
            } else if (i < salaActual || salasEnemigoResuelto.contains(i)) {
                symbols.add("cleared");
            } else if (i == total - 1) {
                symbols.add("boss");
            } else {
                symbols.add("unknown");
            }
        }

        return symbols;
    }

    public synchronized List<SimpleItem> getInventoryItems() {
        List<SimpleItem> simples = new ArrayList<>();
        for (ItemComponent component : inventario.obtenerItems()) {
            if (component instanceof SimpleItem simple) {
                simples.add(simple);
            }
        }
        return simples;
    }

    public synchronized int getInventoryCapacity() {
        return inventario.getCapacidadMaxima();
    }

    public synchronized int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public synchronized Map<String, String> getButtonsState() {
        Map<String, String> states = new HashMap<>();

        boolean heroeVivo = heroe != null && heroe.estaVivo();
        boolean combateActivo = hasActiveEnemy();
        boolean pantallaExploracion = "exploration".equals(pantallaActiva);
        boolean pantallaInventario = "inventory".equals(pantallaActiva);
        boolean pantallaCombate = "combat".equals(pantallaActiva);
        boolean hayConsumible = hasConsumableInInventory();
        boolean consumibleSeleccionado = isSelectedItemConsumable();

        states.put("btn-avanzar", (heroeVivo && pantallaExploracion && !combateActivo
            && salaActual < mazmorra.getSalas().size() - 1) ? "default" : "disabled");

        states.put("btn-explorar", (heroeVivo && pantallaExploracion && !combateActivo
            && !salasTesoroResuelto.contains(salaActual)) ? "default" : "disabled");

        states.put("btn-inventory", (heroeVivo && !pantallaCombate) ? "default" : "disabled");
        states.put("btn-guardar", heroeVivo ? "default" : "disabled");

        states.put("btn-forzar-combate", (heroeVivo && pantallaExploracion && !combateActivo)
            ? "default" : "disabled");

        states.put("btn-atacar", (heroeVivo && pantallaCombate && combateActivo)
            ? "default" : "disabled");

        states.put("btn-defender", (heroeVivo && pantallaCombate && combateActivo)
            ? "default" : "disabled");

        states.put("btn-usar-objeto", (heroeVivo && pantallaCombate && combateActivo && hayConsumible)
            ? "default" : "disabled");

        states.put("btn-habilidad", (heroeVivo && pantallaCombate && combateActivo)
            ? "default" : "disabled");

        states.put("btn-use-item-inv", (heroeVivo && pantallaInventario && consumibleSeleccionado)
            ? "default" : "disabled");

        states.put("btn-back-inv", pantallaInventario ? "default" : "disabled");

        return states;
    }

    private void inicializarPartidaDemo() {
        this.heroe = new GuerreroFactory(150, 25).crearPersonaje("Aventurero");
        this.temaActual = new FireThemeFactory();

        DungeonBuilder builder = new ConcreteDungeonBuilder();
        this.mazmorra = ProceduralDungeonGenerator.generar(builder, temaActual, random);

        this.inventario = new ContainerItem("Mochila", "Inventario principal", 20, 2);
        this.inventario.agregar(new SimpleItem("Pocion de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        this.inventario.agregar(new SimpleItem("Pocion de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        this.inventario.agregar(new SimpleItem("Antidoto", "Elimina el veneno", "Consumible", 30, 1));

        this.salaActual = 0;
        this.oroAcumulado = 0;
        this.enemigosDerrotados = 0;
        this.enemigoActual = null;
        this.enemyAI = null;
        this.defensaHeroeActiva = false;
        this.turnosVenenoHeroe = 0;
        this.danioVenenoHeroe = 0;
        this.selectedItemIndex = 0;
        this.pantallaActiva = "exploration";

        this.salasTesoroResuelto.clear();
        this.salasEnemigoResuelto.clear();
        this.eventLog.clear();
        this.combatLog.clear();

        appendEvent("Partida UI iniciada para " + heroe.getNombre() + ".");
        appendEvent("Mazmorra: " + mazmorra.getNombre() + " (Tema: " + temaActual.getNombreTema() + ").");

        eventManager.notificar(new GameEvent(EventType.JUEGO_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("tema", temaActual.getNombreTema()));
    }

    private void advanceRoom() {
        if (!heroe.estaVivo()) {
            registrarMensajeSistema("No puedes avanzar: el heroe esta derrotado.");
            return;
        }

        if (hasActiveEnemy()) {
            registrarMensajeSistema("No puedes avanzar mientras haya un combate activo.");
            return;
        }

        if (salaActual >= mazmorra.getSalas().size() - 1) {
            registrarMensajeSistema("Ya estas en la ultima sala de la mazmorra.");
            return;
        }

        salaActual++;
        pantallaActiva = "exploration";

        Room room = getSalaActualRoom();
        appendEvent("Avanzas a la sala " + (salaActual + 1) + ": " + room.getNombre());

        eventManager.notificar(new GameEvent(EventType.SALA_ENTRAR)
            .agregarDato("sala", salaActual + 1)
            .agregarDato("nombre", room.getNombre()));

        if (room.tieneEnemigo() && !salasEnemigoResuelto.contains(salaActual)) {
            iniciarCombate(false);
        } else if (random.nextInt(100) < 25) {
            iniciarCombate(true);
        }
    }

    private void searchTreasure() {
        if (!heroe.estaVivo()) {
            registrarMensajeSistema("No puedes buscar tesoros: el heroe esta derrotado.");
            return;
        }

        if (hasActiveEnemy()) {
            registrarMensajeSistema("No puedes buscar tesoros durante combate.");
            return;
        }

        if (salasTesoroResuelto.contains(salaActual)) {
            registrarMensajeSistema("Esta sala ya fue explorada.");
            return;
        }

        salasTesoroResuelto.add(salaActual);

        Room room = getSalaActualRoom();
        boolean hayDrop = room.tieneTesoro() || random.nextInt(100) < 45;

        if (!hayDrop) {
            appendEvent("Exploraste la sala pero no encontraste ningun tesoro.");
            return;
        }

        SimpleItem loot = random.nextInt(100) < 30
            ? temaActual.crearTesoroRaro()
            : temaActual.crearTesoroComun();

        try {
            inventario.agregar(loot);
            oroAcumulado += loot.getValorTotal();
            selectedItemIndex = getInventoryItems().size() - 1;

            appendEvent("Tesoro obtenido: " + loot.getNombre() + " (+" + loot.getValorTotal() + " oro).");
            eventManager.notificar(new GameEvent(EventType.TESORO_ENCONTRADO)
                .agregarDato("item", loot.getNombre())
                .agregarDato("oro", loot.getValorTotal()));
        } catch (RuntimeException ex) {
            appendEvent("Encontraste " + loot.getNombre() + ", pero el inventario esta lleno.");
        }
    }

    private void openInventory() {
        pantallaActiva = "inventory";
        clampSelectedItemIndex();
        appendEvent("Inventario abierto.");
    }

    private void closeInventory() {
        pantallaActiva = hasActiveEnemy() ? "combat" : "exploration";
    }

    private void saveGame(JsonObject payload) {
        int slot = parseInt(payload, "slot", 1);
        slot = Math.max(1, Math.min(3, slot));

        String nombreArchivo = "Slot_" + slot;
        GameMemento memento = crearMementoSesion(pantallaActiva);

        try {
            caretaker.guardarEnMemoria(memento);
            caretaker.guardarEnDisco(memento, nombreArchivo);
            appendEvent("Partida guardada en " + nombreArchivo + ".save");

            eventManager.notificar(new GameEvent(EventType.JUEGO_GUARDADO)
                .agregarDato("archivo", nombreArchivo)
                .agregarDato("sala", salaActual + 1));
        } catch (RuntimeException ex) {
            registrarMensajeSistema("No se pudo guardar: " + ex.getMessage());
        }
    }

    private void forceCombat() {
        if (!heroe.estaVivo()) {
            registrarMensajeSistema("No puedes iniciar combate: el heroe esta derrotado.");
            return;
        }
        iniciarCombate(true);
    }

    private void attack() {
        if (!prepararTurnoJugador()) {
            return;
        }

        AttackCommand attack = new AttackCommand(heroe, enemigoActual);
        if (!attack.canExecute()) {
            registrarMensajeSistema("No se puede ejecutar el ataque ahora.");
            return;
        }

        commandInvoker.ejecutarComando(attack);
        appendCombat(heroe.getNombre() + " ataca e inflige " + attack.getDanioAplicado() + " de dano.");

        eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", heroe.getNombre())
            .agregarDato("defensor", enemigoActual.getNombre())
            .agregarDato("danio", attack.getDanioAplicado()));

        if (!enemigoActual.estaVivo()) {
            resolverVictoriaCombate();
            return;
        }

        ejecutarTurnoEnemigo();
    }

    private void defend() {
        if (!prepararTurnoJugador()) {
            return;
        }

        DefendCommand defend = new DefendCommand(heroe);
        if (!defend.canExecute()) {
            registrarMensajeSistema("No se puede activar defensa en este momento.");
            return;
        }

        commandInvoker.ejecutarComando(defend);
        defensaHeroeActiva = true;
        appendCombat("Te preparas para defender el proximo ataque.");

        ejecutarTurnoEnemigo();
    }

    private void useItem(JsonObject payload) {
        if (!heroe.estaVivo()) {
            registrarMensajeSistema("No puedes usar objetos: el heroe esta derrotado.");
            return;
        }

        if (hasActiveEnemy() && !prepararTurnoJugador()) {
            return;
        }

        int payloadIndex = parseInt(payload, "itemIndex", selectedItemIndex);
        SimpleItem item = getInventoryItemByIndex(payloadIndex);

        if (item == null) {
            registrarMensajeSistema("Selecciona un objeto valido para usar.");
            return;
        }

        selectedItemIndex = payloadIndex;

        String nombreNormalizado = normalize(item.getNombre());
        if (nombreNormalizado.contains("poci")) {
            usarPocion(item);
        } else if (nombreNormalizado.contains("antid")) {
            usarAntidoto(item);
        } else {
            registrarMensajeSistema("El objeto " + item.getNombre() + " no tiene uso activo inmediato.");
            return;
        }

        if (hasActiveEnemy()) {
            ejecutarTurnoEnemigo();
        }
    }

    private void useSkill(JsonObject payload) {
        if (!prepararTurnoJugador()) {
            return;
        }

        String skillName = parseString(payload, "skill", "Golpe Especial");
        SkillCommand skill = new SkillCommand(heroe, skillName, enemigoActual);

        if (!skill.canExecute()) {
            registrarMensajeSistema("No se puede ejecutar la habilidad en este momento.");
            return;
        }

        commandInvoker.ejecutarComando(skill);

        int danio = 35 + Math.max(0, heroe.getNivel() - 1) * 5;
        enemigoActual.recibirDanio(danio);

        appendCombat(heroe.getNombre() + " usa " + skillName + " e inflige " + danio + " de dano.");

        eventManager.notificar(new GameEvent(EventType.ACCION_REALIZADA)
            .agregarDato("personaje", heroe.getNombre())
            .agregarDato("accion", "habilidad")
            .agregarDato("nombre", skillName)
            .agregarDato("danio", danio));

        if (!enemigoActual.estaVivo()) {
            resolverVictoriaCombate();
            return;
        }

        ejecutarTurnoEnemigo();
    }

    private boolean prepararTurnoJugador() {
        if (!hasActiveEnemy()) {
            iniciarCombate(false);
            if (!hasActiveEnemy()) {
                registrarMensajeSistema("No hay un enemigo activo en esta sala.");
                return false;
            }
        }

        pantallaActiva = "combat";

        aplicarVenenoHeroeInicioTurno();
        if (!heroe.estaVivo()) {
            manejarDerrota();
            return false;
        }

        return true;
    }

    private void iniciarCombate(boolean forzado) {
        if (hasActiveEnemy()) {
            pantallaActiva = "combat";
            return;
        }

        Room room = getSalaActualRoom();

        if (!forzado && salasEnemigoResuelto.contains(salaActual)) {
            registrarMensajeSistema("Esta sala ya fue despejada.");
            return;
        }

        boolean esJefe = salaActual == mazmorra.getSalas().size() - 1;
        Personaje enemigo;

        if (esJefe) {
            enemigo = temaActual.crearJefe();
        } else if (forzado || room.tieneEnemigo() || random.nextInt(100) < 60) {
            enemigo = random.nextInt(100) < 70
                ? temaActual.crearEnemigoBasico()
                : temaActual.crearEnemigoMedio();
        } else {
            appendEvent("No hay enemigos en esta sala por ahora.");
            return;
        }

        enemigo.setExperienciaOtorgada(Math.max(20, enemigo.getVida() * 2));
        enemigoActual = enemigo;

        AIStrategy estrategia = esJefe ? new AggressiveStrategy() : new RandomStrategy();
        enemyAI = new AIController(enemigoActual, estrategia);

        defensaHeroeActiva = false;
        pantallaActiva = "combat";

        appendCombat("Combate iniciado contra " + enemigoActual.getNombre() + ".");

        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("enemigo", enemigoActual.getNombre())
            .agregarDato("sala", salaActual + 1));
    }

    private void ejecutarTurnoEnemigo() {
        if (!hasActiveEnemy()) {
            return;
        }

        actualizarEstrategiaEnemiga();

        Command accionEnemiga = enemyAI.decidirAccion(List.of(heroe));
        commandInvoker.ejecutarComando(accionEnemiga);

        if (accionEnemiga instanceof AttackCommand ataque) {
            int danioBruto = ataque.getDanioAplicado();
            int mitigado = 0;

            if (defensaHeroeActiva) {
                mitigado = Math.max(1, danioBruto / 2);
                heroe.curar(mitigado);
                defensaHeroeActiva = false;
            }

            int danioFinal = Math.max(0, danioBruto - mitigado);
            appendCombat(enemigoActual.getNombre() + " ataca. Dano recibido: " + danioFinal + ".");

            if (mitigado > 0) {
                appendCombat("Tu defensa mitigo " + mitigado + " puntos de dano.");
            }

            aplicarVenenoPorAtaqueEnemigo();

            eventManager.notificar(new GameEvent(EventType.DANIO_RECIBIDO)
                .agregarDato("personaje", heroe.getNombre())
                .agregarDato("danio", danioFinal));
        } else if (accionEnemiga instanceof DefendCommand) {
            appendCombat(enemigoActual.getNombre() + " adopta postura defensiva.");
        }

        if (!heroe.estaVivo()) {
            manejarDerrota();
        }
    }

    private void actualizarEstrategiaEnemiga() {
        if (!hasActiveEnemy() || enemyAI == null) {
            return;
        }

        int hp = enemigoActual.getVida();
        AIStrategy nueva;

        if (hp > 70) {
            nueva = new AggressiveStrategy();
        } else if (hp > 35) {
            nueva = new RandomStrategy();
        } else {
            nueva = new DefensiveStrategy();
        }

        if (!enemyAI.getEstrategia().getNombreEstrategia().equals(nueva.getNombreEstrategia())) {
            enemyAI.setEstrategia(nueva);
            appendCombat("El enemigo cambia a estrategia " + nueva.getNombreEstrategia() + ".");
        }
    }

    private void resolverVictoriaCombate() {
        if (enemigoActual == null) {
            return;
        }

        String enemigoNombre = enemigoActual.getNombre();
        int xpGanada = Math.max(20, enemigoActual.getExperienciaOtorgada());

        LevelUpCommand levelUpCommand = new LevelUpCommand(heroe, xpGanada);
        if (levelUpCommand.canExecute()) {
            commandInvoker.ejecutarComando(levelUpCommand);
        }

        enemigosDerrotados++;
        salasEnemigoResuelto.add(salaActual);

        appendCombat("Derrotaste a " + enemigoNombre + ". Ganaste " + xpGanada + " XP.");

        if (levelUpCommand.getNivelesGanados() > 0) {
            appendCombat("Subiste " + levelUpCommand.getNivelesGanados() + " nivel(es). Nivel actual: "
                + heroe.getNivel() + ".");
        }

        entregarRecompensaPostCombate();

        enemigoActual = null;
        enemyAI = null;
        defensaHeroeActiva = false;
        pantallaActiva = "exploration";

        eventManager.notificar(new GameEvent(EventType.COMBATE_FINALIZADO)
            .agregarDato("ganador", heroe.getNombre())
            .agregarDato("enemigosDerrotados", enemigosDerrotados));
    }

    private void entregarRecompensaPostCombate() {
        SimpleItem loot = random.nextBoolean() ? temaActual.crearTesoroRaro() : temaActual.crearTesoroComun();

        try {
            inventario.agregar(loot);
            oroAcumulado += loot.getValorTotal();
            selectedItemIndex = getInventoryItems().size() - 1;
            appendEvent("Recompensa de combate: " + loot.getNombre() + " (+" + loot.getValorTotal() + " oro).");

            eventManager.notificar(new GameEvent(EventType.ITEM_RECOGIDO)
                .agregarDato("item", loot.getNombre())
                .agregarDato("origen", "combate"));
        } catch (RuntimeException ex) {
            appendEvent("Recompensa no recogida por inventario lleno: " + loot.getNombre() + ".");
        }
    }

    private void manejarDerrota() {
        pantallaActiva = "combat";
        appendCombat("Has sido derrotado. El combate ha terminado.");
        appendEvent("Game Over en sala " + (salaActual + 1) + ".");

        eventManager.notificar(new GameEvent(EventType.JUEGO_TERMINADO)
            .agregarDato("resultado", "Derrota")
            .agregarDato("sala", salaActual + 1));
    }

    private void usarPocion(SimpleItem pocion) {
        UseItemCommand useItemCommand = new UseItemCommand(heroe, pocion, heroe);
        if (!useItemCommand.canExecute()) {
            registrarMensajeSistema("No se pudo usar la pocion seleccionada.");
            return;
        }

        commandInvoker.ejecutarComando(useItemCommand);

        int hpAntes = heroe.getVida();
        heroe.curar(50);
        inventario.remover(pocion);

        appendEvent("Usaste " + pocion.getNombre() + " (HP " + hpAntes + " -> " + heroe.getVida() + ").");
        appendCombat("Consumiste una pocion y recuperaste vida.");

        eventManager.notificar(new GameEvent(EventType.ITEM_USADO)
            .agregarDato("usuario", heroe.getNombre())
            .agregarDato("item", pocion.getNombre()));
    }

    private void usarAntidoto(SimpleItem antidoto) {
        UseItemCommand useItemCommand = new UseItemCommand(heroe, antidoto, heroe);
        if (!useItemCommand.canExecute()) {
            registrarMensajeSistema("No se pudo usar el antidoto seleccionado.");
            return;
        }

        commandInvoker.ejecutarComando(useItemCommand);
        inventario.remover(antidoto);

        if (turnosVenenoHeroe > 0) {
            turnosVenenoHeroe = 0;
            danioVenenoHeroe = 0;
            appendEvent("Antidoto aplicado. El veneno fue removido.");
            appendCombat("Estado alterado eliminado: veneno.");
        } else {
            appendEvent("Usaste un antidoto, pero no estabas envenenado.");
        }

        eventManager.notificar(new GameEvent(EventType.ITEM_USADO)
            .agregarDato("usuario", heroe.getNombre())
            .agregarDato("item", antidoto.getNombre()));
    }

    private void aplicarVenenoHeroeInicioTurno() {
        if (turnosVenenoHeroe <= 0 || danioVenenoHeroe <= 0) {
            return;
        }

        heroe.recibirDanio(danioVenenoHeroe);
        turnosVenenoHeroe--;

        appendCombat("Veneno activo: recibes " + danioVenenoHeroe
            + " de dano (" + turnosVenenoHeroe + " turnos restantes).");

        eventManager.notificar(new GameEvent(EventType.EFECTO_APLICADO)
            .agregarDato("personaje", heroe.getNombre())
            .agregarDato("efecto", "VENENO")
            .agregarDato("duracion", turnosVenenoHeroe));
    }

    private void aplicarVenenoPorAtaqueEnemigo() {
        if (!"poison".equals(getThemeKey())) {
            return;
        }

        if (random.nextInt(100) >= 35) {
            return;
        }

        turnosVenenoHeroe = 3;
        danioVenenoHeroe = 4;
        appendCombat("Has sido envenenado. Busca un antidoto.");
    }

    private GameMemento crearMementoSesion(String estadoActual) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (SimpleItem item : getInventoryItems()) {
            Map<String, Object> data = new HashMap<>();
            data.put("nombre", item.getNombre());
            data.put("descripcion", item.getDescripcion());
            data.put("tipo", item.getTipo());
            data.put("valor", item.getValorTotal());
            data.put("peso", item.getPesoTotal());
            items.add(data);
        }

        return new GameMemento.Builder()
            .nombreJugador(heroe.getNombre())
            .nivelActual(Math.max(1, heroe.getNivel()))
            .salaActual(salaActual + 1)
            .agregarEstadoPersonaje("vida", heroe.getVida())
            .agregarEstadoPersonaje("experiencia", heroe.getExperiencia())
            .agregarEstadoPersonaje("enemigosDerrotados", enemigosDerrotados)
            .agregarEstadoPersonaje("oroAcumulado", oroAcumulado)
            .agregarEstadoPersonaje("venenoTurnos", turnosVenenoHeroe)
            .agregarEstadoPersonaje("venenoDanio", danioVenenoHeroe)
            .agregarEstadoInventario("items", items)
            .agregarEstadoMazmorra("tema", temaActual.getNombreTema())
            .agregarEstadoMazmorra("estadoActual", estadoActual)
            .agregarEstadoMazmorra("salaActualIndex", salaActual)
            .build();
    }

    private void clampSelectedItemIndex() {
        List<SimpleItem> items = getInventoryItems();
        if (items.isEmpty()) {
            selectedItemIndex = -1;
            return;
        }

        if (selectedItemIndex < 0 || selectedItemIndex >= items.size()) {
            selectedItemIndex = 0;
        }
    }

    private boolean hasConsumableInInventory() {
        for (SimpleItem item : getInventoryItems()) {
            if (isConsumable(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSelectedItemConsumable() {
        SimpleItem item = getInventoryItemByIndex(selectedItemIndex);
        return item != null && isConsumable(item);
    }

    private boolean isConsumable(SimpleItem item) {
        String tipo = normalize(item.getTipo());
        String nombre = normalize(item.getNombre());
        return tipo.contains("consum") || nombre.contains("poci") || nombre.contains("antid");
    }

    private SimpleItem getInventoryItemByIndex(int index) {
        List<SimpleItem> items = getInventoryItems();
        if (index < 0 || index >= items.size()) {
            return null;
        }
        return items.get(index);
    }

    private int parseInt(JsonObject payload, String field, int defaultValue) {
        if (payload == null) {
            return defaultValue;
        }

        JsonElement element = payload.get(field);
        if (element == null || element.isJsonNull()) {
            return defaultValue;
        }

        try {
            return element.getAsInt();
        } catch (RuntimeException ignored) {
            try {
                return Integer.parseInt(element.getAsString());
            } catch (RuntimeException ignoredAgain) {
                return defaultValue;
            }
        }
    }

    private String parseString(JsonObject payload, String field, String defaultValue) {
        if (payload == null) {
            return defaultValue;
        }

        JsonElement element = payload.get(field);
        if (element == null || element.isJsonNull()) {
            return defaultValue;
        }

        try {
            String value = element.getAsString();
            return value == null || value.isBlank() ? defaultValue : value;
        } catch (RuntimeException ignored) {
            return defaultValue;
        }
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    private static String themeNameToKey(String themeName) {
        String normalized = normalize(themeName);
        if (normalized.contains("hielo")) {
            return "ice";
        }
        if (normalized.contains("veneno")) {
            return "poison";
        }
        if (normalized.contains("oscur")) {
            return "dark";
        }
        return "fire";
    }

    private void appendEvent(String message) {
        appendBounded(eventLog, timestamp() + " " + message, EVENT_LOG_LIMIT);
    }

    private void appendCombat(String message) {
        appendBounded(combatLog, timestamp() + " " + message, COMBAT_LOG_LIMIT);
    }

    private static void appendBounded(List<String> target, String value, int maxSize) {
        target.add(value);
        while (target.size() > maxSize) {
            target.remove(0);
        }
    }

    private static String timestamp() {
        return LocalDateTime.now().format(TIME_FMT);
    }
}
