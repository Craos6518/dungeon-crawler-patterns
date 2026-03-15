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
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Juego interactivo completo que integra los patrones del proyecto.
 */
public class InteractiveGame {

    private static final class InputClosedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
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

        try {
            while (juegoActivo) {
                mostrarMenuPrincipal();
                int opcion = leerOpcion(1, 4);

                switch (opcion) {
                    case 1 -> nuevaPartida();
                    case 2 -> cargarPartida();
                    case 3 -> mostrarEstadisticas();
                    case 4 -> {
                        System.out.println("\n¡Gracias por jugar! 🎮");
                        juegoActivo = false;
                    }
                }
            }
        } catch (InputClosedException e) {
            System.out.println("\nEntrada finalizada. Cerrando juego de forma segura.");
        }

        scanner.close();
    }

    private void nuevaPartida() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎮 NUEVA PARTIDA");
        System.out.println("=".repeat(60));

        this.heroe = elegirHeroe();
        if (heroe == null) {
            return;
        }

        crearInventarioInicial();

        this.temaActual = elegirTema();
        if (temaActual == null) {
            return;
        }

        construirMazmorra();

        this.salaActual = 0;
        this.enemigosDerrota = 0;
        this.oroAcumulado = 0;

        eventManager.notificar(new GameEvent(EventType.JUEGO_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("tema", temaActual.getNombreTema()));

        explorarMazmorra();
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
        String criterio = textoParcial.toLowerCase();
        for (var item : inventario.obtenerItems()) {
            if (item instanceof SimpleItem simpleItem) {
                String nombre = simpleItem.getNombre().toLowerCase();
                String tipo = simpleItem.getTipo().toLowerCase();
                if (tipo.contains("consum") && nombre.contains(criterio)) {
                    return simpleItem;
                }
            }
        }
        return null;
    }

    private void guardarPartida() {
        System.out.println("\n💾 Guardando partida...");

        sincronizarEstadoOriginator("Exploracion");

        GameMemento memento = originator.guardar();
        caretaker.guardarEnMemoria(memento);

        System.out.print("Nombre del archivo (o Enter para autosave): ");
        String nombreArchivo = leerLineaRequerida().trim();
        if (nombreArchivo.isEmpty()) {
            nombreArchivo = "autosave";
        }

        try {
            caretaker.guardarEnDisco(memento, nombreArchivo);
            System.out.println("✅ Partida guardada: " + nombreArchivo + ".save");
        } catch (RuntimeException e) {
            System.out.println("❌ Error al guardar partida: " + e.getMessage());
        }
    }

    private void cargarPartida() {
        System.out.println("\n📂 CARGAR PARTIDA");
        List<String> guardados = caretaker.listarGuardados();
        if (guardados.isEmpty()) {
            System.out.println("No hay guardados en disco.");
            System.out.println("\n(Presiona Enter)");
            esperarEnterSiDisponible();
            return;
        }

        System.out.println("Ranuras disponibles:");
        for (int i = 0; i < guardados.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + guardados.get(i));
        }

        System.out.print("Nombre del archivo o numero de ranura: ");
        String entrada = leerLineaRequerida().trim();
        String nombreArchivo = resolverNombreArchivo(entrada, guardados);

        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            System.out.println("❌ Selección inválida. Operación cancelada.");
            System.out.println("\n(Presiona Enter)");
            esperarEnterSiDisponible();
            return;
        }

        try {
            GameMemento memento = caretaker.cargarDesdeDisco(nombreArchivo);
            originator.restaurar(memento);
            aplicarMementoAlJuego(memento);
            System.out.println("✅ Partida cargada");
            System.out.println("   Jugador: " + originator.getNombreJugador());
            System.out.println("   Nivel: " + originator.getNivelActual());
            System.out.println("   Sala: " + originator.getSalaActual());
            System.out.println("   Vida: " + originator.getVidaJugador());
            System.out.println("\nEstado restaurado en la sesión actual.");
        } catch (RuntimeException e) {
            System.out.println("❌ No se pudo cargar la partida: " + e.getMessage());
        }

        System.out.println("\n(Presiona Enter)");
        esperarEnterSiDisponible();
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

        if (random.nextInt(100) < 50 && temaActual.getNombreTema().toLowerCase().contains("veneno")) {
            enemigo = new PoisonEffect(enemigo, 3, 4);
            System.out.println("   ☠️  El enemigo está envenenado!");
        }

        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("enemigo", enemigo.getNombre()));

        iniciarCombate(enemigo, esJefe);
    }

    private void iniciarCombate(Personaje enemigo, boolean esJefe) {
        enCombate = true;
        historialIA.clear();

        AIStrategy estrategiaInicial = esJefe ? new AggressiveStrategy() : new RandomStrategy();
        AIController enemyAI = new AIController(enemigo, estrategiaInicial);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("⚔️  COMBATE");
        System.out.println("=".repeat(60));

        int turno = 1;
        while (heroe.estaVivo() && enemigo.estaVivo()) {
            System.out.println("\n--- TURNO " + turno + " ---");
            System.out.println("Tu HP: " + heroe.getVida() + " | " +
                enemigo.getNombre() + " HP: " + enemigo.getVida());

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
                        .agregarDato("danio", attackCommand.getDanioAplicado()));
                }
                case 2 -> {
                    DefendCommand defendCommand = new DefendCommand(heroe);
                    commandInvoker.ejecutarComando(defendCommand);
                    System.out.println("\n🛡️  " + heroe.getNombre() + " se defiende! (+reducción de daño)");
                }
                case 3 -> {
                    SimpleItem item = buscarConsumiblePorNombre("poci");
                    if (item == null) {
                        System.out.println("\n❌ No tienes consumibles de curación.");
                        continue;
                    }

                    UseItemCommand useItemCommand = new UseItemCommand(heroe, item, heroe);
                    commandInvoker.ejecutarComando(useItemCommand);
                    usarPocion();

                    eventManager.notificar(new GameEvent(EventType.ITEM_USADO)
                        .agregarDato("usuario", heroe.getNombre())
                        .agregarDato("item", item.getNombre()));
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
                        .agregarDato("actor", heroe.getNombre())
                        .agregarDato("accion", "habilidad")
                        .agregarDato("nombre", nombreHabilidad));
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

        if (heroe.estaVivo()) {
            System.out.println("\n🎉 ¡VICTORIA!");
            enemigosDerrota++;
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
            " | HP: " + heroe.getVida() +
            " | Oro: " + oroAcumulado +
            " | Enemigos derrotados: " + enemigosDerrota);
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
                .agregarDato("sistema", "IA")
                .agregarDato("estrategia", nueva.getNombreEstrategia()));
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
        sincronizarEstadoOriginator("PostCombate");

        GameMemento memento = originator.guardar();
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

    private void sincronizarEstadoOriginator(String estado) {
        originator.setEstadoActual(estado);
        originator.recibirDanio(Math.max(0, originator.getVidaJugador() - heroe.getVida()));
    }

    private void aplicarMementoAlJuego(GameMemento memento) {
        if (heroe != null) {
            int vidaObjetivo = originator.getVidaJugador();
            int delta = vidaObjetivo - heroe.getVida();

            if (delta > 0) {
                heroe.curar(delta);
            } else if (delta < 0) {
                heroe.recibirDanio(-delta);
            }
        }

        salaActual = Math.max(0, originator.getSalaActual() - 1);
        eventManager.notificar(new GameEvent(EventType.JUEGO_CARGADO)
            .agregarDato("jugador", memento.getNombreJugador())
            .agregarDato("sala", memento.getSalaActual()));
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
                aplicarMementoAlJuego(ultimo);
                System.out.println("✅ Checkpoint restaurado. Continuas en exploración.");
                return true;
            }
            case 2 -> {
                System.out.println("Regresando al menú principal...");
                return true;
            }
            case 3 -> {
                System.out.println("Iniciando nueva partida...");
                nuevaPartida();
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
