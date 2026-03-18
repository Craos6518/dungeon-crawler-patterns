package game.effects.status;

import game.combat.model.ResultadoAtaque;
import game.domain.personaje.Personaje;

/**
 * Decorador concreto que aplica el efecto de Fortalecimiento.
 * El personaje inflige más daño durante la duración del efecto.
 */
public class StrengthEffect extends CharacterDecorator {
    private final double multiplicadorDanio;
    private int turnosRestantes;

    public StrengthEffect(Personaje personaje, double multiplicadorDanio, int duracion) {
        super(personaje);
        this.multiplicadorDanio = Math.max(1.0, multiplicadorDanio);
        this.turnosRestantes = Math.max(1, duracion);
    }

    @Override
    public ResultadoAtaque atacar(Personaje objetivo) {
        ResultadoAtaque resultado = personajeDecorado.atacar(objetivo);
        
        if (turnosRestantes > 0) {
            // Aplicar bonus de daño
            int danioOriginal = resultado.danio();
            int danioMejorado = (int) (danioOriginal * multiplicadorDanio);
            int danioAdicional = danioMejorado - danioOriginal;
            
            if (danioAdicional > 0) {
                objetivo.recibirDanio(danioAdicional);
            }
            
            return new ResultadoAtaque(
                resultado.atacante(),
                resultado.defensor(),
                danioMejorado,
                objetivo.getVida()
            );
        }
        
        return resultado;
    }

    @Override
    public void aplicarEfecto() {
        if (turnosRestantes > 0) {
            turnosRestantes--;
        }
    }

    @Override
    public String getDescripcionEfecto() {
        return String.format("💪 Fortalecido (x%.1f daño, %d turnos restantes)", 
            multiplicadorDanio, turnosRestantes);
    }

    /**
     * Verifica si el efecto sigue activo.
     */
    public boolean efectoActivo() {
        return turnosRestantes > 0;
    }

    public int getTurnosRestantes() {
        return turnosRestantes;
    }

    @Override
    public String getNombre() {
        if (efectoActivo()) {
            return personajeDecorado.getNombre() + " [Fortalecido]";
        }
        return personajeDecorado.getNombre();
    }
}
