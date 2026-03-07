package game.unit.combat;

import game.combat.engine.MotorCombate;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatEndTest {
    @Test
    void motorFinalizaCuandoUnPersonajeMuere() {
        Guerrero guerrero = new Guerrero("Heroe", 30, 30);
        EnemigoBasico enemigo = new EnemigoBasico("Goblin", 20, 1);
        MotorCombate motor = new MotorCombate(guerrero, enemigo);

        Personaje ganador = motor.iniciar();

        assertTrue(motor.combateFinalizado());
        assertNotNull(ganador);
    }
}
