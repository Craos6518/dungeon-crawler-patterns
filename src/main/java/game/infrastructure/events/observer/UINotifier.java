package game.infrastructure.events.observer;

import game.application.ports.events.GameEvent;
import game.application.ports.events.GameObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer concreto - Envía notificaciones a la UI
 * 
 * En un sistema real, esto actualizaría componentes visuales.
 * En la versión de consola, simplemente formatea mensajes.
 */
public class UINotifier implements GameObserver {
    private final List<String> notificaciones;
    private final boolean mostrarInmediato;
    
    public UINotifier() {
        this(true);
    }
    
    public UINotifier(boolean mostrarInmediato) {
        this.notificaciones = new ArrayList<>();
        this.mostrarInmediato = mostrarInmediato;
    }
    
    @Override
    public void onEvent(GameEvent evento) {
        String notificacion = crearNotificacion(evento);
        
        if (notificacion != null) {
            notificaciones.add(notificacion);
            
            if (mostrarInmediato) {
                System.out.println("[UI] " + notificacion);
            }
        }
    }
    
    private String crearNotificacion(GameEvent evento) {
        return switch (evento.getTipo()) {
            case COMBATE_INICIADO -> "¡Combate iniciado!";
            case PERSONAJE_MUERTO -> String.format("¡%s ha sido derrotado!", 
                evento.getDato("personaje"));
            case COMBATE_FINALIZADO -> String.format("¡Victoria para %s!", 
                evento.getDato("ganador"));
            case ITEM_RECOGIDO -> String.format("Has recogido: %s", 
                evento.getDato("item"));
            case TESORO_ENCONTRADO -> "¡Tesoro encontrado!";
            case TRAMPA_ACTIVADA -> "¡Cuidado! Has activado una trampa";
            default -> null; // No todas los eventos necesitan notificación UI
        };
    }
    
    @Override
    public String getNombre() {
        return "UINotifier";
    }
    
    /**
     * Obtiene todas las notificaciones
     */
    public List<String> getNotificaciones() {
        return new ArrayList<>(notificaciones);
    }
    
    /**
     * Limpia las notificaciones
     */
    public void limpiar() {
        notificaciones.clear();
    }
}
