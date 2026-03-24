package game.domain.personaje;

import game.combat.model.ResultadoAtaque;

public abstract class Personaje {
    private final String nombre;
    private int vida;
    private int vidaMaxima;
    private int nivel;
    private int experiencia;

    protected Personaje(String nombre, int vidaInicial) {
        this.nombre = nombre;
        this.vida = Math.max(0, vidaInicial);
        this.vidaMaxima = this.vida;
        this.nivel = 1;
        this.experiencia = 0;
    }

    public abstract ResultadoAtaque atacar(Personaje objetivo);

    public void recibirDanio(int cantidad) {
        int danioAplicado = Math.max(0, cantidad);
        vida = Math.max(0, vida - danioAplicado);
    }

    public void curar(int cantidad) {
        int curacion = Math.max(0, cantidad);
        vida = Math.min(vidaMaxima, vida + curacion);
    }

    public void ganarExperiencia(int xp) {
        this.experiencia += xp;
    }

    public void subirNivel() {
        this.nivel++;
        this.vidaMaxima += 20;
        this.vida = this.vidaMaxima;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public int getVida() {
        return vida;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public void setExperienciaOtorgada(int xp) {
        this.experiencia = xp;
    }

    public int getExperienciaOtorgada() {
        return experiencia;
    }

    public String getNombre() {
        return nombre;
    }
}
