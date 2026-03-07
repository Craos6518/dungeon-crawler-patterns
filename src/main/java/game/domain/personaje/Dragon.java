package game.domain.personaje;

import game.combat.model.ResultadoAtaque;

public class Dragon extends Personaje {
    private final int fuegoDragon;

    public Dragon(String nombre, int vidaInicial, int fuegoDragon) {
        super(nombre, vidaInicial);
        this.fuegoDragon = Math.max(1, fuegoDragon);
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        // El dragón lanza fuego devastador
        int danio = fuegoDragon;
        objetivo.recibirDanio(danio);
        return new ResultadoAtaque(getNombre(), objetivo.getNombre(), danio, objetivo.getVida());
    }

    public int getFuegoDragon() {
        return fuegoDragon;
    }
}
