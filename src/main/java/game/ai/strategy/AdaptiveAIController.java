package game.ai.strategy;

import game.command.actions.Command;
import game.domain.personaje.Personaje;

import java.util.List;

/**
 * Controlador de IA Adaptativo - Mejora del sistema de estrategias
 * 
 * Este controlador cambia dinámicamente la estrategia del enemigo basándose en:
 * - Vida actual vs vida máxima (ratios de supervivencia)
 * - Cantidad de enemigos enfrentados
 * - Historial de combates previos en el turno
 * 
 * Sistema de Umbrales Táticos:
 * - Vida > 75%: AGRESIVA (atacar al enemigo más fuerte)
 * - 50% <= Vida <= 75%: INTELIGENTE (análisis de situación)
 * - 25% <= Vida < 50%: DEFENSIVA (supervivencia prioritaria)
 * - Vida < 25%: DESESPERADA (ataque a enemigo débil para eliminar rápido)
 */
public class AdaptiveAIController {
    private final Personaje personaje;
    private AIStrategy estrategiaActual;
    private final AggressiveStrategy agresiva;
    private final DefensiveStrategy defensiva;
    private final IntelligentStrategy inteligente;
    private final RandomStrategy aleatoria;
    
    // Umbrales de vida para cambio de estrategia (en porcentaje)
    private static final double UMBRAL_AGRESIVO = 0.75;      // > 75% = Agresivo
    private static final double UMBRAL_INTELIGENTE = 0.50;   // 50-75% = Inteligente
    private static final double UMBRAL_DEFENSIVO = 0.25;     // 25-50% = Defensivo
    // < 25% = Desesperado (ataque a enemigos débiles)
    
    // Vida máxima del personaje (estimada)
    private final int vidaMaximaEstimada;
    
    public AdaptiveAIController(Personaje personaje, int vidaMaximaEstimada) {
        if (personaje == null) {
            throw new IllegalArgumentException("El personaje no puede ser null");
        }
        
        this.personaje = personaje;
        this.vidaMaximaEstimada = vidaMaximaEstimada;
        
        // Crear instancias de todas las estrategias disponibles
        this.agresiva = new AggressiveStrategy();
        this.defensiva = new DefensiveStrategy();
        this.inteligente = new IntelligentStrategy();
        this.aleatoria = new RandomStrategy();
        
        // Establecer estrategia inicial basada en vida actual
        this.estrategiaActual = agresiva;
    }
    
    /**
     * Decide la acción del personaje, adaptando la estrategia según su estado
     */
    public Command decidirAccion(List<Personaje> enemigos) {
        // Actualizar estrategia basada en estado actual
        actualizarEstrategiaAdaptativa();
        
        // Delegar la decisión a la estrategia actual
        return estrategiaActual.decidirAccion(personaje, enemigos);
    }
    
    /**
     * Actualiza la estrategia basándose en el porcentaje de vida actual
     */
    private void actualizarEstrategiaAdaptativa() {
        double porcentajeVida = (double) personaje.getVida() / vidaMaximaEstimada;
        AIStrategy nuevaEstrategia = estrategiaActual;
        
        if (porcentajeVida > UMBRAL_AGRESIVO) {
            // Vida alta: Atacar agresivamente
            nuevaEstrategia = agresiva;
        } else if (porcentajeVida > UMBRAL_INTELIGENTE) {
            // Vida media-alta: Estrategia balanceada e inteligente
            nuevaEstrategia = inteligente;
        } else if (porcentajeVida > UMBRAL_DEFENSIVO) {
            // Vida media-baja: Priorizar defensa
            nuevaEstrategia = defensiva;
        } else {
            // Vida muy baja: Intentar terminar combates rápido
            // Usar inteligente para aprovechar enemigos débiles
            nuevaEstrategia = inteligente;
        }
        
        // Cambiar de estrategia si es diferente
        if (nuevaEstrategia != estrategiaActual) {
            estrategiaActual = nuevaEstrategia;
        }
    }
    
    /**
     * Obtiene la estrategia actual
     */
    public AIStrategy getEstrategiaActual() {
        return estrategiaActual;
    }
    
    /**
     * Obtiene el porcentaje de vida actual
     */
    public double getPorcentajeVida() {
        return (double) personaje.getVida() / vidaMaximaEstimada;
    }
    
    /**
     * Obtiene el personaje controlado
     */
    public Personaje getPersonaje() {
        return personaje;
    }
    
    /**
     * Obtiene información de debug sobre el estado del controlador
     */
    public String getInfoDebug() {
        return String.format(
            "[AI Debug] %s | HP: %d/%d (%.0f%%) | Estrategia: %s",
            personaje.getNombre(),
            personaje.getVida(),
            vidaMaximaEstimada,
            getPorcentajeVida() * 100,
            estrategiaActual.getNombreEstrategia()
        );
    }
    
    @Override
    public String toString() {
        return String.format(
            "AdaptiveAIController[%s, Vida: %d/%d, Estrategia: %s]",
            personaje.getNombre(),
            personaje.getVida(),
            vidaMaximaEstimada,
            estrategiaActual.getNombreEstrategia()
        );
    }
}
