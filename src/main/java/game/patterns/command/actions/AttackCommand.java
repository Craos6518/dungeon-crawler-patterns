package game.patterns.command.actions;

import game.domain.personaje.Personaje;

import java.util.Objects;

/**
 * Command concreto para realizar un ataque entre personajes
 */
public class AttackCommand implements Command {
    @FunctionalInterface
    public interface AttackExecutor {
        int apply(Personaje atacante, Personaje defensor);
    }

    private final Personaje atacante;
    private final Personaje defensor;
    private final AttackExecutor attackExecutor;
    private int danioAplicado;
    
    public AttackCommand(Personaje atacante, Personaje defensor) {
        this(atacante, defensor, (source, target) -> source.atacar(target).danio());
    }

    public AttackCommand(Personaje atacante, Personaje defensor, AttackExecutor attackExecutor) {
        this.atacante = atacante;
        this.defensor = defensor;
        this.attackExecutor = Objects.requireNonNull(attackExecutor, "attackExecutor no puede ser null");
        this.danioAplicado = 0;
    }
    
    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException("No se puede ejecutar el ataque");
        }

        danioAplicado = Math.max(0, attackExecutor.apply(atacante, defensor));
    }
    
    @Override
    public void undo() {
        throw new UnsupportedOperationException(
            "AttackCommand no es reversible por diseño: el daño aplicado no conserva estado suficiente para undo seguro"
        );
    }
    
    @Override
    public boolean canExecute() {
        return atacante != null && 
               defensor != null && 
               atacante.estaVivo() && 
               defensor.estaVivo();
    }
    
    @Override
    public String getDescription() {
        return String.format("%s ataca a %s", 
            atacante.getNombre(), 
            defensor.getNombre());
    }
    
    public Personaje getAtacante() {
        return atacante;
    }
    
    public Personaje getDefensor() {
        return defensor;
    }
    
    public int getDanioAplicado() {
        return danioAplicado;
    }
}
