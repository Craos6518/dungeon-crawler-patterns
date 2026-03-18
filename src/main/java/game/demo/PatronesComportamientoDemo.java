package game.demo;

import game.ai.strategy.*;
import game.command.actions.*;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import game.events.observer.*;
import game.items.model.SimpleItem;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameMemento;
import game.persistence.memento.GameOriginator;

import java.util.List;

/**
 * Clase de demostración de los patrones de comportamiento implementados.
 * 
 * Muestra el uso de:
 * - Command (sistema de comandos)
 * - Strategy (estrategias de IA)
 * - Observer (sistema de eventos)
 * - State (gestionado en package state.game, no incluido aquí por simplicidad)
 * - Memento (guardado/carga de partidas)
 */
public class PatronesComportamientoDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("DEMOSTRACIÓN DE PATRONES DE COMPORTAMIENTO");
        System.out.println("=".repeat(60));
        System.out.println();

        demoCommand();
        System.out.println();

        demoStrategy();
        System.out.println();

        demoObserver();
        System.out.println();

        demoMemento();
    }

    /**
     * Demuestra el patrón Command con el sistema de comandos de acción
     */
    private static void demoCommand() {
        System.out.println("--- PATRÓN COMMAND: Sistema de Comandos ---");
        System.out.println();

        Personaje guerrero = new Guerrero("Arthas", 100, 20);
        Personaje enemigo = new EnemigoBasico("Orco", 50, 10);
        SimpleItem pocion = new SimpleItem("Poción de Vida", "Restaura 50 HP", "Poción", 50, 1);

        // Crear invoker para gestionar comandos
        CommandInvoker invoker = new CommandInvoker();

        // Crear y ejecutar comandos
        System.out.println("Ejecutando comandos...");

        Command ataque1 = new AttackCommand(guerrero, enemigo);
        invoker.ejecutarComando(ataque1);
        System.out.println("  → " + ataque1.getDescription());

        Command ataque2 = new AttackCommand(guerrero, enemigo);
        invoker.ejecutarComando(ataque2);
        System.out.println("  → " + ataque2.getDescription());

        Command defensa = new DefendCommand(guerrero);
        invoker.ejecutarComando(defensa);
        System.out.println("  → " + defensa.getDescription());

        Command usarItem = new UseItemCommand(guerrero, pocion, guerrero);
        invoker.ejecutarComando(usarItem);
        System.out.println("  → " + usarItem.getDescription());

        // Mostrar historial
        System.out.println();
        System.out.println(invoker.getHistorialTexto());
    }

    /**
     * Demuestra el patrón Strategy con diferentes estrategias de IA
     */
    private static void demoStrategy() {
        System.out.println("--- PATRÓN STRATEGY: Estrategias de IA ---");
        System.out.println();

        Personaje enemigo = new EnemigoBasico("Goblín", 60, 12);
        List<Personaje> heroes = List.of(
            new Guerrero("Héroe 1", 100, 20),
            new Guerrero("Héroe 2", 50, 15),
            new Guerrero("Héroe 3", 80, 18)
        );

        // Crear controller con estrategia agresiva
        AIStrategy estrategiaAgresiva = new AggressiveStrategy();
        AIController controller = new AIController(enemigo, estrategiaAgresiva);

        System.out.println("Estrategia: " + estrategiaAgresiva.getNombreEstrategia());
        System.out.println("Descripción: " + estrategiaAgresiva.getDescripcion());
        Command accion = controller.decidirAccion(heroes);
        System.out.println("Acción decidida: " + accion.getDescription());
        System.out.println();

        // Cambiar a estrategia defensiva
        AIStrategy estrategiaDefensiva = new DefensiveStrategy();
        controller.setEstrategia(estrategiaDefensiva);

        System.out.println("Estrategia: " + estrategiaDefensiva.getNombreEstrategia());
        System.out.println("Descripción: " + estrategiaDefensiva.getDescripcion());
        accion = controller.decidirAccion(heroes);
        System.out.println("Acción decidida: " + accion.getDescription());
        System.out.println();

        // Cambiar a estrategia inteligente
        AIStrategy estrategiaInteligente = new IntelligentStrategy();
        controller.setEstrategia(estrategiaInteligente);

        System.out.println("Estrategia: " + estrategiaInteligente.getNombreEstrategia());
        System.out.println("Descripción: " + estrategiaInteligente.getDescripcion());
        accion = controller.decidirAccion(heroes);
        System.out.println("Acción decidida: " + accion.getDescription());
    }

    /**
     * Demuestra el patrón Observer con el sistema de eventos
     */
    private static void demoObserver() {
        System.out.println("--- PATRÓN OBSERVER: Sistema de Eventos ---");
        System.out.println();

        // Obtener el event manager (Singleton)
        EventManager manager = EventManager.getInstance();
        manager.limpiar(); // Limpiar estado previo

        // Crear observers
        CombatLogger logger = new CombatLogger(false);
        StatisticsTracker stats = new StatisticsTracker();
        UINotifier notifier = new UINotifier(false);

        // Suscribir observers
        manager.suscribir(logger);
        manager.suscribir(stats);
        manager.suscribir(notifier);

        System.out.println("Observers suscritos: " + manager.getCantidadObservers());
        System.out.println();

        // Simular eventos de combate
        System.out.println("Simulando eventos de combate...");

        GameEvent combateIniciado = new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Dragón");
        manager.notificar(combateIniciado);

        GameEvent ataque1 = new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Guerrero")
            .agregarDato("defensor", "Dragón")
            .agregarDato("danio", 25);
        manager.notificar(ataque1);

        GameEvent ataque2 = new GameEvent(EventType.ATAQUE_REALIZADO)
            .agregarDato("atacante", "Dragón")
            .agregarDato("defensor", "Guerrero")
            .agregarDato("danio", 40);
        manager.notificar(ataque2);

        GameEvent personajeMuerto = new GameEvent(EventType.PERSONAJE_MUERTO)
            .agregarDato("personaje", "Dragón");
        manager.notificar(personajeMuerto);

        GameEvent combateFinalizado = new GameEvent(EventType.COMBATE_FINALIZADO)
            .agregarDato("ganador", "Guerrero");
        manager.notificar(combateFinalizado);

        // Mostrar resultados
        System.out.println();
        logger.imprimirLog();
        System.out.println();
        System.out.println(stats.getReporte());
    }

    /**
     * Demuestra el patrón Memento con guardado/carga de partidas
     */
    private static void demoMemento() {
        System.out.println("--- PATRÓN MEMENTO: Guardado/Carga de Partidas ---");
        System.out.println();

        // Crear juego y caretaker
        GameOriginator juego = new GameOriginator("Héroe Valiente");
        GameCaretaker caretaker = new GameCaretaker();

        System.out.println("Estado inicial: " + juego);
        System.out.println();

        // Progresar en el juego
        System.out.println("Progresando en el juego...");
        juego.progresar();
        juego.progresar();
        juego.progresar();
        System.out.println("Estado después de progresar: " + juego);
        System.out.println();

        // Guardar estado
        System.out.println("Guardando estado...");
        GameMemento guardado1 = juego.guardar();
        caretaker.guardarEnMemoria(guardado1);
        System.out.println("  → " + guardado1);
        System.out.println();

        // Seguir progresando
        System.out.println("Continuando el juego...");
        juego.progresar();
        juego.progresar();
        juego.recibirDanio(50);
        System.out.println("Estado actual: " + juego);
        System.out.println();

        // Restaurar estado anterior
        System.out.println("Restaurando estado guardado...");
        juego.restaurar(guardado1);
        System.out.println("Estado restaurado: " + juego);
        System.out.println();

        // Demostrar guardado en disco
        System.out.println("Guardando en disco...");
        GameMemento guardadoFinal = juego.guardar();
        try {
            caretaker.guardarEnDisco(guardadoFinal, "partida_demo");
            System.out.println("Partida guardada exitosamente en disco");
        } catch (Exception e) {
            System.out.println("No se pudo guardar en disco: " + e.getMessage());
        }
    }
}
