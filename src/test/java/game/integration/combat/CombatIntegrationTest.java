package game.integration.combat;

import game.combat.engine.MotorCombate;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatIntegrationTest {
    @Test
    void combateCompletoSeEjecutaHastaTenerGanador() {
        Guerrero guerrero = new Guerrero("Heroe", 60, 12);
        EnemigoBasico enemigo = new EnemigoBasico("Orco", 55, 8);
        MotorCombate motor = new MotorCombate(guerrero, enemigo);

        Personaje ganador = motor.iniciar();

        assertTrue(motor.combateFinalizado());
        assertNotNull(ganador);
        assertTrue(motor.getHistorial().size() > 0);
    }
}
