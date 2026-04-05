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
        
        // Salir del estado actual
        if (estadoActual != null) {
            estadoActual.onExit();
        }
        
        // Cambiar al nuevo estado
        estadoActual = nuevoEstado;
        estadoActual.onEnter();
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
