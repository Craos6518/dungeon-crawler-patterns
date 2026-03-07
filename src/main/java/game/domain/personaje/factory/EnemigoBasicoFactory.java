package game.domain.personaje.factory;

import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Personaje;

/**
 * Factory Method - Implementación concreta para crear Enemigos Básicos
 */
public class EnemigoBasicoFactory implements PersonajeFactory {
    private final int vidaBase;
    private final int ataqueBase;

    public EnemigoBasicoFactory(int vidaBase, int ataqueBase) {
        this.vidaBase = vidaBase;
        this.ataqueBase = ataqueBase;
    }

    @Override
    public Personaje crearPersonaje(String nombre) {
        return new EnemigoBasico(nombre, vidaBase, ataqueBase);
    }
}
