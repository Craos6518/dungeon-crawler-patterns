package game.effects.status;

import game.domain.personaje.Personaje;

/**
 * Decorador concreto que aplica el efecto de Quemadura.
 * El personaje recibe daño por fuego al inicio de cada turno.
 */
public class BurnEffect extends CharacterDecorator {
    private final int danoPorTurno;
    private int turnosRestantes;

    public BurnEffect(Personaje personaje, int danoPorTurno, int duracion) {
        super(personaje);
        this.danoPorTurno = Math.max(1, danoPorTurno);
        this.turnosRestantes = Math.max(1, duracion);
    }

    @Override
    public void aplicarEfecto() {
        if (turnosRestantes > 0 && estaVivo()) {
            personajeDecorado.recibirDanio(danoPorTurno);
            turnosRestantes--;
        }
    }

    @Override
    public String getDescripcionEfecto() {
        return String.format("🔥 Quemándose (-%d HP por turno, %d turnos restantes)", 
            danoPorTurno, turnosRestantes);
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
            return personajeDecorado.getNombre() + " [Quemándose]";
        }
        return personajeDecorado.getNombre();
    }
}
