package game.domain.personaje.factory;

import game.domain.personaje.Orco;
import game.domain.personaje.Personaje;

/**
 * Factory Method - Implementación concreta para crear Orcos
 */
public class OrcoFactory implements PersonajeFactory {
    private final int vidaBase;
    private final int fuerza;

    public OrcoFactory(int vidaBase, int fuerza) {
        this.vidaBase = vidaBase;
        this.fuerza = fuerza;
    }

    @Override
    public Personaje crearPersonaje(String nombre) {
        return new Orco(nombre, vidaBase, fuerza);
    }
}
