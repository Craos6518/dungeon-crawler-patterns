package game.demo;

import game.combat.facade.CombatFacade;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import game.effects.status.BurnEffect;
import game.effects.status.PoisonEffect;
import game.effects.status.StunEffect;
import game.effects.status.StrengthEffect;
import game.items.model.ContainerItem;
import game.items.model.ItemComponent;
import game.items.model.SimpleItem;

/**
 * Clase de demostración de los patrones estructurales implementados.
 * 
 * Muestra el uso de:
 * - Composite (sistema de inventario)
 * - Decorator (efectos de estado)
 * - Facade (sistema de combate simplificado)
 */
public class PatronesEstructuralesDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("DEMOSTRACIÓN DE PATRONES ESTRUCTURALES");
        System.out.println("=".repeat(60));
        System.out.println();

        demoComposite();
        System.out.println();

        demoDecorator();
        System.out.println();

        demoFacade();
        System.out.println();

        demoIntegracion();
    }

    /**
     * Demuestra el patrón Composite con el sistema de inventario.
     */
    private static void demoComposite() {
        System.out.println("--- PATRÓN COMPOSITE: Sistema de Inventario ---");
        System.out.println();

        // Crear items simples
        SimpleItem espada = new SimpleItem("Espada Legendaria", "Arma poderosa", "Arma", 500, 8);
        SimpleItem pocion = new SimpleItem("Poción de Vida", "Restaura 50 HP", "Consumible", 50, 1);
        SimpleItem oro = new SimpleItem("Moneda de Oro", "Moneda valiosa", "Tesoro", 10, 0);

        // Crear contenedores
        ContainerItem mochila = new ContainerItem("Mochila del Aventurero", "Mochila grande", 10, 2);
        ContainerItem bolsaOro = new ContainerItem("Bolsa de Monedas", "Bolsa pequeña", 20, 1);

        // Agregar oro a la bolsa
        for (int i = 0; i < 5; i++) {
            bolsaOro.agregar(new SimpleItem("Moneda de Oro", "Moneda", "Tesoro", 10, 0));
        }

        // Agregar items a la mochila
        mochila.agregar(espada);
        mochila.agregar(pocion);
        mochila.agregar(bolsaOro); // Contenedor dentro de contenedor

        // Mostrar inventario (el patrón Composite permite tratar todo uniformemente)
        System.out.println(mochila.mostrarDetalle());
        System.out.println();
        System.out.println("Valor total del inventario: " + mochila.getValorTotal() + " monedas");
        System.out.println("Peso total del inventario: " + mochila.getPesoTotal() + " kg");
    }

    /**
     * Demuestra el patrón Decorator con efectos de estado.
     */
    private static void demoDecorator() {
        System.out.println("--- PATRÓN DECORATOR: Efectos de Estado ---");
        System.out.println();

        Personaje heroe = new Guerrero("Héroe Valiente", 150, 25);
        System.out.println("Personaje original: " + heroe.getNombre() + " (HP: " + heroe.getVida() + ")");
        System.out.println();

        // Aplicar efecto de fortalecimiento
        StrengthEffect fortalecido = new StrengthEffect(heroe, 1.5, 3);
        System.out.println("Aplicando StrengthEffect (x1.5 daño, 3 turnos)");
        System.out.println("  → " + fortalecido.getNombre());
        System.out.println();

        // Aplicar envenenamiento encima del fortalecimiento
        PoisonEffect envenenado = new PoisonEffect(fortalecido, 5, 4);
        System.out.println("Aplicando PoisonEffect (5 daño/turno, 4 turnos)");
        System.out.println("  → " + envenenado.getNombre());
        System.out.println();

        // Simular 3 turnos
        System.out.println("Simulando efectos por 3 turnos:");
        for (int i = 1; i <= 3; i++) {
            System.out.println("Turno " + i + ":");
            
            // Aplicar efectos
            fortalecido.aplicarEfecto();
            envenenado.aplicarEfecto();
            
            System.out.println("  - " + envenenado.getDescripcionEfecto());
            System.out.println("  - " + fortalecido.getDescripcionEfecto());
            System.out.println("  - HP actual: " + envenenado.getVida());
            System.out.println();
        }

        System.out.println("Personaje base (sin decoradores): " + envenenado.getPersonajeBase().getNombre());
    }

    /**
     * Demuestra el patrón Facade con el sistema de combate simplificado.
     */
    private static void demoFacade() {
        System.out.println("--- PATRÓN FACADE: Sistema de Combate Simplificado ---");
        System.out.println();

        // Crear personajes
        Personaje heroe = new Guerrero("Guerrero", 100, 25);
        Personaje enemigo = new EnemigoBasico("Goblin", 60, 15);

        // Usar la fachada para simplificar el combate
        CombatFacade facade = new CombatFacade();

        System.out.println("Iniciando combate con API simplificada...");
        System.out.println();

        // 3 líneas de código vs ~20 líneas sin facade
        facade.iniciarCombate(heroe, enemigo);
        Personaje ganador = facade.ejecutarCombateCompleto();
        
        System.out.println("=".repeat(60));
        System.out.println();

        // Obtener estadísticas
        CombatFacade.EstadisticasCombate stats = facade.obtenerEstadisticas();
        System.out.println("ESTADÍSTICAS DEL COMBATE:");
        System.out.println(stats);
        System.out.println();

        System.out.println("LOG DETALLADO:");
        facade.imprimirLog();
    }

    /**
     * Demuestra la integración de los tres patrones estructurales.
     */
    private static void demoIntegracion() {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("--- INTEGRACIÓN: Todos los Patrones Juntos ---");
        System.out.println("=".repeat(60));
        System.out.println();

        // Crear personajes
        Personaje heroe = new Guerrero("Caballero", 120, 20);
        Personaje enemigo = new EnemigoBasico("Dragón Joven", 80, 18);

        // DECORATOR: Aplicar efectos de estado
        System.out.println("1. Aplicando efectos de estado (DECORATOR):");
        StunEffect dragonAturdido = new StunEffect(enemigo, 1);
        StrengthEffect heroeFortalecido = new StrengthEffect(heroe, 1.8, 5);
        System.out.println("   - " + dragonAturdido.getNombre());
        System.out.println("   - " + heroeFortalecido.getNombre());
        System.out.println();

        // FACADE: Ejecutar combate simplificado
        System.out.println("2. Ejecutando combate (FACADE):");
        CombatFacade facade = new CombatFacade();
        facade.iniciarCombate(heroeFortalecido, dragonAturdido);
        Personaje ganador = facade.ejecutarCombateCompleto();
        System.out.println();

        // Mostrar resultado
        System.out.println("3. Resultado:");
        System.out.println("   Ganador: " + ganador.getNombre());
        System.out.println("   HP restante: " + ganador.getVida());
        System.out.println();

        // COMPOSITE: Recompensa del combate
        System.out.println("4. Recompensas del combate (COMPOSITE):");
        ContainerItem botin = new ContainerItem("Botín del Dragón", "Tesoros obtenidos", 5, 0);
        
        botin.agregar(new SimpleItem("Escama de Dragón", "Material raro", "Material", 200, 1));
        botin.agregar(new SimpleItem("Gema Roja", "Gema preciosa", "Tesoro", 150, 0));
        botin.agregar(new SimpleItem("Poción Mayor", "Restaura 100 HP", "Consumible", 100, 1));
        
        System.out.println(botin.mostrarDetalle());
        System.out.println();
        System.out.println("Valor total del botín: " + botin.getValorTotal() + " monedas");
        System.out.println();

        System.out.println("=".repeat(60));
        System.out.println("Los tres patrones estructurales trabajando juntos!");
        System.out.println("=".repeat(60));
    }
}
