package game.domain.personaje.factory;

import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;

/**
 * Factory Method - Implementación concreta para crear Guerreros
 */
public class GuerreroFactory implements PersonajeFactory {
    private final int vidaBase;
    private final int ataqueBase;

    public GuerreroFactory(int vidaBase, int ataqueBase) {
        this.vidaBase = vidaBase;
        this.ataqueBase = ataqueBase;
    }

    @Override
    public Personaje crearPersonaje(String nombre) {
        return new Guerrero(nombre, vidaBase, ataqueBase);
    }
}
