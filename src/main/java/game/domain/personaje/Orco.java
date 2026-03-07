package game.domain.personaje;

import game.combat.model.ResultadoAtaque;

public class Orco extends Personaje {
    private final int fuerza;

    public Orco(String nombre, int vidaInicial, int fuerza) {
        super(nombre, vidaInicial);
        this.fuerza = Math.max(1, fuerza);
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        // El orco tiene mucha fuerza bruta
        int danio = fuerza;
        objetivo.recibirDanio(danio);
        return new ResultadoAtaque(getNombre(), objetivo.getNombre(), danio, objetivo.getVida());
    }

    public int getFuerza() {
        return fuerza;
    }
}
