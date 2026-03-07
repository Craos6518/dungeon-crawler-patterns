package game.unit.combat;

import game.combat.engine.MotorCombate;
import game.combat.model.ResultadoAtaque;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatTurnAlternationTest {
    @Test
    void motorAlternaTurnosEnCadaRonda() {
        Guerrero guerrero = new Guerrero("Heroe", 100, 10);
        EnemigoBasico enemigo = new EnemigoBasico("Goblin", 100, 5);
        MotorCombate motor = new MotorCombate(guerrero, enemigo);

        ResultadoAtaque ronda1 = motor.ejecutarRonda();
        ResultadoAtaque ronda2 = motor.ejecutarRonda();

        assertEquals("Heroe", ronda1.atacante());
        assertEquals("Goblin", ronda2.atacante());
    }
}
