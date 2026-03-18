package game.state.game.runtime;

import game.state.game.GameState;

/**
 * Estado runtime de aventura (exploracion/combate).
 */
public class AdventureRuntimeState implements GameState {
    private final GameRuntimeCoordinator coordinator;

    public AdventureRuntimeState(GameRuntimeCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void manejarEntrada(String entrada) {
        // La entrada se procesa en actualizar().
    }

    @Override
    public void actualizar() {
        coordinator.ejecutarAventuraRuntime();

        if (!coordinator.estaJuegoActivoRuntime()) {
            return;
        }

        if (coordinator.consumirSolicitudNuevaPartida()) {
            coordinator.cambiarEstadoRuntime(new SetupRuntimeState(coordinator));
            return;
        }

        if (coordinator.consumirSolicitudReanudarExploracion()) {
            coordinator.cambiarEstadoRuntime(new AdventureRuntimeState(coordinator));
            return;
        }

        coordinator.cambiarEstadoRuntime(new MenuRuntimeState(coordinator));
    }

    @Override
    public void render() {
        // Render inline en actualizar para mantener flujo por consola.
    }

    @Override
    public void onEnter() {
        coordinator.cambiarEstadoFlujoRuntime("Exploracion");
    }

    @Override
    public void onExit() {
        // Sin efecto lateral adicional.
    }

    @Override
    public String getNombre() {
        return "RuntimeAdventure";
    }
}
