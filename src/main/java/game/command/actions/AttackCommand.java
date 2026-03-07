package game.command.actions;

import game.domain.personaje.Personaje;

/**
 * Command concreto para realizar un ataque entre personajes
 */
public class AttackCommand implements Command {
    private final Personaje atacante;
    private final Personaje defensor;
    private int danioAplicado;
    
    public AttackCommand(Personaje atacante, Personaje defensor) {
        this.atacante = atacante;
        this.defensor = defensor;
        this.danioAplicado = 0;
    }
    
    @Override
    public void execute() {
        if (!canExecute()) {
            throw new IllegalStateException("No se puede ejecutar el ataque");
        }
        
        var resultado = atacante.atacar(defensor);
        danioAplicado = resultado.danio();
    }
    
    @Override
    public void undo() {
        // Restaurar la vida del defensor
        // Nota: Esta es una implementación simplificada
        // En un sistema real, necesitarías guardar más estado
        throw new UnsupportedOperationException(
            "Deshacer ataques no está implementado en esta versión"
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
