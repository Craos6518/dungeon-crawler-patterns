package game.domain.personaje.factory;

import game.domain.personaje.Arquero;
import game.domain.personaje.Personaje;

/**
 * Factory Method - Implementación concreta para crear Arqueros
 */
public class ArqueroFactory implements PersonajeFactory {
    private final int vidaBase;
    private final int precision;

    public ArqueroFactory(int vidaBase, int precision) {
        this.vidaBase = vidaBase;
        this.precision = precision;
    }

    @Override
    public Personaje crearPersonaje(String nombre) {
        return new Arquero(nombre, vidaBase, precision);
    }
}
