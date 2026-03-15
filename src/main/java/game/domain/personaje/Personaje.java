package game.domain.personaje;

import game.combat.model.ResultadoAtaque;

public abstract class Personaje {
    private final String nombre;
    private int vida;

    protected Personaje(String nombre, int vidaInicial) {
        this.nombre = nombre;
        this.vida = Math.max(0, vidaInicial);
    }

    public abstract ResultadoAtaque atacar(Personaje objetivo);

    public void recibirDanio(int cantidad) {
        int danioAplicado = Math.max(0, cantidad);
        vida = Math.max(0, vida - danioAplicado);
    }

    public void curar(int cantidad) {
        int curacion = Math.max(0, cantidad);
        vida += curacion;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public int getVida() {
        return vida;
    }

    public String getNombre() {
        return nombre;
    }
}
