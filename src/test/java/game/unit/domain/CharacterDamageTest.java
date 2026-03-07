package game.unit.domain;

import game.domain.personaje.Guerrero;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharacterDamageTest {
    @Test
    void recibirDanioReduceVidaYSinValoresNegativos() {
        Guerrero guerrero = new Guerrero("Heroe", 50, 10);

        guerrero.recibirDanio(20);
        assertEquals(30, guerrero.getVida());

        guerrero.recibirDanio(100);
        assertEquals(0, guerrero.getVida());
    }
}
