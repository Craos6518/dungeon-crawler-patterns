package game.domain.personaje;

import game.combat.model.ResultadoAtaque;

public class Mago extends Personaje {
    private final int poderMagico;

    public Mago(String nombre, int vidaInicial, int poderMagico) {
        super(nombre, vidaInicial);
        this.poderMagico = Math.max(1, poderMagico);
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        // El mago hace daño mágico (mayor poder pero menos vida)
        int danio = poderMagico;
        objetivo.recibirDanio(danio);
        return new ResultadoAtaque(getNombre(), objetivo.getNombre(), danio, objetivo.getVida());
    }

    public int getPoderMagico() {
        return poderMagico;
    }
}
