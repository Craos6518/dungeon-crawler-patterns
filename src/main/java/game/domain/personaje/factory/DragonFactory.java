package game.domain.personaje.factory;

import game.domain.personaje.Dragon;
import game.domain.personaje.Personaje;

/**
 * Factory Method - Implementación concreta para crear Dragones
 */
public class DragonFactory implements PersonajeFactory {
    private final int vidaBase;
    private final int fuegoDragon;

    public DragonFactory(int vidaBase, int fuegoDragon) {
        this.vidaBase = vidaBase;
        this.fuegoDragon = fuegoDragon;
    }

    @Override
    public Personaje crearPersonaje(String nombre) {
        return new Dragon(nombre, vidaBase, fuegoDragon);
    }
}
