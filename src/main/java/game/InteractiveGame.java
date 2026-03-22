package game;

import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.ai.strategy.AggressiveStrategy;
import game.ai.strategy.DefensiveStrategy;
import game.ai.strategy.RandomStrategy;
import game.command.actions.*;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.*;
import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.DungeonBuilder;
import game.dungeon.builder.DungeonDirector;
import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import game.dungeon.theme.*;
import game.effects.status.PoisonEffect;
import game.events.observer.*;
import game.items.model.ContainerItem;
import game.items.model.ItemComponent;
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;
import game.state.game.GameState;
import game.state.game.GameStateContext;
import game.state.game.runtime.GameRuntimeCoordinator;
import game.state.game.runtime.MenuRuntimeState;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Juego interactivo completo que integra los patrones del proyecto.
 */
public class InteractiveGame implements GameRuntimeCoordinator {

    private static final class InputClosedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private static final class FlowState implements GameState {
        private final String nombre;

        private FlowState(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public void manejarEntrada(String entrada) {
            // Estado de flujo informativo para el loop principal.
        }

        @Override
        public void actualizar() {
            // Sin lógica de actualización en este adaptador.
        }

        @Override
        public void render() {
            // El render real lo maneja InteractiveGame.
        }

        @Override
        public void onEnter() {
            // Estado de flujo silencioso para evitar ruido en UI.
        }

        @Override
        public void onExit() {
            // Estado de flujo silencioso para evitar ruido en UI.
        }

        @Override
        public String getNombre() {
            return nombre;
        }
    }

    private final Scanner scanner;
    private final Random random;

    // Componentes del juego
    private Personaje heroe;
    private Dungeon mazmorra;
    private DungeonThemeFactory temaActual;
    private ContainerItem inventario;
    private int salaActual;
    private int enemigosDerrota;
    private int oroAcumulado;

    // Patrones de comportamiento
    private final EventManager eventManager;
    private final CombatLogger combatLogger;
    private final StatisticsTracker statistics;
    private final CommandInvoker commandInvoker;
    private final GameOriginator originator;
    private final GameCaretaker caretaker;

    // Estado del juego
    private boolean juegoActivo;
    private boolean enCombate;
    private final boolean vistaDebugIA;
    private final List<String> historialIA;
    private GameStateContext runtimeContext;
    private GameStateContext flowContext;
    private boolean defensaHeroeActiva;
    private int turnosVenenoHeroe;
    private int danioVenenoHeroe;
    private boolean solicitarNuevaPartida;
    private boolean solicitarReanudarExploracion;

    public InteractiveGame() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.salaActual = 0;
        this.enemigosDerrota = 0;
        this.oroAcumulado = 0;
        this.juegoActivo = true;
        this.enCombate = false;
        this.vistaDebugIA = true;
        this.historialIA = new ArrayList<>();
        this.defensaHeroeActiva = false;
        this.turnosVenenoHeroe = 0;
        this.danioVenenoHeroe = 0;
        this.solicitarNuevaPartida = false;
        this.solicitarReanudarExploracion = false;

        this.eventManager = EventManager.getInstance();
        this.eventManager.limpiar();
        this.combatLogger = new CombatLogger(true);
        this.statistics = new StatisticsTracker();
        this.eventManager.suscribir(combatLogger);
        this.eventManager.suscribir(statistics);

        this.commandInvoker = new CommandInvoker();

        this.originator = new GameOriginator("Jugador");
        this.caretaker = new GameCaretaker("./game-saves/");
    }

    public static void main(String[] args) {
        InteractiveGame juego = new InteractiveGame();
        juego.iniciar();
    }

    private void iniciar() {
        mostrarTitulo();
        this.runtimeContext = new GameStateContext(new MenuRuntimeState(this));

        try {
            while (juegoActivo && runtimeContext.isEjecutando()) {
                runtimeContext.actualizar();
            }
        } catch (InputClosedException e) {
            System.out.println("\nEntrada finalizada. Cerrando juego de forma segura.");
        }

        scanner.close();
    }

    private boolean configurarNuevaPartida() {
        cambiarEstadoFlujo("Preparacion");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎮 NUEVA PARTIDA");
        System.out.println("=".repeat(60));

        this.heroe = elegirHeroe();
        if (heroe == null) {
            return false;
        }

        crearInventarioInicial();

        this.temaActual = elegirTema();
        if (temaActual == null) {
            return false;
        }

        construirMazmorra();

        this.salaActual = 0;
        this.enemigosDerrota = 0;
        this.oroAcumulado = 0;
        this.turnosVenenoHeroe = 0;
        this.danioVenenoHeroe = 0;
        this.defensaHeroeActiva = false;

        eventManager.notificar(new GameEvent(EventType.JUEGO_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("tema", temaActual.getNombreTema()));

        return true;
    }

    @Override
    public void cambiarEstadoRuntime(GameState nuevoEstado) {
        runtimeContext.cambiarEstado(nuevoEstado);
    }

    @Override
    public void cambiarEstadoFlujoRuntime(String nombreEstado) {
        cambiarEstadoFlujo(nombreEstado);
    }

    @Override
    public int leerOpcionMenuPrincipal() {
        mostrarMenuPrincipal();
        return leerOpcion(1, 4);
    }

    @Override
    public boolean configurarNuevaPartidaRuntime() {
        return configurarNuevaPartida();
    }

    @Override
    public boolean cargarPartidaDesdeMenuRuntime() {
        return cargarPartida(false);
    }

    @Override
    public void mostrarEstadisticasRuntime() {
        mostrarEstadisticas();
    }

    @Override
    public void ejecutarAventuraRuntime() {
        explorarMazmorra();
    }

    @Override
    public boolean consumirSolicitudNuevaPartida() {
        if (!solicitarNuevaPartida) {
            return false;
        }
        solicitarNuevaPartida = false;
        return true;
    }

    @Override
    public boolean consumirSolicitudReanudarExploracion() {
        if (!solicitarReanudarExploracion) {
            return false;
        }
        solicitarReanudarExploracion = false;
        return true;
    }

    @Override
    public void detenerJuegoRuntime() {
        System.out.println("\n¡Gracias por jugar! 🎮");
        juegoActivo = false;
        runtimeContext.detener();
    }

    @Override
    public boolean estaJuegoActivoRuntime() {
        return juegoActivo;
    }

    private Personaje elegirHeroe() {
        System.out.println("\n⚔️  ELIGE TU HÉROE:");
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ 1) 🗡️  Guerrero  HP:150  ATK:25  Perfil: Resistente      │");
        System.out.println("│ 2) 🔮 Mago      HP:100  ATK:35  Perfil: Daño alto       │");
        System.out.println("│ 3) 🏹  Arquero   HP:120  ATK:28  Perfil: Balanceado      │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println("4. Volver");

        int opcion = leerOpcion(1, 4);
        if (opcion == 4) {
            return null;
        }

        PersonajeFactory factory = switch (opcion) {
            case 1 -> new GuerreroFactory(150, 25);
            case 2 -> new MagoFactory(100, 35);
            case 3 -> new ArqueroFactory(120, 28);
            default -> new GuerreroFactory(150, 25);
        };

        System.out.print("\n¿Nombre de tu héroe? ");
        String nombre = leerLineaRequerida().trim();
        if (nombre.isEmpty()) {
            nombre = "Héroe";
        }

        Personaje personaje = factory.crearPersonaje(nombre);
        System.out.println("\n✅ " + personaje.getNombre() + " está listo para la aventura!");

        return personaje;
    }

    private void crearInventarioInicial() {
        inventario = new ContainerItem("Mochila", "Tu inventario principal", 20, 2);

        inventario.agregar(new SimpleItem("Poción de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        inventario.agregar(new SimpleItem("Poción de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        inventario.agregar(new SimpleItem("Antídoto", "Cura veneno", "Consumible", 30, 1));

        System.out.println("🎒 Inventario inicial creado con 3 items");
    }

    private DungeonThemeFactory elegirTema() {
        System.out.println("\n🏰 ELIGE EL TEMA DE LA MAZMORRA:");
        System.out.println("1. 🔥 Fuego    - Enemigos ardientes y dragones");
        System.out.println("2. ❄️  Hielo    - Criaturas congeladas y yetis");
        System.out.println("3. 🌑 Oscuridad - Sombras y no-muertos");
        System.out.println("4. ☠️  Veneno   - Arañas y plantas venenosas");
        System.out.println("5. Volver");

        int opcion = leerOpcion(1, 5);
        if (opcion == 5) {
            return null;
        }

        DungeonThemeFactory tema = switch (opcion) {
            case 1 -> new FireThemeFactory();
            case 2 -> new IceThemeFactory();
            case 3 -> new DarkThemeFactory();
            case 4 -> new PoisonThemeFactory();
            default -> new FireThemeFactory();
        };

        System.out.println("\n✅ Tema seleccionado: " + tema.getNombreTema());
        return tema;
    }

    private void construirMazmorra() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        DungeonDirector director = new DungeonDirector(builder);

        String nombreTema = temaActual.getNombreTema();
        mazmorra = switch (nombreTema) {
            case "Fuego" -> director.construirMazmorraFuego();
            case "Hielo" -> director.construirMazmorraBasica();
            case "Oscuridad" -> director.construirMazmorraBasica();
            case "Veneno" -> director.construirMazmorraBasica();
            default -> director.construirMazmorraBasica();
        };

        System.out.println("\n🏗️  Mazmorra construida: " + mazmorra.getNombre());
        System.out.println("   Salas: " + mazmorra.getSalas().size());
        System.out.println("   Dificultad: " + mazmorra.getNivelDificultad());
    }

    private void explorarMazmorra() {
        cambiarEstadoFlujo("Exploracion");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🗺️  EXPLORANDO: " + mazmorra.getNombre());
        System.out.println("=".repeat(60));

        while (heroe.estaVivo() && salaActual < mazmorra.getSalas().size()) {
            Room sala = mazmorra.getSalas().get(salaActual);

            mostrarMapaMazmorra();
            mostrarHudExploracion();

            System.out.println("\n📍 Sala " + (salaActual + 1) + "/" + mazmorra.getSalas().size() + ": " + sala.getNombre());
            System.out.println("   " + sala.getDescripcion());
            System.out.println("   Dificultad: " + sala.getDificultad());

            System.out.println("\n¿Qué deseas hacer?");
            System.out.println("1. [→] Avanzar a la siguiente sala");
            System.out.println("2. [E] Explorar sala / Buscar tesoro");
            System.out.println("3. [I] Abrir inventario");
            System.out.println("4. [G] Guardar partida");
            System.out.println("5. [C] Forzar combate (si hay enemigo)");
            System.out.println("6. Volver al menú principal");

            int opcion = leerOpcion(1, 6);

            switch (opcion) {
                case 1 -> avanzarSala();
                case 2 -> buscarTesoro();
                case 3 -> abrirInventario();
                case 4 -> guardarPartida();
                case 5 -> encontrarEnemigo();
                case 6 -> {
                    return;
                }
            }
        }

        if (heroe.estaVivo()) {
            victoria();
        } else {
            derrota();
        }
    }

    private void avanzarSala() {
        salaActual++;

        if (salaActual >= mazmorra.getSalas().size()) {
            System.out.println("\n🎉 ¡Has llegado al final de la mazmorra!");
            return;
        }

        if (random.nextInt(100) < 70) {
            encontrarEnemigo();
        } else {
            System.out.println("\n✅ Sala despejada. Puedes continuar.");
        }
    }

    private void buscarTesoro() {
        System.out.println("\n🔍 Buscando tesoro...");

        int probabilidad = random.nextInt(100);
        SimpleItem tesoro;

        if (probabilidad < 30) {
            tesoro = temaActual.crearTesoroRaro();
            System.out.println("✨ ¡Tesoro RARO encontrado!");
        } else if (probabilidad < 70) {
            tesoro = temaActual.crearTesoroComun();
            System.out.println("💰 Tesoro común encontrado.");
        } else {
            System.out.println("❌ No encontraste nada.");
            return;
        }

        System.out.println("   → " + tesoro.getNombre() + " (Valor: " + tesoro.getValorTotal() + ")");
        inventario.agregar(tesoro);
        oroAcumulado += tesoro.getValorTotal();
        System.out.println("   Agregado al inventario.");
    }

    private void abrirInventario() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎒 INVENTARIO");
        System.out.println("=".repeat(60));

        System.out.println(inventario.mostrarDetalle());
        System.out.println("\nValor total: " + inventario.getValorTotal() + " oro");
        System.out.println("Peso total: " + inventario.getPesoTotal() + " kg");

        if (!enCombate) {
            System.out.println("\n(Presiona Enter para continuar)");
            esperarEnterSiDisponible();
        }
    }

    private void usarPocion() {
        SimpleItem pocion = buscarConsumiblePorNombre("poci");
        if (pocion == null) {
            System.out.println("❌ No tienes pociones de vida disponibles.");
            return;
        }

        int hpAntes = heroe.getVida();
        heroe.curar(50);
        inventario.remover(pocion);

        System.out.println("💊 Usando Poción de Vida (+50 HP)");
        System.out.println("   HP antes: " + hpAntes);
        System.out.println("   HP después: " + heroe.getVida());
    }

    private SimpleItem buscarConsumiblePorNombre(String textoParcial) {
        String criterio = normalizarTexto(textoParcial);
        for (var item : inventario.obtenerItems()) {
            if (item instanceof SimpleItem simpleItem) {
                String nombre = normalizarTexto(simpleItem.getNombre());
                String tipo = normalizarTexto(simpleItem.getTipo());
                if (tipo.contains("consum") && nombre.contains(criterio)) {
                    return simpleItem;
                }
            }
        }
        return null;
    }

    private String normalizarTexto(String texto) {
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }

    private void guardarPartida() {
        System.out.println("\n💾 Guardando partida...");

        GameMemento memento = crearMementoSesion("Exploracion");
        caretaker.guardarEnMemoria(memento);

        System.out.println("Selecciona un slot para guardar:");
        System.out.println("1. Slot 1");
        System.out.println("2. Slot 2");
        System.out.println("3. Slot 3");
        System.out.println("4. Cancelar");

        int opcion = leerOpcion(1, 4);
        if (opcion == 4) {
            System.out.println("❌ Guardado cancelado.");
            return;
        }

        String nombreArchivo = "Slot_" + opcion;

        try {
            caretaker.guardarEnDisco(memento, nombreArchivo);
            System.out.println("✅ Partida guardada en: " + nombreArchivo + ".save");
            eventManager.notificar(new GameEvent(EventType.JUEGO_GUARDADO)
                .agregarDato("tipo", "manual")
                .agregarDato("archivo", nombreArchivo));
        } catch (RuntimeException e) {
            System.out.println("❌ Error al guardar partida: " + e.getMessage());
        }
    }

    private boolean cargarPartida(boolean pausarAlFinal) {
        System.out.println("\n📂 CARGAR PARTIDA");
        List<String> guardados = caretaker.listarGuardados();
        
        System.out.println("Selecciona el slot a cargar:");
        System.out.println("1. Slot 1 " + (guardados.contains("Slot_1") ? "(Disponible)" : "(Vacío)"));
        System.out.println("2. Slot 2 " + (guardados.contains("Slot_2") ? "(Disponible)" : "(Vacío)"));
        System.out.println("3. Slot 3 " + (guardados.contains("Slot_3") ? "(Disponible)" : "(Vacío)"));
        System.out.println("4. Cancelar");

        int opcion = leerOpcion(1, 4);
        if (opcion == 4) {
            System.out.println("❌ Carga cancelada.");
            if (pausarAlFinal) esperarEnterSiDisponible();
            return false;
        }

        String nombreArchivo = "Slot_" + opcion;
        if (!guardados.contains(nombreArchivo)) {
            System.out.println("❌ El " + nombreArchivo + " está vacío.");
            if (pausarAlFinal) esperarEnterSiDisponible();
            return false;
        }

        boolean cargada = false;
        try {
            GameMemento memento = caretaker.cargarDesdeDisco(nombreArchivo);
            originator.restaurar(memento);
            restaurarSesionCompleta(memento);
            cargada = true;
            System.out.println("✅ Partida cargada");
            System.out.println("   Jugador: " + originator.getNombreJugador());
            System.out.println("   Nivel: " + originator.getNivelActual());
            System.out.println("   Sala: " + originator.getSalaActual());
            System.out.println("   Vida: " + originator.getVidaJugador());
            System.out.println("\nEstado restaurado en la sesión actual.");
        } catch (RuntimeException e) {
            System.out.println("❌ No se pudo cargar la partida: " + e.getMessage());
        }

        if (pausarAlFinal) {
            System.out.println("\n(Presiona Enter)");
            esperarEnterSiDisponible();
        }

        return cargada;
    }

    private void encontrarEnemigo() {
        Personaje enemigo;
        boolean esJefe = salaActual == mazmorra.getSalas().size() - 1;

        if (esJefe) {
            enemigo = temaActual.crearJefe();
            System.out.println("\n⚠️  ¡JEFE FINAL APARECIÓ!");
        } else {
            enemigo = (random.nextInt(100) < 70)
                ? temaActual.crearEnemigoBasico()
                : temaActual.crearEnemigoMedio();
            System.out.println("\n⚔️  ¡ENEMIGO APARECIÓ!");
        }

        System.out.println("   " + enemigo.getNombre() + " (HP: " + enemigo.getVida() + ")");

        // Asignar XP basada en la vida del enemigo
        enemigo.setExperienciaOtorgada(enemigo.getVida() * 2);

        if (random.nextInt(100) < 50 && temaActual.getNombreTema().toLowerCase().contains("veneno")) {
            enemigo = new PoisonEffect(enemigo, 3, 4);
            System.out.println("   ☠️  El enemigo está envenenado!");
        }

        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("enemigo", enemigo.getNombre())
            .agregarDato("vidaHeroe", heroe.getVida())
            .agregarDato("vidaEnemigo", enemigo.getVida())
            .agregarDato("estrategia", esJefe ? "Agresiva" : "Aleatoria"));

        iniciarCombate(enemigo, esJefe);
    }

    private void iniciarCombate(Personaje enemigo, boolean esJefe) {
        cambiarEstadoFlujo("Combate");
        enCombate = true;
        historialIA.clear();
        defensaHeroeActiva = false;

        AIStrategy estrategiaInicial = esJefe ? new AggressiveStrategy() : new RandomStrategy();
        AIController enemyAI = new AIController(enemigo, estrategiaInicial);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("⚔️  COMBATE");
        System.out.println("=".repeat(60));

        int turno = 1;
        while (heroe.estaVivo() && enemigo.estaVivo()) {
            aplicarVenenoHeroeInicioTurno();
            if (!heroe.estaVivo()) {
                break;
            }

            System.out.println("\n--- TURNO " + turno + " ---");
            System.out.println("Tu HP: " + heroe.getVida() + " | " +
                enemigo.getNombre() + " HP: " + enemigo.getVida());

            if (turnosVenenoHeroe > 0) {
                System.out.println("Estado: ☠️ Envenenado (" + turnosVenenoHeroe + " turnos restantes)");
            }

            actualizarEstrategiaEnemiga(enemyAI, enemigo);
            if (vistaDebugIA) {
                mostrarVistaDebugIA(enemyAI, enemigo, turno);
            }

            System.out.println("\nTu turno:");
            System.out.println("1. Atacar");
            System.out.println("2. Defender");
            System.out.println("3. Usar objeto");
            System.out.println("4. Usar habilidad");

            int accion = leerOpcion(1, 4);

            switch (accion) {
                case 1 -> {
                    AttackCommand attackCommand = new AttackCommand(heroe, enemigo);
                    commandInvoker.ejecutarComando(attackCommand);

                    System.out.println("\n⚔️  " + heroe.getNombre() + " ataca!");
                    System.out.println("   Daño: " + attackCommand.getDanioAplicado());
                    System.out.println("   HP enemigo: " + enemigo.getVida());

                    eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
                        .agregarDato("atacante", heroe.getNombre())
                        .agregarDato("defensor", enemigo.getNombre())
                        .agregarDato("danio", attackCommand.getDanioAplicado())
                        .agregarDato("vidaRestante", enemigo.getVida())
                        .agregarDato("ronda", turno));
                }
                case 2 -> {
                    DefendCommand defendCommand = new DefendCommand(heroe);
                    commandInvoker.ejecutarComando(defendCommand);
                    defensaHeroeActiva = true;
                    System.out.println("\n🛡️  " + heroe.getNombre() + " se defiende! (reducción activa para el próximo golpe)");
                }
                case 3 -> {
                    usarConsumibleEnCombate();
                }
                case 4 -> {
                    String nombreHabilidad = "Golpe Especial";
                    SkillCommand skillCommand = new SkillCommand(heroe, nombreHabilidad, enemigo);
                    commandInvoker.ejecutarComando(skillCommand);

                    int danioHabilidad = 35;
                    enemigo.recibirDanio(danioHabilidad);
                    System.out.println("\n✨ " + heroe.getNombre() + " usa " + nombreHabilidad + "!");
                    System.out.println("   Daño de habilidad: " + danioHabilidad);
                    System.out.println("   HP enemigo: " + enemigo.getVida());

                    eventManager.notificar(new GameEvent(EventType.ACCION_REALIZADA)
                        .agregarDato("personaje", heroe.getNombre())
                        .agregarDato("accion", "habilidad")
                        .agregarDato("nombre", nombreHabilidad)
                        .agregarDato("ronda", turno));
                }
            }

            if (!enemigo.estaVivo()) {
                break;
            }

            System.out.println("\nTurno del enemigo:");
            Command accionEnemiga = enemyAI.decidirAccion(List.of(heroe));
            commandInvoker.ejecutarComando(accionEnemiga);

            String nombreEstrategia = enemyAI.getEstrategia().getNombreEstrategia();
            historialIA.add("T" + turno + " " + nombreEstrategia + " -> " + accionEnemiga.getDescription());

            if (accionEnemiga instanceof AttackCommand ataqueCommand) {
                if (defensaHeroeActiva) {
                    int mitigado = Math.max(1, ataqueCommand.getDanioAplicado() / 2);
                    heroe.curar(mitigado);
                    defensaHeroeActiva = false;
                    System.out.println("🛡️  Defensa activa: daño mitigado en " + mitigado + " puntos.");
                }

                aplicarVenenoPorAtaqueEnemigo();

                System.out.println("💥 " + enemigo.getNombre() + " ataca!");
                System.out.println("   Estrategia: " + nombreEstrategia);
                System.out.println("   Daño recibido: " + ataqueCommand.getDanioAplicado());
                System.out.println("   Tu HP: " + heroe.getVida());
            } else if (accionEnemiga instanceof DefendCommand) {
                System.out.println("🛡️  " + enemigo.getNombre() + " adopta postura defensiva.");
            }

            turno++;
        }

        enCombate = false;
        cambiarEstadoFlujo("Exploracion");

        if (heroe.estaVivo()) {
            System.out.println("\n🎉 ¡VICTORIA!");
            enemigosDerrota++;
            
            int xpGanada = enemigo.getExperienciaOtorgada();
            System.out.println("⭐ Has ganado " + xpGanada + " XP!");
            heroe.ganarExperiencia(xpGanada);
            
            int nivelActual = heroe.getNivel();
            int xpRequerida = nivelActual * 100;
            if (heroe.getExperiencia() >= xpRequerida) {
                heroe.subirNivel();
                heroe.ganarExperiencia(-xpRequerida);
                System.out.println("🆙 ¡SUBISTE DE NIVEL! Ahora eres nivel " + heroe.getNivel());
                System.out.println("   HP Máximo incrementado. HP restaurado completamente: " + heroe.getVida());
            }

            mostrarPantallaTesoro();
            guardarCheckpointAutomatico();

            eventManager.notificar(new GameEvent(EventType.COMBATE_FINALIZADO)
                .agregarDato("ganador", heroe.getNombre()));

            System.out.println("\n(Presiona Enter)");
            esperarEnterSiDisponible();
            return;
        }

        System.out.println("\n💀 HAS SIDO DERROTADO");
        derrota();
    }

    private void victoria() {
        cambiarEstadoFlujo("Victoria");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎊 ¡¡¡VICTORIA!!! 🎊");
        System.out.println("=".repeat(60));
        System.out.println("Has completado la mazmorra: " + mazmorra.getNombre());
        System.out.println("Enemigos derrotados: " + enemigosDerrota);
        System.out.println("HP restante: " + heroe.getVida());

        eventManager.notificar(new GameEvent(EventType.JUEGO_TERMINADO)
            .agregarDato("resultado", "Victoria"));

        mostrarEstadisticasFinales();

        System.out.println("\n(Presiona Enter)");
        esperarEnterSiDisponible();
    }

    private void derrota() {
        cambiarEstadoFlujo("GameOver");
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💀 GAME OVER 💀");
        System.out.println("=".repeat(60));
        System.out.println(heroe.getNombre() + " ha caído en combate...");
        System.out.println("Salas exploradas: " + salaActual + "/" + mazmorra.getSalas().size());
        System.out.println("Enemigos derrotados: " + enemigosDerrota);
        System.out.println("Oro acumulado: " + oroAcumulado);

        eventManager.notificar(new GameEvent(EventType.JUEGO_TERMINADO)
            .agregarDato("resultado", "Derrota"));

        mostrarEstadisticasFinales();

        if (!mostrarOpcionesGameOver()) {
            System.out.println("\n(Presiona Enter)");
            esperarEnterSiDisponible();
        }
    }

    private void mostrarEstadisticas() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 ESTADÍSTICAS GLOBALES");
        System.out.println("=".repeat(60));
        System.out.println("Ataques totales: " + statistics.getAtaquesTotales());
        System.out.println("Daño causado: " + statistics.getDanioTotalCausado());
        System.out.println("Combates realizados: " + statistics.getCombatesRealizados());
        System.out.println("Personajes derrotados: " + statistics.getPersonajesDerrotados());
        System.out.println("\n(Presiona Enter)");
        esperarEnterSiDisponible();
    }

    private void mostrarEstadisticasFinales() {
        System.out.println("\n📊 Estadísticas de esta partida:");
        System.out.println("   Ataques realizados: " + statistics.getAtaquesTotales());
        System.out.println("   Daño total causado: " + statistics.getDanioTotalCausado());
        System.out.println("   Comandos ejecutados: " + commandInvoker.getHistorial().size());
    }

    private void mostrarTitulo() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          🎮 DUNGEON CRAWLER INTERACTIVO 🎮              ║");
        System.out.println("║                                                          ║");
        System.out.println("║      Proyecto de Patrones de Diseño - UTP 2026         ║");
        System.out.println("║           10 Patrones Completamente Integrados          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("=".repeat(60));
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("MENÚ PRINCIPAL");
        System.out.println("─".repeat(60));
        System.out.println("1. 🎮 Nueva Partida");
        System.out.println("2. 📂 Cargar Partida");
        System.out.println("3. 📊 Ver Estadísticas");
        System.out.println("4. 🚪 Salir");
        System.out.print("\nSelecciona opción: ");
    }

    private void mostrarMapaMazmorra() {
        int total = mazmorra.getSalas().size();
        StringBuilder mapa = new StringBuilder();
        mapa.append("\nMapa: ");

        for (int i = 0; i < total; i++) {
            if (i == salaActual) {
                mapa.append("[⚔]");
            } else if (i < salaActual) {
                mapa.append("[·]");
            } else if (i == total - 1) {
                mapa.append("[💀]");
            } else {
                mapa.append("[?]");
            }

            if (i < total - 1) {
                mapa.append("-");
            }
        }

        System.out.println(mapa);
    }

    private void mostrarHudExploracion() {
        System.out.println("Estado héroe: " + heroe.getNombre() +
            " | Nivel: " + heroe.getNivel() + " (XP: " + heroe.getExperiencia() + "/" + (heroe.getNivel()*100) + ")" +
            " | HP: " + heroe.getVida() + "/" + heroe.getVidaMaxima() +
            " | Oro: " + oroAcumulado +
            " | Enemigos derrotados: " + enemigosDerrota +
            " | Estado: " + estadoFlujoActual());
    }

    private void mostrarVistaDebugIA(AIController enemyAI, Personaje enemigo, int turno) {
        System.out.println("\n[DEBUG IA] " + enemigo.getNombre() +
            " | HP: " + enemigo.getVida() +
            " | Estrategia: " + enemyAI.getEstrategia().getNombreEstrategia());

        int desde = Math.max(0, historialIA.size() - 4);
        if (historialIA.isEmpty()) {
            System.out.println("[DEBUG IA] Historial: (sin decisiones previas)");
        } else {
            System.out.println("[DEBUG IA] Historial reciente:");
            for (int i = desde; i < historialIA.size(); i++) {
                System.out.println("  - " + historialIA.get(i));
            }
        }

        System.out.println("[DEBUG IA] Turno actual: " + turno);
    }

    private void actualizarEstrategiaEnemiga(AIController enemyAI, Personaje enemigo) {
        int hp = enemigo.getVida();
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
            eventManager.notificar(new GameEvent(EventType.ESTADO_CAMBIADO)
                .agregarDato("tipo", "estrategia")
                .agregarDato("nuevaEstrategia", nueva.getNombreEstrategia()));
        }
    }

    private void mostrarPantallaTesoro() {
        SimpleItem loot = random.nextBoolean() ? temaActual.crearTesoroRaro() : temaActual.crearTesoroComun();
        inventario.agregar(loot);
        oroAcumulado += loot.getValorTotal();

        System.out.println("\n🏆 SALA DE TESORO");
        System.out.println("Enemigo derrotado -> recompensa obtenida");
        System.out.println("Loot: " + loot.getNombre() + " (valor: " + loot.getValorTotal() + ")");
        System.out.println("Resumen: salas=" + (salaActual + 1) + "/" + mazmorra.getSalas().size() +
            " | enemigos=" + enemigosDerrota +
            " | oro=" + oroAcumulado +
            " | hp=" + heroe.getVida());
    }

    private void guardarCheckpointAutomatico() {
        GameMemento memento = crearMementoSesion("PostCombate");
        caretaker.guardarEnMemoria(memento);
        try {
            caretaker.guardarEnDisco(memento, "checkpoint-auto");
            System.out.println("✓ Checkpoint automático guardado (checkpoint-auto.save)");
            eventManager.notificar(new GameEvent(EventType.JUEGO_GUARDADO)
                .agregarDato("tipo", "checkpoint-auto")
                .agregarDato("sala", salaActual + 1));
        } catch (RuntimeException e) {
            System.out.println("⚠ No se pudo guardar checkpoint automático: " + e.getMessage());
        }
    }

    private GameMemento crearMementoSesion(String estado) {
        String nombreJugador = heroe != null ? heroe.getNombre() : originator.getNombreJugador();
        int vidaActual = heroe != null ? heroe.getVida() : originator.getVidaJugador();
        int experienciaActual = heroe != null ? heroe.getExperiencia() : 0;

        GameMemento.Builder builder = new GameMemento.Builder()
            .nombreJugador(nombreJugador)
            .nivelActual(Math.max(1, heroe != null ? heroe.getNivel() : originator.getNivelActual()))
            .salaActual(salaActual + 1)
            .agregarEstadoPersonaje("vida", vidaActual)
            .agregarEstadoPersonaje("experiencia", experienciaActual)
            .agregarEstadoPersonaje("claseHeroe", obtenerClaseHeroe())
            .agregarEstadoPersonaje("enemigosDerrotados", enemigosDerrota)
            .agregarEstadoPersonaje("oroAcumulado", oroAcumulado)
            .agregarEstadoPersonaje("venenoTurnos", turnosVenenoHeroe)
            .agregarEstadoPersonaje("venenoDanio", danioVenenoHeroe)
            .agregarEstadoPersonaje("defensaActiva", defensaHeroeActiva)
            .agregarEstadoInventario("items", serializarInventario())
            .agregarEstadoMazmorra("tema", temaActual != null ? temaActual.getNombreTema() : "Fuego")
            .agregarEstadoMazmorra("estadoActual", estado)
            .agregarEstadoMazmorra("salaActualIndex", salaActual)
            .agregarEstadoMazmorra("tamanoMazmorra", mazmorra != null ? mazmorra.getSalas().size() : 0);

        return builder.build();
    }

    private List<Map<String, Object>> serializarInventario() {
        List<Map<String, Object>> snapshot = new ArrayList<>();

        if (inventario == null) {
            return snapshot;
        }

        for (ItemComponent item : inventario.obtenerItems()) {
            recolectarItemsSimples(item, snapshot);
        }

        return snapshot;
    }

    private void recolectarItemsSimples(ItemComponent item, List<Map<String, Object>> snapshot) {
        if (item instanceof SimpleItem simple) {
            Map<String, Object> data = new HashMap<>();
            data.put("nombre", simple.getNombre());
            data.put("descripcion", simple.getDescripcion());
            data.put("tipo", simple.getTipo());
            data.put("valor", simple.getValorTotal());
            data.put("peso", simple.getPesoTotal());
            snapshot.add(data);
            return;
        }

        if (item instanceof ContainerItem contenedor) {
            for (ItemComponent hijo : contenedor.obtenerItems()) {
                recolectarItemsSimples(hijo, snapshot);
            }
        }
    }

    private void restaurarSesionCompleta(GameMemento memento) {
        Map<String, Object> estadoPersonaje = memento.getEstadoPersonaje();
        Map<String, Object> estadoInventario = memento.getEstadoInventario();
        Map<String, Object> estadoMazmorra = memento.getEstadoMazmorra();

        String nombreJugador = memento.getNombreJugador();
        String claseHeroe = valorString(estadoPersonaje, "claseHeroe", "Guerrero");
        this.heroe = crearHeroeSegunClase(claseHeroe, nombreJugador);

        int nivelGuardado = memento.getNivelActual();
        for (int i = 1; i < nivelGuardado; i++) {
            this.heroe.subirNivel();
        }
        int expGuardada = valorEntero(estadoPersonaje, "experiencia", 0);
        this.heroe.ganarExperiencia(expGuardada);

        int vidaObjetivo = valorEntero(estadoPersonaje, "vida", heroe.getVidaMaxima());
        ajustarVida(heroe, vidaObjetivo);

        this.temaActual = crearTemaDesdeNombre(valorString(estadoMazmorra, "tema", "Fuego"));
        construirMazmorra();

        int salaGuardada = valorEntero(estadoMazmorra, "salaActualIndex", Math.max(0, memento.getSalaActual() - 1));
        int ultimaSala = Math.max(0, mazmorra.getSalas().size() - 1);
        this.salaActual = Math.min(Math.max(0, salaGuardada), ultimaSala);

        this.enemigosDerrota = valorEntero(estadoPersonaje, "enemigosDerrotados", 0);
        this.oroAcumulado = valorEntero(estadoPersonaje, "oroAcumulado", 0);
        this.turnosVenenoHeroe = Math.max(0, valorEntero(estadoPersonaje, "venenoTurnos", 0));
        this.danioVenenoHeroe = Math.max(0, valorEntero(estadoPersonaje, "venenoDanio", 0));
        this.defensaHeroeActiva = valorBooleano(estadoPersonaje, "defensaActiva", false);

        restaurarInventario(estadoInventario.get("items"));
        cambiarEstadoFlujo("Exploracion");

        eventManager.notificar(new GameEvent(EventType.JUEGO_CARGADO)
            .agregarDato("jugador", memento.getNombreJugador())
            .agregarDato("sala", memento.getSalaActual())
            .agregarDato("tema", temaActual.getNombreTema()));
    }

    private void restaurarInventario(Object inventarioSerializado) {
        this.inventario = new ContainerItem("Mochila", "Tu inventario principal", 20, 2);

        if (!(inventarioSerializado instanceof List<?> lista)) {
            return;
        }

        for (Object entrada : lista) {
            if (!(entrada instanceof Map<?, ?> map)) {
                continue;
            }

            String nombre = valorStringMap(map, "nombre", "Objeto");
            String descripcion = valorStringMap(map, "descripcion", "Sin descripción");
            String tipo = valorStringMap(map, "tipo", "Consumible");
            int valor = Math.max(0, valorEnteroMap(map, "valor", 0));
            int peso = Math.max(0, valorEnteroMap(map, "peso", 1));

            try {
                inventario.agregar(new SimpleItem(nombre, descripcion, tipo, valor, peso));
            } catch (RuntimeException ignored) {
                // Si excede la capacidad, se descarta el resto para mantener consistencia.
                break;
            }
        }
    }

    private Personaje crearHeroeSegunClase(String claseHeroe, String nombre) {
        return switch (claseHeroe.toLowerCase()) {
            case "mago" -> new MagoFactory(100, 35).crearPersonaje(nombre);
            case "arquero" -> new ArqueroFactory(120, 28).crearPersonaje(nombre);
            default -> new GuerreroFactory(150, 25).crearPersonaje(nombre);
        };
    }

    private String obtenerClaseHeroe() {
        if (heroe == null) {
            return "Guerrero";
        }

        String tipo = heroe.getClass().getSimpleName().toLowerCase();
        if (tipo.contains("mago")) {
            return "Mago";
        }
        if (tipo.contains("arquero")) {
            return "Arquero";
        }
        return "Guerrero";
    }

    private DungeonThemeFactory crearTemaDesdeNombre(String tema) {
        return switch (tema.toLowerCase()) {
            case "hielo" -> new IceThemeFactory();
            case "oscuridad" -> new DarkThemeFactory();
            case "veneno" -> new PoisonThemeFactory();
            default -> new FireThemeFactory();
        };
    }

    private void ajustarVida(Personaje personaje, int vidaObjetivo) {
        int delta = vidaObjetivo - personaje.getVida();
        if (delta > 0) {
            personaje.curar(delta);
        } else if (delta < 0) {
            personaje.recibirDanio(-delta);
        }
    }

    private int valorEntero(Map<String, Object> mapa, String clave, int porDefecto) {
        Object valor = mapa.get(clave);
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        if (valor instanceof String texto) {
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException ignored) {
                return porDefecto;
            }
        }
        return porDefecto;
    }

    private String valorString(Map<String, Object> mapa, String clave, String porDefecto) {
        Object valor = mapa.get(clave);
        return valor != null ? String.valueOf(valor) : porDefecto;
    }

    private boolean valorBooleano(Map<String, Object> mapa, String clave, boolean porDefecto) {
        Object valor = mapa.get(clave);
        if (valor instanceof Boolean bool) {
            return bool;
        }
        if (valor instanceof String texto) {
            return Boolean.parseBoolean(texto);
        }
        return porDefecto;
    }

    private int valorEnteroMap(Map<?, ?> mapa, String clave, int porDefecto) {
        Object valor = mapa.get(clave);
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        if (valor instanceof String texto) {
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException ignored) {
                return porDefecto;
            }
        }
        return porDefecto;
    }

    private String valorStringMap(Map<?, ?> mapa, String clave, String porDefecto) {
        Object valor = mapa.get(clave);
        return valor != null ? String.valueOf(valor) : porDefecto;
    }

    private void usarConsumibleEnCombate() {
        SimpleItem pocion = buscarConsumiblePorNombre("poci");
        SimpleItem antidoto = buscarConsumiblePorNombre("antid");

        if (pocion == null && antidoto == null) {
            System.out.println("\n❌ No tienes consumibles utilizables.");
            return;
        }

        System.out.println("\nConsumibles disponibles:");
        System.out.println("1. Poción de Vida " + (pocion == null ? "(no disponible)" : ""));
        System.out.println("2. Antídoto " + (antidoto == null ? "(no disponible)" : ""));
        System.out.println("3. Cancelar");

        int opcion = leerOpcion(1, 3);
        switch (opcion) {
            case 1 -> {
                if (pocion == null) {
                    System.out.println("❌ No tienes pociones.");
                    return;
                }

                UseItemCommand useItemCommand = new UseItemCommand(heroe, pocion, heroe);
                commandInvoker.ejecutarComando(useItemCommand);
                usarPocion();

                eventManager.notificar(new GameEvent(EventType.ITEM_USADO)
                    .agregarDato("usuario", heroe.getNombre())
                    .agregarDato("item", pocion.getNombre()));
            }
            case 2 -> {
                if (antidoto == null) {
                    System.out.println("❌ No tienes antídotos.");
                    return;
                }

                UseItemCommand useItemCommand = new UseItemCommand(heroe, antidoto, heroe);
                commandInvoker.ejecutarComando(useItemCommand);
                usarAntidoto(antidoto);

                eventManager.notificar(new GameEvent(EventType.ITEM_USADO)
                    .agregarDato("usuario", heroe.getNombre())
                    .agregarDato("item", antidoto.getNombre()));
            }
            case 3 -> System.out.println("Acción cancelada.");
        }
    }

    private void usarAntidoto(SimpleItem antidoto) {
        inventario.remover(antidoto);

        if (turnosVenenoHeroe <= 0) {
            System.out.println("💊 Usaste antídoto, pero no estabas envenenado.");
            return;
        }

        turnosVenenoHeroe = 0;
        danioVenenoHeroe = 0;
        System.out.println("💊 Antídoto aplicado: veneno removido.");
    }

    private void aplicarVenenoHeroeInicioTurno() {
        if (turnosVenenoHeroe <= 0 || danioVenenoHeroe <= 0) {
            return;
        }

        heroe.recibirDanio(danioVenenoHeroe);
        turnosVenenoHeroe--;
        System.out.println("☠️  Veneno: recibes " + danioVenenoHeroe + " de daño. HP actual: " + heroe.getVida());

        eventManager.notificar(new GameEvent(EventType.EFECTO_APLICADO)
            .agregarDato("personaje", heroe.getNombre())
            .agregarDato("efecto", "VENENO")
            .agregarDato("duracion", turnosVenenoHeroe));
    }

    private void aplicarVenenoPorAtaqueEnemigo() {
        if (temaActual == null || !"Veneno".equalsIgnoreCase(temaActual.getNombreTema())) {
            return;
        }

        if (random.nextInt(100) >= 35) {
            return;
        }

        turnosVenenoHeroe = 3;
        danioVenenoHeroe = 4;
        System.out.println("☠️  ¡Has sido envenenado! Usa antídoto para curarte.");

        eventManager.notificar(new GameEvent(EventType.EFECTO_APLICADO)
            .agregarDato("personaje", heroe.getNombre())
            .agregarDato("efecto", "VENENO")
            .agregarDato("duracion", turnosVenenoHeroe));
    }

    private void cambiarEstadoFlujo(String nombreEstado) {
        if (flowContext == null) {
            flowContext = new GameStateContext(new FlowState(nombreEstado));
        } else {
            String actual = flowContext.getEstadoActual().getNombre();
            if (actual.equals(nombreEstado)) {
                return;
            }
            flowContext.cambiarEstado(new FlowState(nombreEstado));
        }

        eventManager.notificar(new GameEvent(EventType.ESTADO_CAMBIADO)
            .agregarDato("tipo", "flujo")
            .agregarDato("estado", nombreEstado));
    }

    private String estadoFlujoActual() {
        if (flowContext == null || flowContext.getEstadoActual() == null) {
            return "N/A";
        }
        return flowContext.getEstadoActual().getNombre();
    }

    private boolean mostrarOpcionesGameOver() {
        System.out.println("\nOpciones:");
        System.out.println("1. [R] Cargar último checkpoint en memoria");
        System.out.println("2. [M] Volver al menú principal");
        System.out.println("3. [N] Nueva partida");

        int opcion = leerOpcion(1, 3);
        switch (opcion) {
            case 1 -> {
                if (caretaker.getCantidadMementos() == 0) {
                    System.out.println("No hay checkpoint en memoria para restaurar.");
                    return false;
                }
                GameMemento ultimo = caretaker.obtenerUltimoMemento();
                originator.restaurar(ultimo);
                restaurarSesionCompleta(ultimo);
                System.out.println("✅ Checkpoint restaurado. Continuas en exploración.");
                return true;
            }
            case 2 -> {
                System.out.println("Regresando al menú principal...");
                return true;
            }
            case 3 -> {
                System.out.println("Iniciando nueva partida...");
                solicitarNuevaPartida = true;
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private String resolverNombreArchivo(String entrada, List<String> guardados) {
        if (entrada == null) {
            return null;
        }
        if (entrada.isEmpty()) {
            return guardados.get(0);
        }

        try {
            int indice = Integer.parseInt(entrada);
            if (indice >= 1 && indice <= guardados.size()) {
                return guardados.get(indice - 1);
            }
            return null;
        } catch (NumberFormatException e) {
            return entrada;
        }
    }

    private String leerLineaRequerida() {
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine();
    }

    private void esperarEnterSiDisponible() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private int leerOpcion(int min, int max) {
        while (true) {
            try {
                System.out.print("> ");
                String input = leerLineaRequerida().trim();
                int opcion = Integer.parseInt(input);

                if (opcion >= min && opcion <= max) {
                    return opcion;
                }
                System.out.println("❌ Opción inválida. Elige entre " + min + " y " + max);
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida. Ingresa un número.");
            } catch (NoSuchElementException e) {
                throw new InputClosedException();
            }
        }
    }
}
