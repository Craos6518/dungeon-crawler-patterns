package game.state.game.runtime;

import game.state.game.GameState;

/**
 * Estado runtime de preparacion de nueva partida.
 */
public class SetupRuntimeState implements GameState {
    private final GameRuntimeCoordinator coordinator;

    public SetupRuntimeState(GameRuntimeCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public void manejarEntrada(String entrada) {
        // La entrada se procesa en actualizar().
    }

    @Override
    public void actualizar() {
        boolean lista = coordinator.configurarNuevaPartidaRuntime();
        if (lista) {
            coordinator.cambiarEstadoRuntime(new AdventureRuntimeState(coordinator));
        } else {
            coordinator.cambiarEstadoRuntime(new MenuRuntimeState(coordinator));
        }
    }

    @Override
    public void render() {
        // Render inline en actualizar para mantener flujo por consola.
    }

    @Override
    public void onEnter() {
        coordinator.cambiarEstadoFlujoRuntime("Preparacion");
    }

    @Override
    public void onExit() {
        // Sin efecto lateral adicional.
    }

    @Override
    public String getNombre() {
        return "RuntimeSetup";
    }
}
