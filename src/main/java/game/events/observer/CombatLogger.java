package game.events.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer concreto - Registra eventos de combate en un log
 */
public class CombatLogger implements GameObserver {
    private final List<String> log;
    private final boolean verbose;
    
    public CombatLogger() {
        this(false);
    }
    
    public CombatLogger(boolean verbose) {
        this.log = new ArrayList<>();
        this.verbose = verbose;
    }
    
    @Override
    public void onEvent(GameEvent evento) {
        // Solo procesar eventos de combate
        if (!esCombatEvent(evento.getTipo())) {
            return;
        }
        
        String mensaje = formatearEvento(evento);
        log.add(mensaje);
        
        if (verbose) {
            System.out.println("[COMBAT LOG] " + mensaje);
        }
    }
    
    private boolean esCombatEvent(EventType tipo) {
        return tipo == EventType.COMBATE_INICIADO ||
               tipo == EventType.ATAQUE_REALIZADO ||
               tipo == EventType.DANIO_RECIBIDO ||
               tipo == EventType.PERSONAJE_MUERTO ||
               tipo == EventType.COMBATE_FINALIZADO ||
               tipo == EventType.TURNO_CAMBIADO;
    }
    
    private String formatearEvento(GameEvent evento) {
        return switch (evento.getTipo()) {
            case COMBATE_INICIADO -> String.format("Combate iniciado: %s vs %s",
                evento.getDato("atacante"),
                evento.getDato("defensor"));
            
            case ATAQUE_REALIZADO -> String.format("%s ataca a %s causando %d de daño",
                evento.getDato("atacante"),
                evento.getDato("defensor"),
                evento.getDato("danio"));
            
            case PERSONAJE_MUERTO -> String.format("%s ha sido derrotado",
                evento.getDato("personaje"));
            
            case COMBATE_FINALIZADO -> String.format("Combate finalizado. Ganador: %s",
                evento.getDato("ganador"));
            
            default -> evento.toString();
        };
    }
    
    @Override
    public String getNombre() {
        return "CombatLogger";
    }
    
    /**
     * Obtiene todo el log
     */
    public List<String> getLog() {
        return new ArrayList<>(log);
    }
    
    /**
     * Limpia el log
     */
    public void limpiar() {
        log.clear();
    }
    
    /**
     * Imprime todo el log
     */
    public void imprimirLog() {
        System.out.println("=== LOG DE COMBATE ===");
        for (int i = 0; i < log.size(); i++) {
            System.out.println((i + 1) + ". " + log.get(i));
        }
    }
}
