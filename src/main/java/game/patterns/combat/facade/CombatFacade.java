package game.patterns.combat.facade;

import game.combat.engine.MotorCombate;
import game.combat.model.ResultadoAtaque;
import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.combat.CombatResult;
import game.domain.combat.PlayerCombatStyle;
import game.domain.inventory.Item;
import game.domain.personaje.Personaje;
import game.domain.turn.TurnManager;
import game.effects.status.CharacterDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Facade productiva del subsistema de combate.
 *
 * Expone una API estable para el runtime y encapsula el agregado {@link Combat}
 * junto a su colaborador legacy de rondas estructurales.
 */
public class CombatFacade {
    private final Combat combat;

    // Soporte legacy para tests estructurales que validan ejecutarRonda.
    private MotorCombate legacyMotor;
    private final List<String> legacyCombatLog;
    private boolean legacyCombatInProgress;

    public CombatFacade() {
        this.combat = null;
        this.legacyCombatLog = new ArrayList<>();
        this.legacyCombatInProgress = false;
    }

    public CombatFacade(Player player, TurnManager turnManager, Random random) {
        this(new Combat(
            Objects.requireNonNull(player, "player"),
            Objects.requireNonNull(turnManager, "turnManager"),
            Objects.requireNonNull(random, "random")
        ));
    }

    public CombatFacade(Combat combat) {
        this.combat = Objects.requireNonNull(combat, "combat");
        this.legacyCombatLog = new ArrayList<>();
        this.legacyCombatInProgress = false;
    }

    // ==============================
    // API productiva (runtime real)
    // ==============================

    public void start(Enemy enemy, boolean bossFight) {
        requireRuntimeCombat().start(enemy, bossFight);
    }

    public void finish() {
        requireRuntimeCombat().finish();
    }

    public boolean isActive() {
        return requireRuntimeCombat().isActive();
    }

    public Enemy currentEnemy() {
        return requireRuntimeCombat().currentEnemy();
    }

    public boolean isBossFight() {
        return requireRuntimeCombat().isBossFight();
    }

    public PlayerCombatStyle playerStyle() {
        return requireRuntimeCombat().playerStyle();
    }

    public int offensiveBuffStacks() {
        return requireRuntimeCombat().offensiveBuffStacks();
    }

    public int guardBuffStacks() {
        return requireRuntimeCombat().guardBuffStacks();
    }

    public boolean hasTacticalCheckpoint() {
        return requireRuntimeCombat().hasTacticalCheckpoint();
    }

    public boolean tacticalCheckpointConsumed() {
        return requireRuntimeCombat().tacticalCheckpointConsumed();
    }

    public Combat.TacticalCheckpoint tacticalCheckpoint() {
        return requireRuntimeCombat().tacticalCheckpoint();
    }

    public boolean isDefenseActive() {
        return requireRuntimeCombat().isDefenseActive();
    }

    public int poisonTurns() {
        return requireRuntimeCombat().poisonTurns();
    }

    public int poisonDamage() {
        return requireRuntimeCombat().poisonDamage();
    }

    public void restoreTurnState(boolean defenseActive, int poisonTurns, int poisonDamage) {
        requireRuntimeCombat().restoreTurnState(defenseActive, poisonTurns, poisonDamage);
    }

    public void restoreActiveEnemy(Enemy enemy, boolean bossFight) {
        requireRuntimeCombat().restoreActiveEnemy(enemy, bossFight);
    }

    public void restoreTacticalState(
        String styleKey,
        int offensiveStacks,
        int guardStacks,
        Combat.TacticalCheckpoint checkpoint,
        boolean checkpointConsumed
    ) {
        requireRuntimeCombat().restoreTacticalState(
            styleKey,
            offensiveStacks,
            guardStacks,
            checkpoint,
            checkpointConsumed
        );
    }

    public CombatResult attack(String targetId, String themeKey) {
        return requireRuntimeCombat().attack(targetId, themeKey);
    }

    public CombatResult defend(String themeKey) {
        return requireRuntimeCombat().defend(themeKey);
    }

    public CombatResult useSkill(String requestedSkillName, String themeKey) {
        return requireRuntimeCombat().useSkill(requestedSkillName, themeKey);
    }

    public CombatResult useItem(Item item, String themeKey) {
        return requireRuntimeCombat().useItem(item, themeKey);
    }

    public CombatResult retreatAttempt(String heroType, String themeKey) {
        return requireRuntimeCombat().retreatAttempt(heroType, themeKey);
    }

    public CombatResult setCombatStyle(String requestedStyle, String themeKey) {
        return requireRuntimeCombat().setCombatStyle(requestedStyle, themeKey);
    }

    public CombatResult applyStackingBuff(String requestedBuffType, String themeKey) {
        return requireRuntimeCombat().applyStackingBuff(requestedBuffType, themeKey);
    }

    public CombatResult applyBuff(String requestedBuffType, String themeKey) {
        return applyStackingBuff(requestedBuffType, themeKey);
    }

    public CombatResult saveTacticalCheckpoint() {
        return requireRuntimeCombat().saveTacticalCheckpoint();
    }

    public CombatResult rollbackTacticalCheckpoint() {
        return requireRuntimeCombat().rollbackTacticalCheckpoint();
    }

    public void resolveTurn() {
        requireRuntimeCombat().resolveTurn();
    }

    public boolean isCombatOver() {
        return !isActive();
    }

    public CombatStatusSnapshot getStatus() {
        Combat runtimeCombat = requireRuntimeCombat();
        Enemy enemy = runtimeCombat.currentEnemy();
        return new CombatStatusSnapshot(
            runtimeCombat.isActive(),
            runtimeCombat.isBossFight(),
            runtimeCombat.playerStyle().key(),
            runtimeCombat.offensiveBuffStacks(),
            runtimeCombat.guardBuffStacks(),
            runtimeCombat.isDefenseActive(),
            runtimeCombat.poisonTurns(),
            runtimeCombat.poisonDamage(),
            enemy == null ? null : enemy.name(),
            enemy == null ? 0 : enemy.hp()
        );
    }

    // ===========================================
    // API legacy (tests estructurales existentes)
    // ===========================================

    public void iniciarCombate(Personaje heroe, Personaje enemigo) {
        if (legacyCombatInProgress) {
            throw new IllegalStateException("Ya hay un combate en curso");
        }

        this.legacyMotor = new MotorCombate(heroe, enemigo);
        this.legacyCombatLog.clear();
        this.legacyCombatInProgress = true;

        registrarLegacyLog("=== COMBATE INICIADO ===");
        registrarLegacyLog(String.format("%s vs %s", heroe.getNombre(), enemigo.getNombre()));
        registrarLegacyLog(String.format("HP: %d vs %d", heroe.getVida(), enemigo.getVida()));
        registrarLegacyLog("");
    }

    public ResultadoAtaque ejecutarRonda() {
        if (!legacyCombatInProgress) {
            throw new IllegalStateException("No hay combate en curso");
        }

        Personaje atacante = legacyMotor.getAtacanteActual();

        // Aplicar efectos de estado al inicio del turno.
        aplicarEfectosDeEstado(atacante);

        ResultadoAtaque resultado = null;
        if (atacante.estaVivo()) {
            resultado = legacyMotor.ejecutarRonda();
            registrarLegacyResultado(resultado);
        }

        if (legacyMotor.combateFinalizado()) {
            finalizarLegacyCombate();
        }

        return resultado;
    }

    public Personaje ejecutarCombateCompleto() {
        if (!legacyCombatInProgress) {
            throw new IllegalStateException("No hay combate en curso");
        }

        int ronda = 1;
        while (!legacyMotor.combateFinalizado()) {
            registrarLegacyLog(String.format("--- Ronda %d ---", ronda));
            ejecutarRonda();
            ronda++;
        }

        return legacyMotor.obtenerGanador();
    }

    public boolean estaFinalizado() {
        return legacyMotor != null && legacyMotor.combateFinalizado();
    }

    public Personaje obtenerGanador() {
        if (!estaFinalizado()) {
            return null;
        }
        return legacyMotor.obtenerGanador();
    }

    public EstadisticasCombate obtenerEstadisticas() {
        if (legacyMotor == null) {
            return null;
        }

        List<ResultadoAtaque> historial = legacyMotor.getHistorial();
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

    public List<String> obtenerLogCombate() {
        return new ArrayList<>(legacyCombatLog);
    }

    public void imprimirLog() {
        legacyCombatLog.forEach(System.out::println);
    }

    public void reiniciar() {
        this.legacyMotor = null;
        this.legacyCombatLog.clear();
        this.legacyCombatInProgress = false;
    }

    private void aplicarEfectosDeEstado(Personaje personaje) {
        if (personaje instanceof CharacterDecorator) {
            CharacterDecorator decorator = (CharacterDecorator) personaje;
            decorator.aplicarEfecto();
            registrarLegacyLog(String.format("  %s", decorator.getDescripcionEfecto()));
        }
    }

    private void registrarLegacyResultado(ResultadoAtaque resultado) {
        String mensaje = String.format(
            "%s ataca a %s → %d de daño (HP restante: %d)",
            resultado.atacante(),
            resultado.defensor(),
            resultado.danio(),
            resultado.vidaRestanteDefensor()
        );
        registrarLegacyLog(mensaje);
    }

    private void finalizarLegacyCombate() {
        Personaje ganador = legacyMotor.obtenerGanador();
        registrarLegacyLog("");
        registrarLegacyLog("=== COMBATE FINALIZADO ===");
        registrarLegacyLog(String.format("Ganador: %s", ganador.getNombre()));
        registrarLegacyLog(String.format("HP restante: %d", ganador.getVida()));
        legacyCombatInProgress = false;
    }

    private void registrarLegacyLog(String mensaje) {
        legacyCombatLog.add(mensaje);
    }

    private Combat requireRuntimeCombat() {
        if (combat == null) {
            throw new IllegalStateException("CombatFacade sin contexto runtime inicializado.");
        }
        return combat;
    }

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

    public record CombatStatusSnapshot(
        boolean active,
        boolean bossFight,
        String style,
        int offensiveBuffStacks,
        int guardBuffStacks,
        boolean defenseActive,
        int poisonTurns,
        int poisonDamage,
        String enemyName,
        int enemyHp
    ) {
    }
}
