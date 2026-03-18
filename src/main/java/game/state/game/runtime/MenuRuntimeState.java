package game.state.game.runtime;

import game.state.game.GameState;

/**
 * Estado runtime del menu principal.
 */
public class MenuRuntimeState implements GameState {
    private final GameRuntimeCoordinator coordinator;

    public MenuRuntimeState(GameRuntimeCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void manejarEntrada(String entrada) {
        // La entrada se procesa en actualizar().
    }

    @Override
    public void actualizar() {
        coordinator.cambiarEstadoFlujoRuntime("MenuPrincipal");
        int opcion = coordinator.leerOpcionMenuPrincipal();

        switch (opcion) {
            case 1 -> coordinator.cambiarEstadoRuntime(new SetupRuntimeState(coordinator));
            case 2 -> {
                boolean cargada = coordinator.cargarPartidaDesdeMenuRuntime();
                if (cargada) {
                    coordinator.cambiarEstadoRuntime(new AdventureRuntimeState(coordinator));
                }
            }
            case 3 -> coordinator.mostrarEstadisticasRuntime();
            case 4 -> coordinator.detenerJuegoRuntime();
            default -> {
                // Opcion validada previamente.
            }
        }
    }

    @Override
    public void render() {
        // Render inline en actualizar para mantener flujo por consola.
    }

    @Override
    public void onEnter() {
        // Sin efecto lateral adicional.
    }

    @Override
    public void onExit() {
        // Sin efecto lateral adicional.
    }

    @Override
    public String getNombre() {
        return "RuntimeMenu";
    }
}
