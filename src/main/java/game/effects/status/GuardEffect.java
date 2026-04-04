package game.effects.status;

import game.domain.personaje.Personaje;

/**
 * Decorador de guardia que aporta mitigacion fija por acumulaciones activas.
 */
public class GuardEffect extends CharacterDecorator {

    private final int stacks;

    public GuardEffect(Personaje personaje, int stacks) {
        super(personaje);
        this.stacks = Math.max(0, stacks);
    }

    public int mitigarDanio(int incomingDamage) {
        int incoming = Math.max(0, incomingDamage);
        int mitigation = stacks * 3;
        return Math.min(incoming, Math.max(0, mitigation));
    }

    @Override
    public void aplicarEfecto() {
        // El buff de guardia se aplica al calcular mitigacion, no por tick.
    }

    @Override
    public String getDescripcionEfecto() {
        return "Guardia activa (stacks: " + stacks + ")";
    }
}
