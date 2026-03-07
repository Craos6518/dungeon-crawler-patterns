package game.combat.facade;

import game.combat.engine.MotorCombate;
import game.combat.model.ResultadoAtaque;
import game.domain.personaje.Personaje;
import game.effects.status.CharacterDecorator;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade del patrón Facade.
 * Simplifica la interacción con el sistema de combate,
 * ocultando la complejidad del motor y los efectos de estado.
 */
public class CombatFacade {
    private MotorCombate motor;
    private final List<String> logCombate;
    private boolean combateEnCurso;

    public CombatFacade() {
        this.logCombate = new ArrayList<>();
        this.combateEnCurso = false;
    }

    /**
     * Inicia un combate entre dos personajes.
     * API simplificada que oculta la creación del motor.
     */
    public void iniciarCombate(Personaje heroe, Personaje enemigo) {
        if (combateEnCurso) {
            throw new IllegalStateException("Ya hay un combate en curso");
        }

        this.motor = new MotorCombate(heroe, enemigo);
        this.logCombate.clear();
        this.combateEnCurso = true;

        registrarLog("=== COMBATE INICIADO ===");
        registrarLog(String.format("%s vs %s", heroe.getNombre(), enemigo.getNombre()));
        registrarLog(String.format("HP: %d vs %d", heroe.getVida(), enemigo.getVida()));
        registrarLog("");
    }

    /**
     * Ejecuta una ronda de combate con aplicación automática de efectos.
     */
    public ResultadoAtaque ejecutarRonda() {
        if (!combateEnCurso) {
            throw new IllegalStateException("No hay combate en curso");
        }

        Personaje atacante = motor.getAtacanteActual();

        // Aplicar efectos de estado al inicio del turno
        aplicarEfectosDeEstado(atacante);

        // Ejecutar ataque si el personaje sigue vivo
        ResultadoAtaque resultado = null;
        if (atacante.estaVivo()) {
            resultado = motor.ejecutarRonda();
            registrarResultado(resultado);
        }

        // Verificar fin de combate
        if (motor.combateFinalizado()) {
            finalizarCombate();
        }

        return resultado;
    }

    /**
     * Ejecuta el combate completo hasta que uno de los personajes muera.
     */
    public Personaje ejecutarCombateCompleto() {
        if (!combateEnCurso) {
            throw new IllegalStateException("No hay combate en curso");
        }

        int ronda = 1;
        while (!motor.combateFinalizado()) {
            registrarLog(String.format("--- Ronda %d ---", ronda));
            ejecutarRonda();
            ronda++;
        }

        return motor.obtenerGanador();
    }

    /**
     * Verifica si el combate ha finalizado.
     */
    public boolean estaFinalizado() {
        return motor != null && motor.combateFinalizado();
    }

    /**
     * Obtiene el ganador del combate.
     */
    public Personaje obtenerGanador() {
        if (!estaFinalizado()) {
            return null;
        }
        return motor.obtenerGanador();
    }

    /**
     * Obtiene estadísticas simplificadas del combate.
     */
    public EstadisticasCombate obtenerEstadisticas() {
        if (motor == null) {
            return null;
        }

        List<ResultadoAtaque> historial = motor.getHistorial();
        int totalRondas = historial.size();
        int danioTotal = historial.stream()
            .mapToInt(ResultadoAtaque::danio)
            .sum();

        return new EstadisticasCombate(
            totalRondas,
            danioTotal,
            obtenerGanador()
        );
    }

    /**
     * Obtiene el log completo del combate.
     */
    public List<String> obtenerLogCombate() {
        return new ArrayList<>(logCombate);
    }

    /**
     * Imprime el log del combate en consola.
     */
    public void imprimirLog() {
        logCombate.forEach(System.out::println);
    }

    /**
     * Resetea el sistema para un nuevo combate.
     */
    public void reiniciar() {
        this.motor = null;
        this.logCombate.clear();
        this.combateEnCurso = false;
    }

    // ============ Métodos privados ============

    private void aplicarEfectosDeEstado(Personaje personaje) {
        if (personaje instanceof CharacterDecorator) {
            CharacterDecorator decorator = (CharacterDecorator) personaje;
            decorator.aplicarEfecto();
            registrarLog(String.format("  %s", decorator.getDescripcionEfecto()));
        }
    }

    private void registrarResultado(ResultadoAtaque resultado) {
        String mensaje = String.format(
            "%s ataca a %s → %d de daño (HP restante: %d)",
            resultado.atacante(),
            resultado.defensor(),
            resultado.danio(),
            resultado.vidaRestanteDefensor()
        );
        registrarLog(mensaje);
    }

    private void finalizarCombate() {
        Personaje ganador = motor.obtenerGanador();
        registrarLog("");
        registrarLog("=== COMBATE FINALIZADO ===");
        registrarLog(String.format("Ganador: %s", ganador.getNombre()));
        registrarLog(String.format("HP restante: %d", ganador.getVida()));
        combateEnCurso = false;
    }

    private void registrarLog(String mensaje) {
        logCombate.add(mensaje);
    }

    /**
     * Record para encapsular estadísticas del combate.
     */
    public record EstadisticasCombate(
        int totalRondas,
        int danioTotalInfligido,
        Personaje ganador
    ) {
        @Override
        public String toString() {
            return String.format(
                "Rondas: %d | Daño total: %d | Ganador: %s",
                totalRondas,
                danioTotalInfligido,
                ganador != null ? ganador.getNombre() : "N/A"
            );
        }
    }
}
