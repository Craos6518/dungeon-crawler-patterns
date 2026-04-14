package game.application.ports.events;

/**
 * Observer Pattern - Interfaz Observer
 * 
 * Define el contrato para objetos que deben ser notificados
 * cuando ocurren eventos en el juego.
 */
public interface GameObserver {
    /**
     * Notifica al observer sobre un evento del juego
     * 
     * @param evento El evento que ocurrió
     */
    void onEvent(GameEvent evento);
    
    /**
     * Obtiene el nombre identificador del observer
     */
    String getNombre();
}
