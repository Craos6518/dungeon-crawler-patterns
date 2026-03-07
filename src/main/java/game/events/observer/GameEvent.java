package game.events.observer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa un evento del juego
 * 
 * Encapsula toda la información relevante sobre un evento que ocurre en el juego.
 */
public class GameEvent {
    private final EventType tipo;
    private final LocalDateTime timestamp;
    private final Map<String, Object> datos;
    
    public GameEvent(EventType tipo) {
        this.tipo = tipo;
        this.timestamp = LocalDateTime.now();
        this.datos = new HashMap<>();
    }
    
    /**
     * Agrega un dato al evento
     */
    public GameEvent agregarDato(String clave, Object valor) {
        datos.put(clave, valor);
        return this;
    }
    
    /**
     * Obtiene un dato del evento
     */
    public Object getDato(String clave) {
        return datos.get(clave);
    }
    
    /**
     * Obtiene un dato con tipo específico
     */
    @SuppressWarnings("unchecked")
    public <T> T getDato(String clave, Class<T> tipo) {
        return (T) datos.get(clave);
    }
    
    public EventType getTipo() {
        return tipo;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getDatos() {
        return new HashMap<>(datos);
    }
    
    @Override
    public String toString() {
        return String.format("GameEvent[%s, %s, datos=%s]", 
            tipo, timestamp, datos);
    }
}
