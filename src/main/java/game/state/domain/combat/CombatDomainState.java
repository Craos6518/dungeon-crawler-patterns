package game.state.domain.combat;

import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.ai.strategy.AggressiveStrategy;
import game.ai.strategy.DefensiveStrategy;
import game.ai.strategy.RandomStrategy;
import game.command.actions.AttackCommand;
import game.command.actions.DefendCommand;
import game.command.actions.SkillCommand;
import game.command.actions.Command;
import game.domain.personaje.Personaje;
import game.events.observer.GameEvent;
import game.events.observer.EventType;
import game.events.observer.StatisticsTracker;
import game.items.model.SimpleItem;
import game.state.domain.AbstractDomainGameState;
import game.state.domain.GameSessionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Estado de dominio que encapsula toda la lógica de combate.
 * Responsable de:
 * - Loop de turnos de combate
 * - Decisiones del jugador (atacar, defenderse, usar objetos, habilidades)
 * - Acciones de IA del enemigo
 * - Efectos de veneno
 * - Mostrar pantalla de victoria/derrota
 * 
 * Completamente independiente y reutilizable.
 */
public class CombatDomainState extends AbstractDomainGameState {
    
    private final GameSessionData sessionData;
    private final Random random;
    private final StatisticsTracker statistics;
    private final DeathCallback deathCallback;
    private final TreasureCallback treasureCallback;
    private final CheckpointCallback checkpointCallback;
    
    private List<String> historialIA;
    private boolean vistaDebugIA; // Controlable externamente
    
    /**
     * Callback cuando el héroe muere
     */
    public interface DeathCallback {
        void alMorir();
    }
    
    /**
     * Callback para mostrar tesoro
     */
    public interface TreasureCallback {
        void mostrarTesoro();
    }
    
    /**
     * Callback para guardar checkpoint automático
     */
    public interface CheckpointCallback {
        void guardarCheckpoint();
    }
    
    public CombatDomainState(
        GameSessionData sessionData,
        Random random,
        StatisticsTracker statistics,
        DeathCallback deathCallback,
        TreasureCallback treasureCallback,
        CheckpointCallback checkpointCallback
    ) {
        this.sessionData = sessionData;
        this.random = random;
        this.statistics = statistics;
        this.deathCallback = deathCallback;
        this.treasureCallback = treasureCallback;
        this.checkpointCallback = checkpointCallback;
        this.historialIA = new ArrayList<>();
        this.vistaDebugIA = false;
    }
    
    public void setVistaDebugIA(boolean vistaDebugIA) {
        this.vistaDebugIA = vistaDebugIA;
    }
    
    @Override
    public boolean ejecutar() {
        // Este estado no se usa directamente como "ejecutar", 
        // iniciaCombate es llamado desde ExplorationDomainState
        return true;
    }
    
    @Override
    public String getNombreEstado() {
        return "Combate";
    }
    
    /**
     * Inicia un combate contra un enemigo
     * @param enemigo El enemigo a combatir
     * @param esJefe true si es el jefe final
     */
    public void iniciarCombate(Personaje enemigo, boolean esJefe) {
        historialIA.clear();
        sessionData.setDefensaHeroeActiva(false);

        AIStrategy estrategiaInicial = esJefe ? new AggressiveStrategy() : new RandomStrategy();
        AIController enemyAI = new AIController(enemigo, estrategiaInicial);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("⚔️  COMBATE");
        System.out.println("=".repeat(60));

        int turno = 1;
        while (sessionData.getHeroe().estaVivo() && enemigo.estaVivo()) {
            aplicarVenenoHeroeInicioTurno();
            if (!sessionData.getHeroe().estaVivo()) {
                break;
            }

            System.out.println("\n--- TURNO " + turno + " ---");
            System.out.println("Tu HP: " + sessionData.getHeroe().getVida() + " | " +
                enemigo.getNombre() + " HP: " + enemigo.getVida());

            if (sessionData.getTurnosVenenoHeroe() > 0) {
                System.out.println("Estado: ☠️ Envenenado (" + sessionData.getTurnosVenenoHeroe() + " turnos restantes)");
            }

            actualizarEstrategiaEnemiga(enemyAI, enemigo);
            if (vistaDebugIA) {
                mostrarVistaDebugIA(enemyAI, enemigo, turno);
            }

            System.out.println("\nTu turno:");
            System.out.println("1. Atacar");
            System.out.println("2. Defender");
            System.out.println("3. Usar objeto");
            System.out.println("4. Usar habilidad");

            int accion = leerOpcion(1, 4);

            manejarAccionHeroe(accion, enemigo, turno);

            if (!enemigo.estaVivo()) {
                break;
            }

            System.out.println("\nTurno del enemigo:");
            Command accionEnemiga = enemyAI.decidirAccion(List.of(sessionData.getHeroe()));
            commandInvoker.ejecutarComando(accionEnemiga);

            String nombreEstrategia = enemyAI.getEstrategia().getNombreEstrategia();
            historialIA.add("T" + turno + " " + nombreEstrategia + " -> " + accionEnemiga.getDescription());

            manejarAccionEnemigo(accionEnemiga, enemigo, nombreEstrategia);

            turno++;
        }

        // Determinar resultado del combate
        if (sessionData.getHeroe().estaVivo()) {
            System.out.println("\n🎉 ¡VICTORIA!");
            sessionData.setEnemigosDerrota(sessionData.getEnemigosDerrota() + 1);
            treasureCallback.mostrarTesoro();
            checkpointCallback.guardarCheckpoint();

            eventManager.notificar(new GameEvent(EventType.COMBATE_FINALIZADO)
                .agregarDato("ganador", sessionData.getHeroe().getNombre()));

            System.out.println("\n(Presiona Enter)");
            esperarEnterSiDisponible();
        } else {
            System.out.println("\n💀 HAS SIDO DERROTADO");
            mostrarDerrota();
            deathCallback.alMorir();
        }
    }
    
    private void manejarAccionHeroe(int accion, Personaje enemigo, int turno) {
        switch (accion) {
            case 1 -> {
                AttackCommand attackCommand = new AttackCommand(sessionData.getHeroe(), enemigo);
                commandInvoker.ejecutarComando(attackCommand);

                System.out.println("\n⚔️  " + sessionData.getHeroe().getNombre() + " ataca!");
                System.out.println("   Daño: " + attackCommand.getDanioAplicado());
                System.out.println("   HP enemigo: " + enemigo.getVida());

                eventManager.notificar(new GameEvent(EventType.ATAQUE_REALIZADO)
                    .agregarDato("atacante", sessionData.getHeroe().getNombre())
                    .agregarDato("defensor", enemigo.getNombre())
                    .agregarDato("danio", attackCommand.getDanioAplicado())
                    .agregarDato("vidaRestante", enemigo.getVida())
                    .agregarDato("ronda", turno));
            }
            case 2 -> {
                DefendCommand defendCommand = new DefendCommand(sessionData.getHeroe());
                commandInvoker.ejecutarComando(defendCommand);
                sessionData.setDefensaHeroeActiva(true);
                System.out.println("\n🛡️  " + sessionData.getHeroe().getNombre() + 
                    " se defiende! (reducción activa para el próximo golpe)");
            }
            case 3 -> usarConsumibleEnCombate();
            case 4 -> {
                String nombreHabilidad = "Golpe Especial";
                SkillCommand skillCommand = new SkillCommand(sessionData.getHeroe(), nombreHabilidad, enemigo);
                commandInvoker.ejecutarComando(skillCommand);

                int danioHabilidad = 35;
                enemigo.recibirDanio(danioHabilidad);
                System.out.println("\n✨ " + sessionData.getHeroe().getNombre() + " usa " + nombreHabilidad + "!");
                System.out.println("   Daño de habilidad: " + danioHabilidad);
                System.out.println("   HP enemigo: " + enemigo.getVida());

                eventManager.notificar(new GameEvent(EventType.ACCION_REALIZADA)
                    .agregarDato("personaje", sessionData.getHeroe().getNombre())
                    .agregarDato("accion", "habilidad")
                    .agregarDato("nombre", nombreHabilidad)
                    .agregarDato("ronda", turno));
            }
        }
    }
    
    private void manejarAccionEnemigo(Command accionEnemiga, Personaje enemigo, String nombreEstrategia) {
        if (accionEnemiga instanceof AttackCommand ataqueCommand) {
            if (sessionData.isDefensaHeroeActiva()) {
                int mitigado = Math.max(1, ataqueCommand.getDanioAplicado() / 2);
                sessionData.getHeroe().curar(mitigado);
                sessionData.setDefensaHeroeActiva(false);
                System.out.println("🛡️  Defensa activa: daño mitigado en " + mitigado + " puntos.");
            }

            aplicarVenenoPorAtaqueEnemigo();

            System.out.println("💥 " + enemigo.getNombre() + " ataca!");
            System.out.println("   Estrategia: " + nombreEstrategia);
            System.out.println("   Daño recibido: " + ataqueCommand.getDanioAplicado());
            System.out.println("   Tu HP: " + sessionData.getHeroe().getVida());
        } else if (accionEnemiga instanceof DefendCommand) {
            System.out.println("🛡️  " + enemigo.getNombre() + " adopta postura defensiva.");
        }
    }
    
    private void usarConsumibleEnCombate() {
        System.out.println("\n📦 Objetos disponibles:");
        SimpleItem pocion = buscarConsumiblePorTipo("poci");
        SimpleItem antidoto = buscarConsumiblePorTipo("antído");
        
        if (pocion == null && antidoto == null) {
            System.out.println("❌ No tienes consumibles disponibles.");
            return;
        }

        List<String> opciones = new ArrayList<>();
        opciones.add("0. Cancelar");
        if (pocion != null) {
            opciones.add("1. Usar Poción (+50 HP)");
        }
        if (antidoto != null) {
            opciones.add("2. Usar Antídoto (elimina veneno)");
        }

        for (String op : opciones) {
            System.out.println(op);
        }

        int maxOpcion = Math.min(2, opciones.size() - 1);
        int seleccion = leerOpcion(0, maxOpcion);

        switch (seleccion) {
            case 0 -> System.out.println("Cancelado.");
            case 1 -> {
                if (pocion != null) {
                    usarPocion();
                }
            }
            case 2 -> {
                if (antidoto != null) {
                    usarAntidoto(antidoto);
                }
            }
        }
    }
    
    private void usarPocion() {
        SimpleItem pocion = buscarConsumiblePorTipo("poci");
        if (pocion == null) {
            System.out.println("❌ No tienes pociones de vida disponibles.");
            return;
        }

        int hpAntes = sessionData.getHeroe().getVida();
        sessionData.getHeroe().curar(50);
        sessionData.getInventario().remover(pocion);

        System.out.println("💊 Usando Poción de Vida (+50 HP)");
        System.out.println("   HP antes: " + hpAntes);
        System.out.println("   HP después: " + sessionData.getHeroe().getVida());
    }
    
    private void usarAntidoto(SimpleItem antidoto) {
        if (sessionData.getTurnosVenenoHeroe() <= 0) {
            System.out.println("❌ No estás envenenado.");
            return;
        }

        sessionData.getInventario().remover(antidoto);
        sessionData.setTurnosVenenoHeroe(0);
        sessionData.setDanioVenenoHeroe(0);

        System.out.println("🧪 Usando Antídoto");
        System.out.println("✅ El veneno ha sido eliminado.");
    }
    
    private SimpleItem buscarConsumiblePorTipo(String textoParcial) {
        String criterio = normalizarTexto(textoParcial);
        for (var item : sessionData.getInventario().obtenerItems()) {
            if (item instanceof SimpleItem simpleItem) {
                String tipo = normalizarTexto(simpleItem.getTipo());
                if (tipo.contains("consum") && tipo.contains(criterio)) {
                    return simpleItem;
                }
            }
        }
        return null;
    }
    
    private String normalizarTexto(String texto) {
        String normalized = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase();
    }
    
    private void aplicarVenenoHeroeInicioTurno() {
        if (sessionData.getTurnosVenenoHeroe() <= 0) {
            return;
        }

        sessionData.getHeroe().recibirDanio(sessionData.getDanioVenenoHeroe());
        sessionData.setTurnosVenenoHeroe(sessionData.getTurnosVenenoHeroe() - 1);

        System.out.println("☠️  Sufres " + sessionData.getDanioVenenoHeroe() + 
            " de daño por veneno.");
    }
    
    private void aplicarVenenoPorAtaqueEnemigo() {
        if (random.nextInt(100) < 20 && 
            sessionData.getTemaActual().getNombreTema().toLowerCase().contains("veneno")) {
            
            sessionData.setTurnosVenenoHeroe(3);
            sessionData.setDanioVenenoHeroe(4);
            System.out.println("☠️  ¡Has sido envenenado! Usa antídoto para curarte.");

            eventManager.notificar(new GameEvent(EventType.EFECTO_APLICADO)
                .agregarDato("personaje", sessionData.getHeroe().getNombre())
                .agregarDato("efecto", "VENENO")
                .agregarDato("duracion", sessionData.getTurnosVenenoHeroe()));
        }
    }
    
    private void actualizarEstrategiaEnemiga(AIController enemyAI, Personaje enemigo) {
        int hp = enemigo.getVida();
        AIStrategy nueva;

        if (hp > 70) {
            nueva = new AggressiveStrategy();
        } else if (hp > 35) {
            nueva = new RandomStrategy();
        } else {
            nueva = new DefensiveStrategy();
        }

        if (!enemyAI.getEstrategia().getNombreEstrategia().equals(nueva.getNombreEstrategia())) {
            enemyAI.setEstrategia(nueva);
            eventManager.notificar(new GameEvent(EventType.ESTADO_CAMBIADO)
                .agregarDato("tipo", "estrategia")
                .agregarDato("nuevaEstrategia", nueva.getNombreEstrategia()));
        }
    }
    
    private void mostrarVistaDebugIA(AIController enemyAI, Personaje enemigo, int turno) {
        System.out.println("\n[DEBUG IA] " + enemigo.getNombre() +
            " | HP: " + enemigo.getVida() +
            " | Estrategia: " + enemyAI.getEstrategia().getNombreEstrategia());

        int desde = Math.max(0, historialIA.size() - 4);
        if (historialIA.isEmpty()) {
            System.out.println("[DEBUG IA] Historial: (sin decisiones previas)");
        } else {
            System.out.println("[DEBUG IA] Historial reciente:");
            for (int i = desde; i < historialIA.size(); i++) {
                System.out.println("  - " + historialIA.get(i));
            }
        }

        System.out.println("[DEBUG IA] Turno actual: " + turno);
    }
    
    private void mostrarDerrota() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💀 GAME OVER 💀");
        System.out.println("=".repeat(60));
        System.out.println(sessionData.getHeroe().getNombre() + " ha caído en combate...");
        System.out.println("Salas exploradas: " + sessionData.getSalaActual() + "/" 
            + sessionData.getMazmorra().getSalas().size());
        System.out.println("Enemigos derrotados: " + sessionData.getEnemigosDerrota());
        System.out.println("Oro acumulado: " + sessionData.getOroAcumulado());

        eventManager.notificar(new GameEvent(EventType.JUEGO_TERMINADO)
            .agregarDato("resultado", "Derrota"));

        mostrarEstadisticasFinales();
    }
    
    private void mostrarEstadisticasFinales() {
        System.out.println("\n📊 Estadísticas de esta partida:");
        System.out.println("   Ataques realizados: " + statistics.getAtaquesTotales());
        System.out.println("   Daño total causado: " + statistics.getDanioTotalCausado());
        if (commandInvoker != null && commandInvoker.getHistorial() != null) {
            System.out.println("   Comandos ejecutados: " + commandInvoker.getHistorial().size());
        }
    }
}
