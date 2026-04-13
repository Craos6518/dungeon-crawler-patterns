package game.application.state;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Memento Pattern - Memento
 * 
 * Almacena el estado del juego en un momento específico.
 * Inmutable para prevenir modificaciones externas.
 */
public class GameMemento implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String schemaVersion;
    private final String nombreJugador;
    private final int nivelActual;
    private final int salaActual;
    private final LocalDateTime fechaGuardado;
    private final Map<String, Object> estadoPersonaje;
    private final Map<String, Object> estadoInventario;
    private final Map<String, Object> estadoMazmorra;
    
    /**
     * Constructor privado para usar Builder
     */
    private GameMemento(Builder builder) {
        this.schemaVersion = builder.schemaVersion;
        this.nombreJugador = builder.nombreJugador;
        this.nivelActual = builder.nivelActual;
        this.salaActual = builder.salaActual;
        this.fechaGuardado = LocalDateTime.now();
        this.estadoPersonaje = new HashMap<>(builder.estadoPersonaje);
        this.estadoInventario = new HashMap<>(builder.estadoInventario);
        this.estadoMazmorra = new HashMap<>(builder.estadoMazmorra);
    }
    
    // Getters (sin setters - inmutable)
    public String getSchemaVersion() { return schemaVersion; }
    public String getNombreJugador() { return nombreJugador; }
    public int getNivelActual() { return nivelActual; }
    public int getSalaActual() { return salaActual; }
    public LocalDateTime getFechaGuardado() { return fechaGuardado; }
    
    public Map<String, Object> getEstadoPersonaje() {
        return new HashMap<>(estadoPersonaje);
    }
    
    public Map<String, Object> getEstadoInventario() {
        return new HashMap<>(estadoInventario);
    }
    
    public Map<String, Object> getEstadoMazmorra() {
        return new HashMap<>(estadoMazmorra);
    }
    
    @Override
    public String toString() {
        return String.format("GameMemento[Player: %s, Nivel: %d, Sala: %d, Guardado: %s]",
            nombreJugador, nivelActual, salaActual, fechaGuardado);
    }
    
    /**
     * Builder para crear GameMemento
     */
    public static class Builder {
        private String schemaVersion = "1.0";
        private String nombreJugador;
        private int nivelActual;
        private int salaActual;
        private Map<String, Object> estadoPersonaje = new HashMap<>();
        private Map<String, Object> estadoInventario = new HashMap<>();
        private Map<String, Object> estadoMazmorra = new HashMap<>();
        
        public Builder schemaVersion(String version) {
            this.schemaVersion = version;
            return this;
        }
        
        public Builder nombreJugador(String nombre) {
            this.nombreJugador = nombre;
            return this;
        }
        
        public Builder nivelActual(int nivel) {
            this.nivelActual = nivel;
            return this;
        }
        
        public Builder salaActual(int sala) {
            this.salaActual = sala;
            return this;
        }
        
        public Builder agregarEstadoPersonaje(String clave, Object valor) {
            this.estadoPersonaje.put(clave, valor);
            return this;
        }
        
        public Builder agregarEstadoInventario(String clave, Object valor) {
            this.estadoInventario.put(clave, valor);
            return this;
        }
        
        public Builder agregarEstadoMazmorra(String clave, Object valor) {
            this.estadoMazmorra.put(clave, valor);
            return this;
        }
        
        public GameMemento build() {
            if (nombreJugador == null || nombreJugador.isEmpty()) {
                throw new IllegalStateException("El nombre del jugador es requerido");
            }
            return new GameMemento(this);
        }
    }
}
