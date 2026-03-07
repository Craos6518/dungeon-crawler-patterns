package game.combat.engine;

import game.combat.model.ResultadoAtaque;
import game.domain.personaje.Personaje;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MotorCombate {
    private final Personaje personajeA;
    private final Personaje personajeB;
    private Personaje atacanteActual;
    private Personaje defensorActual;
    private final List<ResultadoAtaque> historial;

    public MotorCombate(Personaje personajeA, Personaje personajeB) {
        this.personajeA = personajeA;
        this.personajeB = personajeB;
        this.atacanteActual = personajeA;
        this.defensorActual = personajeB;
        this.historial = new ArrayList<>();
    }

    public Personaje iniciar() {
        while (!combateFinalizado()) {
            ejecutarRonda();
        }
        return obtenerGanador();
    }

    public ResultadoAtaque ejecutarRonda() {
        ResultadoAtaque resultado = atacanteActual.atacar(defensorActual);
        historial.add(resultado);
        if (!combateFinalizado()) {
            alternarTurno();
        }
        return resultado;
    }

    public boolean combateFinalizado() {
        return !personajeA.estaVivo() || !personajeB.estaVivo();
    }

    public Personaje obtenerGanador() {
        if (!combateFinalizado()) {
            return null;
        }
        return personajeA.estaVivo() ? personajeA : personajeB;
    }

    public Personaje getAtacanteActual() {
        return atacanteActual;
    }

    public List<ResultadoAtaque> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    private void alternarTurno() {
        Personaje temporal = atacanteActual;
        atacanteActual = defensorActual;
        defensorActual = temporal;
    }
}
