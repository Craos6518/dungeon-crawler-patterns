package game.domain.personaje;

import game.combat.model.ResultadoAtaque;

public class Arquero extends Personaje {
    private final int precision;

    public Arquero(String nombre, int vidaInicial, int precision) {
        super(nombre, vidaInicial);
        this.precision = Math.max(1, precision);
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        // El arquero tiene ataques de precisión
        int danio = precision;
        objetivo.recibirDanio(danio);
        return new ResultadoAtaque(getNombre(), objetivo.getNombre(), danio, objetivo.getVida());
    }

    public int getPrecision() {
        return precision;
    }
}
