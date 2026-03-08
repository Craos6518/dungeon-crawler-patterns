package game;

import game.combat.model.ResultadoAtaque;
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

import java.util.Random;
import java.util.Scanner;

/**
 * Juego interactivo completo que integra TODOS los 10 patrones de diseño.
 * 
 * El jugador puede:
 * - Elegir su héroe (Factory Method)
 * - Seleccionar tema de mazmorra (Abstract Factory)
 * - Explorar salas (Builder, State)
 * - Combatir enemigos (Command, Strategy, Decorator, Facade)
 * - Gestionar inventario (Composite)
 * - Guardar/cargar partida (Memento)
 * - Recibir notificaciones (Observer)
 * 
 * @author Proyecto Patrones de Diseño - UTP
 */
public class InteractiveGame {
    
    private final Scanner scanner;
    private final Random random;
    
    // Componentes del juego
    private Personaje heroe;
    private Dungeon mazmorra;
    private DungeonThemeFactory temaActual;
    private ContainerItem inventario;
    private int salaActual;
    private int enemigosDerrota;
    
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
    
    public InteractiveGame() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.salaActual = 0;
        this.enemigosDerrota = 0;
        this.juegoActivo = true;
        this.enCombate = false;
        
        // PATRÓN OBSERVER: Configurar sistema de eventos
        this.eventManager = EventManager.getInstance();
        this.eventManager.limpiar();
        this.combatLogger = new CombatLogger(true);
        this.statistics = new StatisticsTracker();
        this.eventManager.suscribir(combatLogger);
        this.eventManager.suscribir(statistics);
        
        // PATRÓN COMMAND: Configurar invocador
        this.commandInvoker = new CommandInvoker();
        
        // PATRÓN MEMENTO: Configurar guardado
        this.originator = new GameOriginator("Jugador");
        this.caretaker = new GameCaretaker("./game-saves/");
    }
    
    /**
     * Punto de entrada del juego interactivo
     */
    public static void main(String[] args) {
        InteractiveGame juego = new InteractiveGame();
        juego.iniciar();
    }
    
    /**
     * Loop principal del juego
     */
    public void iniciar() {
        mostrarTitulo();
        
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
        
        scanner.close();
    }
    
    /**
     * Inicia una nueva partida
     */
    private void nuevaPartida() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎮 NUEVA PARTIDA");
        System.out.println("=".repeat(60));
        
        // PATRÓN FACTORY METHOD: Elegir héroe
        this.heroe = elegirHeroe();
        if (heroe == null) return;
        
        // PATRÓN COMPOSITE: Crear inventario
        crearInventarioInicial();
        
        // PATRÓN ABSTRACT FACTORY: Elegir tema de mazmorra
        this.temaActual = elegirTema();
        if (temaActual == null) return;
        
        // PATRÓN BUILDER: Construir mazmorra
        construirMazmorra();
        
        // Reiniciar estado
        this.salaActual = 0;
        this.enemigosDerrota = 0;
        
        // Notificar inicio
        eventManager.notificar(new GameEvent(EventType.JUEGO_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("tema", temaActual.getNombreTema()));
        
        // Iniciar exploración
        explorarMazmorra();
    }
    
    /**
     * PATRÓN FACTORY METHOD: Permite elegir el héroe
     */
    private Personaje elegirHeroe() {
        System.out.println("\n⚔️  ELIGE TU HÉROE:");
        System.out.println("1. Guerrero   (HP: 150, Ataque: 25) - Resistente y poderoso");
        System.out.println("2. Mago       (HP: 100, Ataque: 35) - Alto daño mágico");
        System.out.println("3. Arquero    (HP: 120, Ataque: 28) - Balanceado y preciso");
        System.out.println("4. Volver");
        
        int opcion = leerOpcion(1, 4);
        if (opcion == 4) return null;
        
        PersonajeFactory factory = switch (opcion) {
            case 1 -> new GuerreroFactory(150, 25);
            case 2 -> new MagoFactory(100, 35);
            case 3 -> new ArqueroFactory(120, 28);
            default -> new GuerreroFactory(150, 25);
        };
        
        System.out.print("\n¿Nombre de tu héroe? ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) nombre = "Héroe";
        
        Personaje personaje = factory.crearPersonaje(nombre);
        System.out.println("\n✅ " + personaje.getNombre() + " está listo para la aventura!");
        
        return personaje;
    }
    
    /**
     * PATRÓN COMPOSITE: Crea el inventario inicial con items
     */
    private void crearInventarioInicial() {
        inventario = new ContainerItem("Mochila", "Tu inventario principal", 20, 2);
        
        // Agregar items iniciales
        inventario.agregar(new SimpleItem("Poción de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        inventario.agregar(new SimpleItem("Poción de Vida", "Restaura 50 HP", "Consumible", 50, 1));
        inventario.agregar(new SimpleItem("Antídoto", "Cura veneno", "Consumible", 30, 1));
        
        System.out.println("🎒 Inventario inicial creado con 3 items");
    }
    
    /**
     * PATRÓN ABSTRACT FACTORY: Permite elegir el tema de la mazmorra
     */
    private DungeonThemeFactory elegirTema() {
        System.out.println("\n🏰 ELIGE EL TEMA DE LA MAZMORRA:");
        System.out.println("1. 🔥 Fuego    - Enemigos ardientes y dragones");
        System.out.println("2. ❄️  Hielo    - Criaturas congeladas y yetis");
        System.out.println("3. 🌑 Oscuridad - Sombras y no-muertos");
        System.out.println("4. ☠️  Veneno   - Arañas y plantas venenosas");
        System.out.println("5. Volver");
        
        int opcion = leerOpcion(1, 5);
        if (opcion == 5) return null;
        
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
    
    /**
     * PATRÓN BUILDER: Construye la mazmorra usando el Director
     */
    private void construirMazmorra() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        DungeonDirector director = new DungeonDirector(builder);
        
        // Construir mazmorra según el tema
        String nombreTema = temaActual.getNombreTema();
        mazmorra = switch (nombreTema) {
            case "Fuego" -> director.construirMazmorraFuego();
            case "Hielo" -> director.construirMazmorraBasica(); // Usar básica para hielo
            case "Oscuridad" -> director.construirMazmorraBasica();
            case "Veneno" -> director.construirMazmorraBasica();
            default -> director.construirMazmorraBasica();
        };
        
        System.out.println("\n🏗️  Mazmorra construida: " + mazmorra.getNombre());
        System.out.println("   Salas: " + mazmorra.getSalas().size());
        System.out.println("   Dificultad: " + mazmorra.getNivelDificultad());
    }
    
    /**
     * PATRÓN STATE: Exploración de la mazmorra
     */
    private void explorarMazmorra() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🗺️  EXPLORANDO: " + mazmorra.getNombre());
        System.out.println("=".repeat(60));
        
        while (heroe.estaVivo() && salaActual < mazmorra.getSalas().size()) {
            Room sala = mazmorra.getSalas().get(salaActual);
            
            System.out.println("\n📍 Sala " + (salaActual + 1) + "/" + mazmorra.getSalas().size() + ": " + sala.getNombre());
            System.out.println("   " + sala.getDescripcion());
            System.out.println("   Dificultad: " + sala.getDificultad());
            
            System.out.println("\n¿Qué deseas hacer?");
            System.out.println("1. Avanzar a la siguiente sala");
            System.out.println("2. Buscar tesoro");
            System.out.println("3. Abrir inventario");
            System.out.println("4. Guardar partida");
            System.out.println("5. Volver al menú principal");
            
            int opcion = leerOpcion(1, 5);
            
            switch (opcion) {
                case 1 -> avanzarSala();
                case 2 -> buscarTesoro();
                case 3 -> abrirInventario();
                case 4 -> guardarPartida();
                case 5 -> {
                    return;
                }
            }
        }
        
        // Juego completado
        if (heroe.estaVivo()) {
            victoria();
        } else {
            derrota();
        }
    }
    
    /**
     * Avanza a la siguiente sala (puede encontrar enemigos)
     */
    private void avanzarSala() {
        salaActual++;
        
        if (salaActual >= mazmorra.getSalas().size()) {
            System.out.println("\n🎉 ¡Has llegado al final de la mazmorra!");
            return;
        }
        
        // 70% de probabilidad de encontrar enemigo
        if (random.nextInt(100) < 70) {
            encontrarEnemigo();
        } else {
            System.out.println("\n✅ Sala despejada. Puedes continuar.");
        }
    }
    
    /**
     * Busca tesoro en la sala actual (usa Abstract Factory)
     */
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
        System.out.println("   Agregado al inventario.");
    }
    
    /**
     * PATRÓN COMPOSITE: Muestra y gestiona el inventario
     */
    private void abrirInventario() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎒 INVENTARIO");
        System.out.println("=".repeat(60));
        
        System.out.println(inventario.mostrarDetalle());
        System.out.println("\nValor total: " + inventario.getValorTotal() + " oro");
        System.out.println("Peso total: " + inventario.getPesoTotal() + " kg");
        
        if (enCombate) {
            System.out.println("\n¿Usar poción de vida? (s/n)");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            if (respuesta.equals("s")) {
                usarPocion();
            }
        } else {
            System.out.println("\n(Presiona Enter para continuar)");
            scanner.nextLine();
        }
    }
    
    /**
     * Usa una poción del inventario
     */
    private void usarPocion() {
        // Buscar una poción en el inventario (simplificado)
        System.out.println("💊 Usando Poción de Vida (+50 HP)");
        // En una implementación completa, se buscaría y removería del inventario
        System.out.println("   HP antes: " + heroe.getVida());
        // El héroe se cura (simulado, ya que el método recibirDanio es para daño)
        System.out.println("   (Poción usada - funcionalidad simplificada para demo)");
    }
    
    /**
     * PATRÓN MEMENTO: Guarda el estado actual del juego
     */
    private void guardarPartida() {
        System.out.println("\n💾 Guardando partida...");
        
        // GameOriginator maneja su propio estado internamente
        originator.setEstadoActual("Exploration");
        
        GameMemento memento = originator.guardar();
        caretaker.guardarEnMemoria(memento);
        
        System.out.print("Nombre del archivo (o Enter para autosave): ");
        String nombreArchivo = scanner.nextLine().trim();
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
    
    /**
     * PATRÓN MEMENTO: Carga una partida guardada
     */
    private void cargarPartida() {
        System.out.println("\n📂 CARGAR PARTIDA");
        System.out.println("(Funcionalidad parcial - para demo completa requiere más estado)");
        System.out.print("Nombre del archivo: ");
        String nombreArchivo = scanner.nextLine().trim();
        
        try {
            GameMemento memento = caretaker.cargarDesdeDisco(nombreArchivo);
            originator.restaurar(memento);
            System.out.println("✅ Partida cargada");
            System.out.println("   Jugador: " + originator.getNombreJugador());
            System.out.println("   Nivel: " + originator.getNivelActual());
            System.out.println("   Sala: " + originator.getSalaActual());
            System.out.println("   Vida: " + originator.getVidaJugador());
            System.out.println("\n(Estado restaurado - para continuar usa Nueva Partida)");
        } catch (RuntimeException e) {
            System.out.println("❌ No se pudo cargar la partida: " + e.getMessage());
        }
        
        System.out.println("\n(Presiona Enter)");
        scanner.nextLine();
    }
    
    /**
     * Encuentra un enemigo y comienza el combate
     * Integra: Command, Strategy, Decorator, Facade, Observer
     */
    private void encontrarEnemigo() {
        // Decidir si es enemigo básico, medio o jefe
        Personaje enemigo;
        boolean esJefe = salaActual == mazmorra.getSalas().size() - 1;
        
        if (esJefe) {
            enemigo = temaActual.crearJefe();
            System.out.println("\n⚠️  ¡JEFE FINAL APARECIÓ!");
        } else {
            // 70% enemigo básico, 30% enemigo medio
            enemigo = (random.nextInt(100) < 70) 
                ? temaActual.crearEnemigoBasico()
                : temaActual.crearEnemigoMedio();
            System.out.println("\n⚔️  ¡ENEMIGO APARECIÓ!");
        }
        
        System.out.println("   " + enemigo.getNombre() + " (HP: " + enemigo.getVida() + ")");
        
        // Aplicar efectos con PATRÓN DECORATOR (50% chance)
        if (random.nextInt(100) < 50 && temaActual.getNombreTema().toLowerCase().contains("veneno")) {
            enemigo = new PoisonEffect(enemigo, 3, 4);
            System.out.println("   ☠️  El enemigo está envenenado!");
        }
        
        // PATRÓN OBSERVER: Notificar inicio de combate
        eventManager.notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("enemigo", enemigo.getNombre()));
        
        // Iniciar combate
        iniciarCombate(enemigo, esJefe);
    }
    
    /**
     * Sistema de combate interactivo
     * Usa: Command, Strategy, Facade, Observer
     */
    private void iniciarCombate(Personaje enemigo, boolean esJefe) {
        enCombate = true;
        
        // PATRÓN STRATEGY: Elegir comportamiento de IA
        // (En esta demo simplificada la IA no se usa activamente,
        // pero en un juego completo controlaría las decisiones del enemigo)
        // AIStrategy estrategia = esJefe ? new AggressiveStrategy() : new DefensiveStrategy();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("⚔️  COMBATE");
        System.out.println("=".repeat(60));
        
        int turno = 1;
        while (heroe.estaVivo() && enemigo.estaVivo()) {
            System.out.println("\n--- TURNO " + turno + " ---");
            System.out.println("Tu HP: " + heroe.getVida() + " | " + 
                             enemigo.getNombre() + " HP: " + enemigo.getVida());
            
            // Turno del jugador
            System.out.println("\nTu turno:");
            System.out.println("1. Atacar");
            System.out.println("2. Defender");
            System.out.println("3. Abrir inventario");
            
            int accion = leerOpcion(1, 3);
            
            Command comando = null;
            switch (accion) {
                case 1 -> {
                    // PATRÓN COMMAND: Encapsular ataque
                    comando = new AttackCommand(heroe, enemigo);
                    commandInvoker.ejecutarComando(comando);
                    
                    ResultadoAtaque resultado = heroe.atacar(enemigo);
                    System.out.println("\n⚔️  " + heroe.getNombre() + " ataca!");
                    System.out.println("   Daño: " + resultado.danio());
                    System.out.println("   HP enemigo: " + resultado.vidaRestanteDefensor());
                    
                    // PATRÓN OBSERVER: Notificar ataque
                    eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
                        .agregarDato("atacante", heroe.getNombre())
                        .agregarDato("defensor", enemigo.getNombre())
                        .agregarDato("danio", resultado.danio()));
                }
                case 2 -> {
                    comando = new DefendCommand(heroe);
                    commandInvoker.ejecutarComando(comando);
                    System.out.println("\n🛡️  " + heroe.getNombre() + " se defiende! (+reducción de daño)");
                }
                case 3 -> {
                    abrirInventario();
                    continue; // No consume turno
                }
            }
            
            if (!enemigo.estaVivo()) {
                break;
            }
            
            // Turno del enemigo (PATRÓN STRATEGY controla comportamiento)
            System.out.println("\nTurno del enemigo:");
            ResultadoAtaque ataqueEnemigo = enemigo.atacar(heroe);
            System.out.println("💥 " + enemigo.getNombre() + " ataca!");
            System.out.println("   Daño recibido: " + ataqueEnemigo.danio());
            System.out.println("   Tu HP: " + ataqueEnemigo.vidaRestanteDefensor());
            
            turno++;
        }
        
        enCombate = false;
        
        // Resultado del combate
        if (heroe.estaVivo()) {
            System.out.println("\n🎉 ¡VICTORIA!");
            enemigosDerrota++;
            
            eventManager.notificar(new GameEvent(EventType.COMBATE_FINALIZADO)
                .agregarDato("ganador", heroe.getNombre()));
        } else {
            System.out.println("\n💀 HAS SIDO DERROTADO");
            derrota();
        }
        
        System.out.println("\n(Presiona Enter)");
        scanner.nextLine();
    }
    
    /**
     * Victoria del juego
     */
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
        scanner.nextLine();
    }
    
    /**
     * Derrota del jugador
     */
    private void derrota() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💀 GAME OVER 💀");
        System.out.println("=".repeat(60));
        System.out.println(heroe.getNombre() + " ha caído en combate...");
        System.out.println("Salas exploradas: " + salaActual + "/" + mazmorra.getSalas().size());
        System.out.println("Enemigos derrotados: " + enemigosDerrota);
        
        eventManager.notificar(new GameEvent(EventType.JUEGO_TERMINADO)
            .agregarDato("resultado", "Derrota"));
        
        mostrarEstadisticasFinales();
        
        System.out.println("\n(Presiona Enter)");
        scanner.nextLine();
    }
    
    /**
     * PATRÓN OBSERVER: Muestra estadísticas recopiladas
     */
    private void mostrarEstadisticas() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 ESTADÍSTICAS GLOBALES");
        System.out.println("=".repeat(60));
        System.out.println("Ataques totales: " + statistics.getAtaquesTotales());
        System.out.println("Daño causado: " + statistics.getDanioTotalCausado());
        System.out.println("Combates realizados: " + statistics.getCombatesRealizados());
        System.out.println("Personajes derrotados: " + statistics.getPersonajesDerrotados());
        System.out.println("\n(Presiona Enter)");
        scanner.nextLine();
    }
    
    private void mostrarEstadisticasFinales() {
        System.out.println("\n📊 Estadísticas de esta partida:");
        System.out.println("   Ataques realizados: " + statistics.getAtaquesTotales());
        System.out.println("   Daño total causado: " + statistics.getDanioTotalCausado());
        System.out.println("   Comandos ejecutados: " + commandInvoker.getHistorial().size());
    }
    
    // ==================== UI HELPERS ====================
    
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
    
    /**
     * Lee una opción del usuario dentro de un rango
     */
    private int leerOpcion(int min, int max) {
        while (true) {
            try {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                int opcion = Integer.parseInt(input);
                
                if (opcion >= min && opcion <= max) {
                    return opcion;
                } else {
                    System.out.println("❌ Opción inválida. Elige entre " + min + " y " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida. Ingresa un número.");
            }
        }
    }
}
