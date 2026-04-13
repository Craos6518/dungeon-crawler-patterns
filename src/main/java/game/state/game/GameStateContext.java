package game.state.game;

/**
 * Contexto del patron State para flujo de juego.
 *
 * Responsabilidad:
 * - Mantener la referencia al estado actual y delegar operaciones del ciclo (entrada, update, render).
 * - Garantizar hooks de salida/entrada al cambiar de estado.
 *
 * No hace:
 * - logica de negocio de dominio (combate, inventario, persistencia).
 * - decisiones de UI o serializacion de estado.
 */
public class GameStateContext {
    private GameState estadoActual;
    private boolean ejecutando;
    
    public GameStateContext(GameState estadoInicial) {
        this.ejecutando = true;
        cambiarEstado(estadoInicial);
    }
    
    /**
     * Cambia el estado del juego
     */
    public void cambiarEstado(GameState nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }

        if (estadoActual != null && !estadoActual.permiteTransicionA(nuevoEstado.getNombre())) {
            throw new IllegalStateException(
                "Transicion invalida: " + estadoActual.getNombre() + " -> " + nuevoEstado.getNombre()
            );
        }
        
        // Salir del estado actual
        if (estadoActual != null) {
            estadoActual.onExit();
        }
        
        // Cambiar al nuevo estado
        estadoActual = nuevoEstado;
        estadoActual.onEnter();
    }

    /**
     * Alias semantico para transiciones tipadas.
     */
    public void transitionTo(GameState nuevoEstado) {
        cambiarEstado(nuevoEstado);
    }
    
    /**
     * Procesa la entrada del usuario
     */
    public void procesarEntrada(String entrada) {
        if (estadoActual != null) {
            estadoActual.manejarEntrada(entrada);
        }
    }

    /**
     * Verifica si la accion se permite en el estado actual.
     */
    public boolean permiteAccion(String accion) {
        if (estadoActual == null) {
            return true;
        }
        return estadoActual.permiteAccion(accion);
    }

    /**
     * Valida accion runtime y lanza error tipado si el estado activo la bloquea.
     */
    public void assertAccionPermitida(String accion) {
        if (!permiteAccion(accion)) {
            throw new IllegalStateException(
                "Accion no permitida en estado " + estadoActual.getNombre() + ": " + accion
            );
        }
    }
    
    /**
     * Actualiza el juego
     */
    public void actualizar() {
        if (estadoActual != null && ejecutando) {
            estadoActual.actualizar();
        }
    }
    
    /**
     * Renderiza el juego
     */
    public void render() {
        if (estadoActual != null) {
            estadoActual.render();
        }
    }
    
    /**
     * Loop principal del juego
     */
    public void ejecutarLoop() {
        while (ejecutando) {
            render();
            actualizar();
            
            // En un juego real, aquí habría lógica para leer entrada, sleep, etc.
            // Por simplicidad, lo dejamos básico
        }
    }
    
    /**
     * Detiene el loop del juego
     */
    public void detener() {
        ejecutando = false;
    }
    
    /**
     * Obtiene el estado actual
     */
    public GameState getEstadoActual() {
        return estadoActual;
    }
    
    /**
     * Verifica si el juego está ejecutándose
     */
    public boolean isEjecutando() {
        return ejecutando;
    }
}
