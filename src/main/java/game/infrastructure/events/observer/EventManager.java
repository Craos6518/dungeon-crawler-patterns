package game.infrastructure.events.observer;

import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;
import game.application.ports.events.GameObserver;
import game.application.ports.events.EventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observer Pattern - Subject (EventManager)
 * 
 * Gestiona la suscripción de observers y la notificación de eventos.
 * Implementa el patrón Singleton para acceso global.
 */
public class EventManager implements EventPublisher {
    private static EventManager instance;
    
    private final List<GameObserver> observers;
    private final Map<EventType, List<GameObserver>> observersPorTipo;
    private final List<GameEvent> historialEventos;
    private boolean habilitado;
    private boolean validacionContratoHabilitada;
    
    private EventManager() {
        this.observers = new ArrayList<>();
        this.observersPorTipo = new HashMap<>();
        this.historialEventos = new ArrayList<>();
        this.habilitado = true;
        this.validacionContratoHabilitada = true;
    }
    
    /**
     * Obtiene la instancia única del EventManager
     */
    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }
    
    /**
     * Suscribe un observer para recibir todos los eventos
     */
    public void suscribir(GameObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("El observer no puede ser null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Suscribe un observer para recibir solo eventos de un tipo específico
     */
    public void suscribir(EventType tipo, GameObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("El observer no puede ser null");
        }
        
        observersPorTipo.computeIfAbsent(tipo, k -> new ArrayList<>());
        List<GameObserver> observersDeTipo = observersPorTipo.get(tipo);
        
        if (!observersDeTipo.contains(observer)) {
            observersDeTipo.add(observer);
        }
    }
    
    /**
     * Desuscribe un observer de todos los eventos
     */
    public void desuscribir(GameObserver observer) {
        observers.remove(observer);
        observersPorTipo.values().forEach(lista -> lista.remove(observer));
    }
    
    /**
     * Desuscribe un observer de un tipo de evento específico
     */
    public void desuscribir(EventType tipo, GameObserver observer) {
        List<GameObserver> observersDeTipo = observersPorTipo.get(tipo);
        if (observersDeTipo != null) {
            observersDeTipo.remove(observer);
        }
    }
    
    /**
     * Notifica un evento a todos los observers suscritos
     */
    public void notificar(GameEvent evento) {
        if (!habilitado) {
            return;
        }
        
        if (evento == null) {
            throw new IllegalArgumentException("El evento no puede ser null");
        }

        if (validacionContratoHabilitada) {
            EventContractValidator.validateOrThrow(evento);
        }
        
        // Guardar en historial
        historialEventos.add(evento);
        
        // Notificar a observers globales
        for (GameObserver observer : observers) {
            try {
                observer.onEvent(evento);
            } catch (Exception e) {
                // Log del error pero continuar notificando a otros observers
                System.err.println("Error notificando a observer: " + observer.getNombre());
                e.printStackTrace();
            }
        }
        
        // Notificar a observers específicos del tipo
        List<GameObserver> observersDeTipo = observersPorTipo.get(evento.getTipo());
        if (observersDeTipo != null) {
            for (GameObserver observer : observersDeTipo) {
                try {
                    observer.onEvent(evento);
                } catch (Exception e) {
                    System.err.println("Error notificando a observer: " + observer.getNombre());
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Limpia todos los observers y el historial
     */
    public void limpiar() {
        observers.clear();
        observersPorTipo.clear();
        historialEventos.clear();
    }
    
    /**
     * Limpia solo el historial de eventos
     */
    public void limpiarHistorial() {
        historialEventos.clear();
    }
    
    /**
     * Habilita o deshabilita la notificación de eventos
     */
    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    /**
     * Verifica si el sistema de eventos está habilitado
     */
    public boolean isHabilitado() {
        return habilitado;
    }

    public void setValidacionContratoHabilitada(boolean validacionContratoHabilitada) {
        this.validacionContratoHabilitada = validacionContratoHabilitada;
    }

    public boolean isValidacionContratoHabilitada() {
        return validacionContratoHabilitada;
    }
    
    /**
     * Obtiene el historial de eventos
     */
    public List<GameEvent> getHistorial() {
        return new ArrayList<>(historialEventos);
    }
    
    /**
     * Obtiene la cantidad de observers suscritos
     */
    public int getCantidadObservers() {
        return observers.size();
    }
}
