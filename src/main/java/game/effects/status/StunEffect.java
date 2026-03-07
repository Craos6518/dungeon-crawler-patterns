package game.effects.status;

import game.combat.model.ResultadoAtaque;
import game.domain.personaje.Personaje;

/**
 * Decorador concreto que aplica el efecto de Aturdimiento.
 * El personaje no puede atacar durante la duración del efecto.
 */
public class StunEffect extends CharacterDecorator {
    private int turnosRestantes;

    public StunEffect(Personaje personaje, int duracion) {
        super(personaje);
        this.turnosRestantes = Math.max(1, duracion);
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        if (turnosRestantes > 0) {
            // El personaje está aturdido, no puede atacar
            return new ResultadoAtaque(this.getNombre(), objetivo.getNombre(), 0, objetivo.getVida());
        }
        return personajeDecorado.atacar(objetivo);
    }

    @Override
    public void aplicarEfecto() {
        if (turnosRestantes > 0) {
            turnosRestantes--;
        }
    }

    @Override
    public String getDescripcionEfecto() {
        return String.format("💫 Aturdido (no puede atacar, %d turnos restantes)", 
            turnosRestantes);
    }

    /**
     * Verifica si el efecto sigue activo.
     */
    public boolean efectoActivo() {
        return turnosRestantes > 0;
    }

    public int getTurnosRestantes() {
        return turnosRestantes;
    }

    @Override
    public String getNombre() {
        if (efectoActivo()) {
            return personajeDecorado.getNombre() + " [Aturdido]";
        }
        return personajeDecorado.getNombre();
    }
}
