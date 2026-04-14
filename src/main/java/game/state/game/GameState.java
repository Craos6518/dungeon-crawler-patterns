package game.state.game;

/**
 * State Pattern - Interfaz State
 * 
 * Define el comportamiento que varía según el estado actual del juego.
 */
public interface GameState {
    /**
     * Maneja la entrada del usuario en este estado
     * 
     * @param entrada La entrada del usuario
     */
    void manejarEntrada(String entrada);
    
    /**
     * Actualiza el estado del juego
     * Llamado cada frame/turno
     */
    void actualizar();
    
    /**
     * Renderiza/muestra el estado actual
     */
    void render();
    
    /**
     * Se ejecuta al entrar a este estado
     */
    void onEnter();
    
    /**
     * Se ejecuta al salir de este estado
     */
    void onExit();
    
    /**
     * Obtiene el nombre del estado
     */
    String getNombre();

    /**
     * Indica si una accion runtime es valida para este estado.
     */
    default boolean permiteAccion(String accion) {
        return true;
    }

    /**
     * Indica si se permite transicionar desde este estado al estado destino.
     */
    default boolean permiteTransicionA(String nombreEstadoDestino) {
        return true;
    }
}
