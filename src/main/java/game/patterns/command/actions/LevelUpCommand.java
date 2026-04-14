package game.patterns.command.actions;

import game.domain.personaje.Personaje;

/**
 * Command para encapsular el sistema de experiencia y subida de nivel.
 */
public class LevelUpCommand implements Command {
    private final Personaje personaje;
    private final int experienciaGanada;
    private int nivelesGanados;

    public LevelUpCommand(Personaje personaje, int experienciaGanada) {
        this.personaje = personaje;
        this.experienciaGanada = experienciaGanada;
        this.nivelesGanados = 0;
    }

    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException("No se puede ejecutar LevelUpCommand");
        }

        personaje.ganarExperiencia(experienciaGanada);
        nivelesGanados = 0;

        while (personaje.getExperiencia() >= personaje.getNivel() * 100) {
            int xpRequerida = personaje.getNivel() * 100;
            personaje.ganarExperiencia(-xpRequerida);
            personaje.subirNivel();
            nivelesGanados++;
        }
    }

    @Override
    public boolean canExecute() {
        return personaje != null && personaje.estaVivo() && experienciaGanada > 0;
    }

    @Override
    public String getDescription() {
        return "Aplicar " + experienciaGanada + " XP a " + personaje.getNombre();
    }

    public int getNivelesGanados() {
        return nivelesGanados;
    }

    public int getExperienciaGanada() {
        return experienciaGanada;
    }
}