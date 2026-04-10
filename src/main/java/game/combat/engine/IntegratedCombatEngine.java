package game.combat.engine;

import game.ai.strategy.AIController;
import game.ai.strategy.AIStrategy;
import game.patterns.command.actions.AttackCommand;
import game.patterns.command.actions.Command;
import game.patterns.command.actions.CommandInvoker;
import game.patterns.command.actions.DefendCommand;
import game.domain.personaje.Personaje;
import game.effects.status.CharacterDecorator;
import game.infrastructure.events.observer.EventManager;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de combate integrado que conecta todos los patrones de diseño:
 * 
 * - Command: Encapsula acciones de combate
 * - Strategy: IA intercambiable para enemigos
 * - Observer: Notifica eventos del combate
 * - Decorator: Aplica efectos de estado
 * - Facade: Simplifica la interacción
 * 
 * Este motor demuestra cómo múltiples patrones trabajan juntos
 * en un sistema cohesivo.
 */
public class IntegratedCombatEngine {
    private final Personaje heroe;
    private final Personaje enemigo;
    private final AIController aiController;
    private final CommandInvoker invoker;
    private final EventManager eventManager;
    
    private Personaje turnoActual;
    private int rondaActual;
    private boolean combateActivo;
    
    /**
     * Constructor que inicializa el motor con todos los componentes
     * 
     * @param heroe Personaje controlado por el jugador
     * @param enemigo Personaje controlado por IA
     * @param estrategiaIA Estrategia de IA del enemigo
     */
    public IntegratedCombatEngine(Personaje heroe, Personaje enemigo, AIStrategy estrategiaIA) {
        if (heroe == null || enemigo == null || estrategiaIA == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser null");
        }
        
        this.heroe = heroe;
        this.enemigo = enemigo;
        this.aiController = new AIController(enemigo, estrategiaIA);
        this.invoker = new CommandInvoker();
        this.eventManager = EventManager.getInstance();
        
        this.turnoActual = heroe; // El héroe siempre ataca primero
        this.rondaActual = 1;
        this.combateActivo = false;
    }
    
    /**
     * Inicia el combate y notifica el evento
     */
    public void iniciarCombate() {
        if (combateActivo) {
            throw new IllegalStateException("El combate ya está activo");
        }
        
        combateActivo = true;
        rondaActual = 1;
        
        // PATRÓN OBSERVER: Notificar inicio de combate
        GameEvent evento = new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", heroe.getNombre())
            .agregarDato("enemigo", enemigo.getNombre())
            .agregarDato("vidaHeroe", heroe.getVida())
            .agregarDato("vidaEnemigo", enemigo.getVida())
            .agregarDato("estrategia", aiController.getEstrategia().getNombreEstrategia());
        
        eventManager.notificar(evento);
    }
    
    /**
     * Ejecuta una ronda completa de combate utilizando el patrón Command
     * 
     * @return El comando ejecutado en esta ronda
     */
    public Command ejecutarRonda() {
        if (!combateActivo) {
            throw new IllegalStateException("El combate no está activo");
        }
        
        if (combateFinalizado()) {
            throw new IllegalStateException("El combate ya ha finalizado");
        }
        
        // Aplicar efectos de estado (PATRÓN DECORATOR)
        aplicarEfectosDeEstado(turnoActual);
        
        // Decidir y ejecutar acción (PATRÓN COMMAND + STRATEGY)
        Command comando = decidirAccion();
        
        // Ejecutar comando a través del invoker (PATRÓN COMMAND)
        invoker.ejecutarComando(comando);
        
        // PATRÓN OBSERVER: Notificar acción realizada
        notificarAccion(comando);
        
        // Verificar si el combate terminó
        if (combateFinalizado()) {
            finalizarCombate();
        } else {
            // Alternar turno
            alternarTurno();
        }
        
        return comando;
    }
    
    /**
     * Ejecuta el combate completo hasta que finalice
     * 
     * @return El ganador del combate
     */
    public Personaje ejecutarCombateCompleto() {
        iniciarCombate();
        
        while (!combateFinalizado()) {
            ejecutarRonda();
        }
        
        return obtenerGanador();
    }
    
    /**
     * Verifica si el combate ha finalizado
     */
    public boolean combateFinalizado() {
        return !heroe.estaVivo() || !enemigo.estaVivo();
    }
    
    /**
     * Obtiene el ganador del combate
     */
    public Personaje obtenerGanador() {
        if (!combateFinalizado()) {
            return null;
        }
        return heroe.estaVivo() ? heroe : enemigo;
    }
    
    /**
     * Obtiene el historial de comandos ejecutados
     */
    public List<Command> getHistorialComandos() {
        return new ArrayList<>(invoker.getHistorial());
    }
    
    /**
     * Obtiene la ronda actual
     */
    public int getRondaActual() {
        return rondaActual;
    }
    
    /**
     * Obtiene el personaje del turno actual
     */
    public Personaje getTurnoActual() {
        return turnoActual;
    }
    
    /**
     * Cambia la estrategia de IA del enemigo en tiempo de ejecución
     * (Demuestra flexibilidad del patrón Strategy)
     */
    public void cambiarEstrategiaIA(AIStrategy nuevaEstrategia) {
        aiController.setEstrategia(nuevaEstrategia);
        
        // PATRÓN OBSERVER: Notificar cambio de estrategia
        GameEvent evento = new GameEvent(EventType.ESTADO_CAMBIADO)
            .agregarDato("tipo", "estrategia")
            .agregarDato("nuevaEstrategia", nuevaEstrategia.getNombreEstrategia());
        
        eventManager.notificar(evento);
    }
    
    // ========== MÉTODOS PRIVADOS ==========
    
    /**
     * Decide qué acción tomar según quien tiene el turno
     * PATRONES: COMMAND + STRATEGY
     */
    private Command decidirAccion() {
        if (turnoActual == heroe) {
            // En esta demo, el héroe siempre ataca
            // En un juego real, esto vendría del input del usuario
            return new AttackCommand(heroe, enemigo);
        } else {
            // El enemigo usa su estrategia de IA (PATRÓN STRATEGY)
            List<Personaje> objetivos = List.of(heroe);
            return aiController.decidirAccion(objetivos);
        }
    }
    
    /**
     * Aplica efectos de estado al personaje
     * PATRÓN DECORATOR: Los efectos están envueltos como decoradores
     */
    private void aplicarEfectosDeEstado(Personaje personaje) {
        if (personaje instanceof CharacterDecorator) {
            CharacterDecorator decorator = (CharacterDecorator) personaje;
            decorator.aplicarEfecto();
            
            // PATRÓN OBSERVER: Notificar efecto aplicado
            GameEvent evento = new GameEvent(EventType.EFECTO_APLICADO)
                .agregarDato("personaje", personaje.getNombre())
                .agregarDato("efecto", decorator.getClass().getSimpleName());
            
            eventManager.notificar(evento);
        }
    }
    
    /**
     * Notifica la acción realizada
     * PATRÓN OBSERVER
     */
    private void notificarAccion(Command comando) {
        EventType tipo;
        GameEvent evento;
        
        if (comando instanceof AttackCommand) {
            AttackCommand ataque = (AttackCommand) comando;
            tipo = EventType.ATAQUE_REALIZADO;
            evento = new GameEvent(tipo)
                .agregarDato("atacante", ataque.getAtacante().getNombre())
                .agregarDato("defensor", ataque.getDefensor().getNombre())
                .agregarDato("danio", ataque.getDanioAplicado())
                .agregarDato("vidaRestante", ataque.getDefensor().getVida())
                .agregarDato("ronda", rondaActual);
        } else if (comando instanceof DefendCommand) {
            tipo = EventType.ACCION_REALIZADA;
            evento = new GameEvent(tipo)
                .agregarDato("accion", "defender")
                .agregarDato("personaje", turnoActual.getNombre())
                .agregarDato("ronda", rondaActual);
        } else {
            tipo = EventType.ACCION_REALIZADA;
            evento = new GameEvent(tipo)
                .agregarDato("accion", comando.getDescription())
                .agregarDato("ronda", rondaActual);
        }
        
        eventManager.notificar(evento);
        
        // Notificar si alguien murió
        if (!heroe.estaVivo()) {
            GameEvent muerte = new GameEvent(EventType.PERSONAJE_MUERTO)
                .agregarDato("personaje", heroe.getNombre());
            eventManager.notificar(muerte);
        }
        if (!enemigo.estaVivo()) {
            GameEvent muerte = new GameEvent(EventType.PERSONAJE_MUERTO)
                .agregarDato("personaje", enemigo.getNombre());
            eventManager.notificar(muerte);
        }
    }
    
    /**
     * Alterna el turno entre héroe y enemigo
     */
    private void alternarTurno() {
        turnoActual = (turnoActual == heroe) ? enemigo : heroe;
        
        if (turnoActual == heroe) {
            rondaActual++;
        }
    }
    
    /**
     * Finaliza el combate y notifica el resultado
     * PATRÓN OBSERVER
     */
    private void finalizarCombate() {
        combateActivo = false;
        Personaje ganador = obtenerGanador();
        
        GameEvent evento = new GameEvent(EventType.COMBATE_FINALIZADO)
            .agregarDato("ganador", ganador.getNombre())
            .agregarDato("rondasTotales", rondaActual)
            .agregarDato("comandosEjecutados", invoker.getHistorial().size());
        
        eventManager.notificar(evento);
    }
}
