package game.domain.personaje.factory;

import game.domain.personaje.Mago;
import game.domain.personaje.Personaje;

/**
 * Factory Method - Implementación concreta para crear Magos
 */
public class MagoFactory implements PersonajeFactory {
    private final int vidaBase;
    private final int poderMagico;

    public MagoFactory(int vidaBase, int poderMagico) {
        this.vidaBase = vidaBase;
        this.poderMagico = poderMagico;
    }

    @Override
    public Personaje crearPersonaje(String nombre) {
        return new Mago(nombre, vidaBase, poderMagico);
    }
}
