package game.state.game.runtime;

import game.state.game.GameState;

/**
 * Contrato de coordinación entre estados runtime y el juego principal.
 */
public interface GameRuntimeCoordinator {
    void cambiarEstadoRuntime(GameState nuevoEstado);

    void cambiarEstadoFlujoRuntime(String nombreEstado);

    int leerOpcionMenuPrincipal();

    boolean configurarNuevaPartidaRuntime();

    boolean cargarPartidaDesdeMenuRuntime();

    void mostrarEstadisticasRuntime();

    void ejecutarAventuraRuntime();

    boolean consumirSolicitudNuevaPartida();

    boolean consumirSolicitudReanudarExploracion();

    void detenerJuegoRuntime();

    boolean estaJuegoActivoRuntime();
}
