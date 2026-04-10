package game.infrastructure.events.observer;

import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;
import game.application.ports.events.GameObserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Observer concreto - Recopila estadísticas del juego
 */
public class StatisticsTracker implements GameObserver {
    private int combatesRealizados;
    private int ataquesTotales;
    private int danioTotalCausado;
    private int personajesDerrotados;
    private int itemsUsados;
    private final Map<String, Integer> estadisticasPersonalizadas;
    
    public StatisticsTracker() {
        this.combatesRealizados = 0;
        this.ataquesTotales = 0;
        this.danioTotalCausado = 0;
        this.personajesDerrotados = 0;
        this.itemsUsados = 0;
        this.estadisticasPersonalizadas = new HashMap<>();
    }
    
    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case COMBATE_INICIADO -> combatesRealizados++;
            
            case ATAQUE_REALIZADO -> {
                ataquesTotales++;
                Object danio = evento.getDato("danio");
                if (danio instanceof Integer) {
                    danioTotalCausado += (Integer) danio;
                }
            }
            
            case PERSONAJE_MUERTO -> personajesDerrotados++;
            
            case ITEM_USADO -> itemsUsados++;
            
            default -> {}
        }
    }
    
    @Override
    public String getNombre() {
        return "StatisticsTracker";
    }
    
    /**
     * Obtiene el reporte completo de estadísticas
     */
    public String getReporte() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS DEL JUEGO ===\n");
        sb.append(String.format("Combates realizados: %d\n", combatesRealizados));
        sb.append(String.format("Ataques totales: %d\n", ataquesTotales));
        sb.append(String.format("Daño total causado: %d\n", danioTotalCausado));
        sb.append(String.format("Personajes derrotados: %d\n", personajesDerrotados));
        sb.append(String.format("Items usados: %d\n", itemsUsados));
        
        if (!estadisticasPersonalizadas.isEmpty()) {
            sb.append("\nEstadísticas personalizadas:\n");
            estadisticasPersonalizadas.forEach((k, v) -> 
                sb.append(String.format("  %s: %d\n", k, v))
            );
        }
        
        return sb.toString();
    }
    
    /**
     * Reinicia todas las estadísticas
     */
    public void reiniciar() {
        combatesRealizados = 0;
        ataquesTotales = 0;
        danioTotalCausado = 0;
        personajesDerrotados = 0;
        itemsUsados = 0;
        estadisticasPersonalizadas.clear();
    }
    
    // Getters
    public int getCombatesRealizados() { return combatesRealizados; }
    public int getAtaquesTotales() { return ataquesTotales; }
    public int getDanioTotalCausado() { return danioTotalCausado; }
    public int getPersonajesDerrotados() { return personajesDerrotados; }
    public int getItemsUsados() { return itemsUsados; }
}
