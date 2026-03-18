package game.demo;

import game.state.game.GameState;
import game.state.game.GameStateContext;
import game.state.game.MenuState;

/**
 * Demostración del patrón State en su forma académica/clásica.
 * 
 * Esta demo utiliza los "legacy states" (MenuState, ExplorationState, CombatState, etc.)
 * para mostrar el patrón State en su implementación más pura y educativa.
 * 
 * Los legacy states están diseñados principalmente para:
 * - Enseñanza: Mostrar cómo el patrón State elimina condicionales complejos
 * - Demostración: Ilustrar transiciones de estado explícitas
 * - Validación académica: Comprobar que el patrón está correctamente implementado
 * 
 * En la implementación de PRODUCCIÓN, el juego utiliza RuntimeStates
 * (MenuRuntimeState, SetupRuntimeState, AdventureRuntimeState) que orquestan
 * el flujo real del juego.
 * 
 * DIFERENCIAS CLAVE:
 * 
 * Legacy States (esta demo):
 * - Diseño: Estado por estado pequeño y granular
 * - Flujo: MenuState → ExplorationState → CombatState → GameOverState → MenuState
 * - Uso: Demostración del patrón, pruebas unitarias de estados
 * - Contexto: GameStateContext simple
 * - Acoplamiento: Los estados conocen el GameStateContext
 * 
 * RuntimeStates (InteractiveGame):
 * - Diseño: Estados gruesos de alto nivel (Menu, Setup, Adventure)
 * - Flujo: MenuRuntimeState → SetupRuntimeState → AdventureRuntimeState → MenuRuntimeState
 * - Uso: Orquestación del juego completo en tiempo de ejecución
 * - Contexto: GameStateContext + GameRuntimeCoordinator
 * - Acoplamiento: Los RuntimeStates solo conocen GameRuntimeCoordinator (desacoplados)
 * 
 * @see game.state.game.MenuState
 * @see game.state.game.ExplorationState
 * @see game.state.game.CombatState
 * @see game.state.game.InventoryState
 * @see game.state.game.GameOverState
 * @see game.state.game.GameStateContext
 * @see game.InteractiveGame (implementación de producción con RuntimeStates)
 */
public class LegacyStatePatternDemo {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("DEMOSTRACIÓN DEL PATRÓN STATE (Enfoque Académico - Legacy States)");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("Este programa demuestra el patrón State usando la implementación académica.");
        System.out.println("Los estados están completamente desacoplados y comunican a través del contexto.");
        System.out.println();
        System.out.println("Estados disponibles:");
        System.out.println("  - MenuState       : Menú principal");
        System.out.println("  - ExplorationState: Exploración de la mazmorra");
        System.out.println("  - CombatState     : Combate por turnos");
        System.out.println("  - InventoryState  : Gestión del inventario");
        System.out.println("  - GameOverState   : Fin del juego");
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println();
        
        // Crear un estado dummy inicial para el contexto (para evitar referencia circular)
        GameState dummyState = new GameState() {
            @Override public void manejarEntrada(String entrada) {}
            @Override public void actualizar() {}
            @Override public void render() {}
            @Override public void onEnter() {}
            @Override public void onExit() {}
            @Override public String getNombre() { return "Dummy"; }
        };
        
        // Crear el contexto con el estado dummy
        GameStateContext contexto = new GameStateContext(dummyState);
        
        // Inmediatamente cambiar al MenuState real
        contexto.cambiarEstado(new MenuState(contexto));
        
        // Loop simple de demostración
        simularJuego(contexto);
    }
    
    /**
     * Simula un ciclo simple del juego para demostración.
     * En un juego real, esto sería un loop más elaborado con entrada del usuario.
     */
    private static void simularJuego(GameStateContext contexto) {
        System.out.println("\nIniciando simulación...\n");
        
        // El juego se ejecutaría leyendo entrada del usuario
        System.out.println("NOTAS SOBRE ESTA DEMOSTRACIÓN:");
        System.out.println("- Los legacy states están totalmente implementados y funcionales");
        System.out.println("- Cada estado encapsula su propia lógica de entrada/actualización/render");
        System.out.println("- Las transiciones de estado son explícitas (no hay magic strings)");
        System.out.println("- El GameStateContext gestiona las llamadas a onEnter() y onExit()");
        System.out.println("- El patrón State cumple el Single Responsibility Principle");
        System.out.println();
        System.out.println("Para ver los legacy states en acción, revisar:");
        System.out.println("  - src/main/java/game/state/game/MenuState.java");
        System.out.println("  - src/main/java/game/state/game/ExplorationState.java");
        System.out.println("  - src/main/java/game/state/game/CombatState.java");
        System.out.println("  - src/main/java/game/state/game/InventoryState.java");
        System.out.println("  - src/main/java/game/state/game/GameOverState.java");
        System.out.println();
        System.out.println("PARA JUGAR EL JUEGO COMPLETO, ejecutar:");
        System.out.println("  java game.InteractiveGame");
        System.out.println();
        System.out.println("Que usa los RuntimeStates (implementación de producción):");
        System.out.println("  - MenuRuntimeState (en game.state.game.runtime)");
        System.out.println("  - SetupRuntimeState (en game.state.game.runtime)");
        System.out.println("  - AdventureRuntimeState (en game.state.game.runtime)");
        System.out.println();
        System.out.println("=".repeat(70));
    }
}
