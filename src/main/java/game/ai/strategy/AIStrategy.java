package game.ai.strategy;

import game.patterns.command.actions.Command;
import game.domain.personaje.Personaje;

import java.util.List;

/**
 * Strategy Pattern - Interfaz para estrategias de IA
 * 
 * Define el contrato para diferentes comportamientos de enemigos.
 * Permite cambiar el comportamiento de un personaje en tiempo de ejecución.
 */
public interface AIStrategy {
    /**
     * Decide y retorna el comando a ejecutar basándose en el estado actual
     * 
     * @param propio El personaje que ejecutará la acción
     * @param enemigos Lista de enemigos disponibles
     * @return El comando a ejecutar
     */
    Command decidirAccion(Personaje propio, List<Personaje> enemigos);
    
    /**
     * Retorna el nombre descriptivo de la estrategia
     */
    String getNombreEstrategia();
    
    /**
     * Retorna una descripción del comportamiento de la estrategia
     */
    String getDescripcion();
}
