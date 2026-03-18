package game.effects.status;

import game.combat.model.ResultadoAtaque;
import game.domain.personaje.Personaje;

/**
 * Decorador base del patrón Decorator.
 * Envuelve un Personaje y delega todas las operaciones a él,
 * permitiendo a subclases modificar comportamientos específicos.
 */
public abstract class CharacterDecorator extends Personaje {
    protected final Personaje personajeDecorado;

    protected CharacterDecorator(Personaje personaje) {
        super(personaje.getNombre(), personaje.getVida());
        this.personajeDecorado = personaje;
    }

    /**
     * Obtiene el personaje base (sin decoradores).
     */
    public Personaje getPersonajeBase() {
        Personaje base = personajeDecorado;
        while (base instanceof CharacterDecorator) {
            base = ((CharacterDecorator) base).personajeDecorado;
        }
        return base;
    }

    /**
     * Obtiene el personaje decorado directo.
     */
    protected Personaje getPersonajeDecorado() {
        return personajeDecorado;
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        return personajeDecorado.atacar(objetivo);
    }

    @Override
    public void recibirDanio(int cantidad) {
        personajeDecorado.recibirDanio(cantidad);
    }

    @Override
    public boolean estaVivo() {
        return personajeDecorado.estaVivo();
    }

    @Override
    public int getVida() {
        return personajeDecorado.getVida();
    }

    @Override
    public String getNombre() {
        return personajeDecorado.getNombre();
    }

    /**
     * Aplica el efecto al inicio de cada turno.
     * Las subclases deben implementar esto para definir su comportamiento.
     */
    public abstract void aplicarEfecto();

    /**
     * Obtiene la descripción del efecto aplicado.
     */
    public abstract String getDescripcionEfecto();
}
