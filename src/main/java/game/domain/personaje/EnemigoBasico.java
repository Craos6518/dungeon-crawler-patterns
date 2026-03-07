package game.domain.personaje;

import game.combat.model.ResultadoAtaque;

public class EnemigoBasico extends Personaje {
    private final int ataqueBase;

    public EnemigoBasico(String nombre, int vidaInicial, int ataqueBase) {
        super(nombre, vidaInicial);
        this.ataqueBase = Math.max(1, ataqueBase);
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        int danio = ataqueBase;
        objetivo.recibirDanio(danio);
        return new ResultadoAtaque(getNombre(), objetivo.getNombre(), danio, objetivo.getVida());
    }
}
