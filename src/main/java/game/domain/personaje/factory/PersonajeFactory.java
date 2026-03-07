package game.domain.personaje.factory;

import game.domain.personaje.Personaje;

/**
 * Factory Method Pattern - Interfaz base para crear personajes
 * 
 * Permite desacoplar la creación de personajes de su uso.
 */
public interface PersonajeFactory {
    /**
     * Método factory que crea un personaje con nombre y estadísticas base.
     * 
     * @param nombre Nombre del personaje
     * @return Instancia del personaje creado
     */
    Personaje crearPersonaje(String nombre);
}
