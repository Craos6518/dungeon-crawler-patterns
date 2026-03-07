package game;

import game.combat.engine.MotorCombate;
import game.demo.IntegracionCompletaDemo;
import game.domain.personaje.Personaje;
import game.domain.personaje.factory.GuerreroFactory;
import game.domain.personaje.factory.MagoFactory;
import game.domain.personaje.factory.PersonajeFactory;
import game.dungeon.builder.ConcreteDungeonBuilder;
import game.dungeon.builder.DungeonBuilder;
import game.dungeon.builder.DungeonDirector;
import game.dungeon.model.Dungeon;
import game.dungeon.theme.DungeonThemeFactory;
import game.dungeon.theme.FireThemeFactory;
import game.dungeon.theme.IceThemeFactory;
import game.items.model.SimpleItem;

/**
 * Clase principal del proyecto Dungeon Crawler.
 * 
 * Demuestra el uso de los patrones de diseño creacionales en un demo básico.
 * Para ver la INTEGRACIÓN COMPLETA de TODOS los patrones, ejecutar:
 * 
 *     IntegracionCompletaDemo.main()
 * 
 * Patrones demostrados aquí:
 * - Factory Method
 * - Builder  
 * - Abstract Factory
 */
public class Main {
	public static void main(String[] args) {
		// Verificar si se solicita la demo completa
		if (args.length > 0 && args[0].equals("--integracion")) {
			IntegracionCompletaDemo.main(args);
			return;
		}
		
		System.out.println("=== DUNGEON CRAWLER - PATRONES CREACIONALES ===\n");
		System.out.println("💡 Tip: Ejecuta con '--integracion' para ver TODOS los patrones integrados\n");
		
		// ========== FACTORY METHOD ==========
		System.out.println("--- 1. FACTORY METHOD: Creación de Personajes ---");
		PersonajeFactory guerreroFactory = new GuerreroFactory(100, 15);
		PersonajeFactory magoFactory = new MagoFactory(80, 20);
		
		Personaje heroe = guerreroFactory.crearPersonaje("Arthas");
		Personaje heroe2 = magoFactory.crearPersonaje("Gandalf");
		
		System.out.println("Héroe creado: " + heroe.getNombre() + 
		                   " (HP: " + heroe.getVida() + ")");
		System.out.println("Héroe creado: " + heroe2.getNombre() + 
		                   " (HP: " + heroe2.getVida() + ")");
		System.out.println();
		
		// ========== BUILDER ==========
		System.out.println("--- 2. BUILDER: Construcción de Mazmorras ---");
		DungeonBuilder builder = new ConcreteDungeonBuilder();
		DungeonDirector director = new DungeonDirector(builder);
		
		Dungeon mazmorraBasica = director.construirMazmorraBasica();
		System.out.println(mazmorraBasica);
		
		Dungeon mazmorraFuego = director.construirMazmorraFuego();
		System.out.println(mazmorraFuego);
		System.out.println();
		
		// ========== ABSTRACT FACTORY ==========
		System.out.println("--- 3. ABSTRACT FACTORY: Temas de Mazmorra ---");
		DungeonThemeFactory fireTheme = new FireThemeFactory();
		DungeonThemeFactory iceTheme = new IceThemeFactory();
		
		System.out.println("Tema: " + fireTheme.getNombreTema());
		Personaje enemigoFuego = fireTheme.crearEnemigoBasico();
		SimpleItem tesoroFuego = fireTheme.crearTesoroRaro();
		System.out.println("  Enemigo: " + enemigoFuego.getNombre());
		System.out.println("  Tesoro: " + tesoroFuego);
		
		System.out.println("\nTema: " + iceTheme.getNombreTema());
		Personaje jefeFuego = fireTheme.crearJefe();
		SimpleItem tesoroHielo = iceTheme.crearTesoroComun();
		System.out.println("  Jefe: " + jefeFuego.getNombre() + 
		                   " (HP: " + jefeFuego.getVida() + ")");
		System.out.println("  Tesoro: " + tesoroHielo);
		System.out.println();
		
		// ========== COMBATE DE DEMOSTRACIÓN ==========
		System.out.println("--- 4. COMBATE: Héroe vs Jefe de Fuego ---");
		MotorCombate combate = new MotorCombate(heroe, jefeFuego);
		Personaje ganador = combate.iniciar();
		System.out.println("Ganador: " + ganador.getNombre());
		System.out.println();
		
		System.out.println("─".repeat(60));
		System.out.println("✨ Para ver la INTEGRACIÓN COMPLETA, ejecuta:");
		System.out.println("   java -cp target/classes game.demo.IntegracionCompletaDemo");
		System.out.println("   o");
		System.out.println("   java -cp target/classes game.Main --integracion");
		System.out.println("─".repeat(60));
	}
}
