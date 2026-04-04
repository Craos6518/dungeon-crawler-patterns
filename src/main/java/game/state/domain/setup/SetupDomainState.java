package game.state.domain.setup;

import game.domain.personaje.Personaje;
import game.domain.personaje.factory.ArqueroFactory;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.personaje.factory.MagoFactory;
import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.DungeonBuilder;
import game.dungeon.builder.ProceduralDungeonGenerator;
import game.dungeon.theme.DarkThemeFactory;
import game.dungeon.theme.DungeonThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.dungeon.theme.IceThemeFactory;
import game.dungeon.theme.PoisonThemeFactory;
import game.events.observer.GameEvent;
import game.events.observer.EventType;
import game.items.model.ContainerItem;
import game.state.domain.AbstractDomainGameState;
import game.state.domain.GameSessionData;

import java.util.Random;

/**
 * Estado de dominio que encapsula la lógica de setup/configuración.
 * Responsable de:
 * - Selección de héroe
 * - Creación de inventario inicial
 * - Selección de tema de mazmorra
 * - Construcción de mazmorra
 * - Inicialización de variables de sesión
 * 
 * Completamente independiente y reutilizable.
 */
public class SetupDomainState extends AbstractDomainGameState {
    
    private final GameSessionData sessionData;
    private final Random random;
    
    /**
     * Callback cuando la configuración está completa
     */
    public interface SetupCompleteCallback {
        void alCompletarSetup();
    }
    
    private final SetupCompleteCallback setupCallback;
    
    public SetupDomainState(
        GameSessionData sessionData,
        SetupCompleteCallback setupCallback
    ) {
        this.sessionData = sessionData;
        this.setupCallback = setupCallback;
        this.random = new Random();
    }
    
    @Override
    public boolean ejecutar() {
        return configurarNuevaPartida();
    }
    
    @Override
    public String getNombreEstado() {
        return "Setup";
    }
    
    /**
     * Configura una nueva partida
     * @return true si se completó exitosamente, false si se canceló
     */
    public boolean configurarNuevaPartida() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎮 NUEVA PARTIDA");
        System.out.println("=".repeat(60));

        // Seleccionar héroe
        Personaje heroe = elegirHeroe();
        if (heroe == null) {
            return false;
        }
        sessionData.setHeroe(heroe);

        // Crear inventario
        crearInventarioInicial();

        // Seleccionar tema
        DungeonThemeFactory tema = elegirTema();
        if (tema == null) {
            return false;
        }
        sessionData.setTemaActual(tema);

        // Construir mazmorra
        construirMazmorra();

        // Reiniciar contadores
        sessionData.reiniciarParaNuevaPartida();

        // Notificar evento
        eventManager.notificar(new GameEvent(EventType.JUEGO_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("tema", tema.getNombreTema()));

        // Invocar callback
        setupCallback.alCompletarSetup();

        return true;
    }
    
    private Personaje elegirHeroe() {
        System.out.println("\n¿Qué clase deseas elegir?");
        System.out.println("1. 💪 Guerrero (100 HP, 18 ATK)");
        System.out.println("2. 🏹 Arquero (75 HP, 24 ATK)");
        System.out.println("3. 🔮 Mago (55 HP, 30 ATK)");
        System.out.println("4. Volver");

        int seleccion = leerOpcion(1, 4);

        if (seleccion == 4) {
            return null;
        }

        System.out.print("Nombre de tu héroe: ");
        String nombre = leerLineaRequerida().trim();
        if (nombre.isEmpty()) {
            nombre = "Héroe";
        }

        return switch (seleccion) {
            case 1 -> new GuerreroFactory(100, 18).crearPersonaje(nombre);
            case 2 -> new ArqueroFactory(75, 24).crearPersonaje(nombre);
            case 3 -> new MagoFactory(55, 30).crearPersonaje(nombre);
            default -> null;
        };
    }
    
    private void crearInventarioInicial() {
        ContainerItem mochila = new ContainerItem(
            "Mochila",
            "Tu inventario principal",
            20, // capacidad
            2   // peso
        );
        
        // Añadir consumibles iniciales
        mochila.agregar(
            new game.items.model.SimpleItem(
                "Poción de Vida",
                "Restaura 50 HP",
                "Consumible",
                50,
                1
            )
        );
        
        mochila.agregar(
            new game.items.model.SimpleItem(
                "Antídoto",
                "Elimina efectos de veneno",
                "Consumible",
                75,
                1
            )
        );
        
        sessionData.setInventario(mochila);
    }
    
    private DungeonThemeFactory elegirTema() {
        System.out.println("\n¿Qué tema de mazmorra prefieres?");
        System.out.println("1. 🔥 Fuego (Enemigos fuertes, fuego destructivo)");
        System.out.println("2. ❄️  Hielo (Enemigos ágiles, trampas de hielo)");
        System.out.println("3. 🌑 Oscuridad (Enemigos sigilo, magia oscura)");
        System.out.println("4. ☠️  Veneno (Enemigos venenosos, efectos tóxicos)");
        System.out.println("5. Volver");

        int seleccion = leerOpcion(1, 5);

        return switch (seleccion) {
            case 1 -> new FireThemeFactory();
            case 2 -> new IceThemeFactory();
            case 3 -> new DarkThemeFactory();
            case 4 -> new PoisonThemeFactory();
            case 5 -> null;
            default -> new FireThemeFactory();
        };
    }
    
    private void construirMazmorra() {
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        var mazmorra = ProceduralDungeonGenerator.generar(builder, sessionData.getTemaActual(), random);

        sessionData.setMazmorra(mazmorra);

        System.out.println("\n🏗️  Mazmorra construida: " + mazmorra.getNombre());
        System.out.println("   Salas: " + mazmorra.getSalas().size());
        System.out.println("   Dificultad: " + mazmorra.getNivelDificultad());
    }
}
