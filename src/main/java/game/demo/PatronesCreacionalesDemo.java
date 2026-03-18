package game.demo;

import game.domain.personaje.Personaje;
import game.domain.personaje.factory.*;
import game.dungeon.builder.*;
import game.dungeon.model.Dungeon;
import game.dungeon.model.Room;
import game.dungeon.theme.*;
import game.items.model.SimpleItem;

/**
 * Clase de demostración de los patrones creacionales implementados.
 * 
 * Muestra el uso de:
 * - Factory Method (creación flexible de personajes)
 * - Builder (construcción paso a paso de mazmorras)
 * - Abstract Factory (familias temáticas coherentes)
 */
public class PatronesCreacionalesDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("DEMOSTRACIÓN DE PATRONES CREACIONALES");
        System.out.println("=".repeat(60));
        System.out.println();

        demoFactoryMethod();
        System.out.println();

        demoBuilder();
        System.out.println();

        demoAbstractFactory();
        System.out.println();

        demoIntegracion();
    }

    /**
     * Demuestra el patrón Factory Method para creación de personajes.
     */
    private static void demoFactoryMethod() {
        System.out.println("--- PATRÓN FACTORY METHOD: Creación de Personajes ---");
        System.out.println();

        // Cada factory encapsula la lógica de creación de un tipo de personaje
        PersonajeFactory guerreroFactory = new GuerreroFactory(150, 25);
        PersonajeFactory magoFactory = new MagoFactory(80, 15);
        PersonajeFactory arqueroFactory = new ArqueroFactory(100, 20);

        // Crear héroes usando las factories
        Personaje guerrero = guerreroFactory.crearPersonaje("Arthas el Valiente");
        Personaje mago = magoFactory.crearPersonaje("Jaina la Sabia");
        Personaje arquero = arqueroFactory.crearPersonaje("Sylvanas la Cazadora");

        System.out.println("Héroes creados:");
        System.out.println("  → " + guerrero.getNombre() + " (HP: " + guerrero.getVida() + ")");
        System.out.println("  → " + mago.getNombre() + " (HP: " + mago.getVida() + ")");
        System.out.println("  → " + arquero.getNombre() + " (HP: " + arquero.getVida() + ")");
        System.out.println();

        // Factories de enemigos
        PersonajeFactory enemigoBasicoFactory = new EnemigoBasicoFactory(50, 10);
        PersonajeFactory orcoFactory = new OrcoFactory(100, 18);
        PersonajeFactory dragonFactory = new DragonFactory(300, 50);

        Personaje goblin = enemigoBasicoFactory.crearPersonaje("Goblin Ladrón");
        Personaje orco = orcoFactory.crearPersonaje("Orco Guerrero");
        Personaje dragon = dragonFactory.crearPersonaje("Dragón Ancestral");

        System.out.println("Enemigos creados:");
        System.out.println("  → " + goblin.getNombre() + " (HP: " + goblin.getVida() + ")");
        System.out.println("  → " + orco.getNombre() + " (HP: " + orco.getVida() + ")");
        System.out.println("  → " + dragon.getNombre() + " (HP: " + dragon.getVida() + ")");
        System.out.println();

        // Beneficio: El cliente trabaja con la interfaz PersonajeFactory
        // sin conocer las clases concretas
        System.out.println("✅ Beneficio: Desacoplamiento de la creación");
        System.out.println("   El código cliente no depende de clases concretas.");
    }

    /**
     * Demuestra el patrón Builder para construcción de mazmorras.
     */
    private static void demoBuilder() {
        System.out.println("--- PATRÓN BUILDER: Construcción de Mazmorras ---");
        System.out.println();

        // Opción 1: Construcción manual paso a paso
        System.out.println("📐 Construcción manual (sin Director):");
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        builder.reset(); // reset() retorna void, no es encadenable

        Dungeon mazmorraCustom = builder
            .setNombre("Cripta Olvidada")
            .setTema("Muerte")
            .setNivelDificultad(2) // int en vez de String
            .agregarSala("Entrada", "Sala de entrada a la cripta", 1, false, false)
            .agregarSala("Pasillo Oscuro", "Un pasillo lleno de sombras", 1, false, true)
            .agregarSala("Cámara del Tesoro", "Sala con cofre misterioso", 2, true, false)
            .setSalaJefe("Cripta Final", "Tumba del Rey Olvidado", 3)
            .build();

        System.out.println("  " + mazmorraCustom.getNombre());
        System.out.println("  Tema: " + mazmorraCustom.getTema());
        System.out.println("  Dificultad: " + mazmorraCustom.getNivelDificultad());
        System.out.println("  Salas: " + mazmorraCustom.getSalas().size());
        System.out.println();

        // Opción 2: Usando Director para construcciones predefinidas
        System.out.println("🎯 Construcción con Director (plantillas predefinidas):");
        DungeonDirector director = new DungeonDirector(builder);

        Dungeon mazmorraBasica = director.construirMazmorraBasica();
        System.out.println("  → Mazmorra Básica creada:");
        System.out.println("     " + mazmorraBasica.getNombre() + " (" + 
            mazmorraBasica.getSalas().size() + " salas)");

        Dungeon mazmorraFuego = director.construirMazmorraFuego();
        System.out.println("  → Mazmorra de Fuego creada:");
        System.out.println("     " + mazmorraFuego.getNombre() + " (" + 
            mazmorraFuego.getSalas().size() + " salas)");

        Dungeon mazmorraOscura = director.construirMazmorraOscura();
        System.out.println("  → Mazmorra Oscura creada:");
        System.out.println("     " + mazmorraOscura.getNombre() + " (" + 
            mazmorraOscura.getSalas().size() + " salas)");
        System.out.println();

        // Mostrar detalle de una sala
        Room primerasala = mazmorraFuego.getSalas().get(0);
        System.out.println("  Detalle de sala:");
        System.out.println("    Nombre: " + primerasala.getNombre());
        System.out.println("    Descripción: " + primerasala.getDescripcion());
        System.out.println("    Dificultad: " + primerasala.getDificultad());
        System.out.println();

        System.out.println("✅ Beneficio: Construcción compleja simplificada");
        System.out.println("   Objetos complejos se crean paso a paso de forma legible.");
    }

    /**
     * Demuestra el patrón Abstract Factory con familias temáticas.
     */
    private static void demoAbstractFactory() {
        System.out.println("--- PATRÓN ABSTRACT FACTORY: Familias Temáticas ---");
        System.out.println();

        // Abstract Factory crea familias de objetos relacionados
        // sin especificar sus clases concretas
        // NOTA: Los ThemeFactory crean enemigos y tesoros, NO mazmorras

        // Familia de Fuego
        System.out.println("🔥 Familia FUEGO:");
        DungeonThemeFactory temaFuego = new FireThemeFactory();
        
        Personaje enemigoBasico = temaFuego.crearEnemigoBasico();
        Personaje enemigoMedio = temaFuego.crearEnemigoMedio();
        Personaje jefeFuego = temaFuego.crearJefe();
        SimpleItem tesoroComun = temaFuego.crearTesoroComun();
        SimpleItem tesoroRaro = temaFuego.crearTesoroRaro();

        System.out.println("  Enemigo Básico: " + enemigoBasico.getNombre() + 
            " (HP: " + enemigoBasico.getVida() + ")");
        System.out.println("  Enemigo Medio: " + enemigoMedio.getNombre() + 
            " (HP: " + enemigoMedio.getVida() + ")");
        System.out.println("  Jefe: " + jefeFuego.getNombre() + 
            " (HP: " + jefeFuego.getVida() + ")");
        System.out.println("  Tesoro Común: " + tesoroComun.getNombre());
        System.out.println("  Tesoro Raro: " + tesoroRaro.getNombre());
        System.out.println();

        // Familia de Hielo
        System.out.println("❄️  Familia HIELO:");
        DungeonThemeFactory temaHielo = new IceThemeFactory();
        
        Personaje enemigoHielo = temaHielo.crearEnemigoBasico();
        Personaje jefeHielo = temaHielo.crearJefe();
        SimpleItem tesoroHielo = temaHielo.crearTesoroRaro();

        System.out.println("  Enemigo: " + enemigoHielo.getNombre() + 
            " (HP: " + enemigoHielo.getVida() + ")");
        System.out.println("  Jefe: " + jefeHielo.getNombre() + 
            " (HP: " + jefeHielo.getVida() + ")");
        System.out.println("  Tesoro: " + tesoroHielo.getNombre());
        System.out.println();

        // Familia Oscura
        System.out.println("🌑 Familia OSCURA:");
        DungeonThemeFactory temaOscuro = new DarkThemeFactory();
        
        Personaje enemigoOscuro = temaOscuro.crearEnemigoMedio();
        Personaje jefeOscuro = temaOscuro.crearJefe();
        SimpleItem tesoroOscuro = temaOscuro.crearTesoroRaro();

        System.out.println("  Enemigo: " + enemigoOscuro.getNombre() + 
            " (HP: " + enemigoOscuro.getVida() + ")");
        System.out.println("  Jefe: " + jefeOscuro.getNombre() + 
            " (HP: " + jefeOscuro.getVida() + ")");
        System.out.println("  Tesoro: " + tesoroOscuro.getNombre());
        System.out.println();

        // Familia de Veneno
        System.out.println("☠️  Familia VENENO:");
        DungeonThemeFactory temaVeneno = new PoisonThemeFactory();
        
        Personaje enemigoVeneno = temaVeneno.crearEnemigoBasico();
        Personaje jefeVeneno = temaVeneno.crearJefe();
        SimpleItem tesoroVeneno = temaVeneno.crearTesoroComun();

        System.out.println("  Enemigo: " + enemigoVeneno.getNombre() + 
            " (HP: " + enemigoVeneno.getVida() + ")");
        System.out.println("  Jefe: " + jefeVeneno.getNombre() + 
            " (HP: " + jefeVeneno.getVida() + ")");
        System.out.println("  Tesoro: " + tesoroVeneno.getNombre());
        System.out.println();

        System.out.println("✅ Beneficio: Coherencia temática garantizada");
        System.out.println("   Todos los objetos de una familia son compatibles.");
    }

    /**
     * Demuestra la integración de los tres patrones creacionales.
     */
    private static void demoIntegracion() {
        System.out.println("--- INTEGRACIÓN DE PATRONES CREACIONALES ---");
        System.out.println();

        System.out.println("Escenario: Preparar una aventura completa");
        System.out.println();

        // 1. Crear héroe con Factory Method
        System.out.println("1️⃣  Creando héroe (Factory Method):");
        PersonajeFactory guerreroFactory = new GuerreroFactory(200, 30);
        Personaje heroe = guerreroFactory.crearPersonaje("Héroe Legendario");
        System.out.println("   → " + heroe.getNombre() + " está listo para la aventura");
        System.out.println();

        // 2. Seleccionar tema con Abstract Factory
        System.out.println("2️⃣  Seleccionando tema de la aventura (Abstract Factory):");
        DungeonThemeFactory temaSeleccionado = new FireThemeFactory();
        System.out.println("   → Tema: FUEGO 🔥");
        System.out.println();

        // 3. Crear familia de enemigos con Abstract Factory
        System.out.println("3️⃣  Generando enemigos del tema (Abstract Factory):");
        Personaje enemigo = temaSeleccionado.crearEnemigoBasico();
        Personaje jefe = temaSeleccionado.crearJefe();
        SimpleItem tesoro = temaSeleccionado.crearTesoroRaro();
        System.out.println("   → Enemigo: " + enemigo.getNombre());
        System.out.println("   → Jefe: " + jefe.getNombre());
        System.out.println("   → Tesoro: " + tesoro.getNombre());
        System.out.println();

        // 4. Construir mazmorra con Builder
        System.out.println("4️⃣  Construyendo mazmorra (Builder):");
        DungeonBuilder builder = new ConcreteDungeonBuilder();
        DungeonDirector director = new DungeonDirector(builder);
        Dungeon mazmorra = director.construirMazmorraFuego();
        System.out.println("   → " + mazmorra.getNombre() + " lista para explorar");
        System.out.println("   → Salas: " + mazmorra.getSalas().size());
        System.out.println();

        System.out.println("✅ Aventura completa preparada usando:");
        System.out.println("   • Factory Method → Héroe flexible");
        System.out.println("   • Abstract Factory → Familia temática coherente");
        System.out.println("   • Builder → Mazmorra compleja construida paso a paso");
        System.out.println();
        System.out.println("🎮 ¡Todo listo para iniciar el combate!");
    }
}
