package game.infrastructure.persistence.memento;

import game.application.state.GameMemento;

/**
 * Memento Pattern - Originator
 * 
 * El objeto principal del juego que puede guardar y restaurar su estado.
 */
public class GameOriginator {
    // Estado del juego
    private String nombreJugador;
    private int nivelActual;
    private int salaActual;
    private int vidaJugador;
    private int experiencia;
    private String estadoActual;
    
    public GameOriginator(String nombreJugador) {
        this.nombreJugador = nombreJugador;
        this.nivelActual = 1;
        this.salaActual = 1;
        this.vidaJugador = 100;
        this.experiencia = 0;
        this.estadoActual = "Menu";
    }
    
    /**
     * Crea un memento con el estado actual del juego
     */
    public GameMemento guardar() {
        return new GameMemento.Builder()
            .nombreJugador(nombreJugador)
            .nivelActual(nivelActual)
            .salaActual(salaActual)
            .agregarEstadoPersonaje("vida", vidaJugador)
            .agregarEstadoPersonaje("experiencia", experiencia)
            .agregarEstadoPersonaje("nivel", nivelActual)
            .agregarEstadoMazmorra("estadoActual", estadoActual)
            .build();
    }
    
    /**
     * Restaura el estado desde un memento
     */
    public void restaurar(GameMemento memento) {
        if (memento == null) {
            throw new IllegalArgumentException("El memento no puede ser null");
        }
        
        this.nombreJugador = memento.getNombreJugador();
        this.nivelActual = memento.getNivelActual();
        this.salaActual = memento.getSalaActual();
        
        // Restaurar estado del personaje
        var estadoPersonaje = memento.getEstadoPersonaje();
        this.vidaJugador = (Integer) estadoPersonaje.getOrDefault("vida", 100);
        this.experiencia = (Integer) estadoPersonaje.getOrDefault("experiencia", 0);
        
        // Restaurar estado de la mazmorra
        var estadoMazmorra = memento.getEstadoMazmorra();
        this.estadoActual = (String) estadoMazmorra.getOrDefault("estadoActual", "Menu");
    }
    
    /**
     * Simula progresión del juego
     */
    public void progresar() {
        salaActual++;
        experiencia += 50;
        
        if (experiencia >= nivelActual * 100) {
            subirNivel();
        }
    }
    
    private void subirNivel() {
        nivelActual++;
        vidaJugador = 100; // Restaurar vida al subir de nivel
        System.out.println("¡Has subido al nivel " + nivelActual + "!");
    }
    
    public void recibirDanio(int danio) {
        vidaJugador = Math.max(0, vidaJugador - danio);
    }
    
    public boolean estaVivo() {
        return vidaJugador > 0;
    }
    
    // Getters y setters
    public String getNombreJugador() { return nombreJugador; }
    public int getNivelActual() { return nivelActual; }
    public int getSalaActual() { return salaActual; }
    public int getVidaJugador() { return vidaJugador; }
    public int getExperiencia() { return experiencia; }
    public String getEstadoActual() { return estadoActual; }
    
    public void setEstadoActual(String estado) { this.estadoActual = estado; }
    
    @Override
    public String toString() {
        return String.format("Game[Player: %s, Level: %d, Room: %d, HP: %d, EXP: %d]",
            nombreJugador, nivelActual, salaActual, vidaJugador, experiencia);
    }
}
