package game.unit.structural;

import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import game.effects.status.BurnEffect;
import game.effects.status.PoisonEffect;
import game.effects.status.StunEffect;
import game.effects.status.StrengthEffect;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del patrón Decorator aplicado al sistema de efectos de estado.
 * Verifica que los decoradores modifiquen correctamente el comportamiento de los personajes.
 */
class DecoratorPatternTest {

    @Test
    void poisonEffectAplicaDanioCadaTurno() {
        Personaje guerrero = new Guerrero("Héroe", 100, 10);
        PoisonEffect envenenado = new PoisonEffect(guerrero, 5, 3);

        assertEquals(100, envenenado.getVida());

        // Aplicar efecto - primer turno
        envenenado.aplicarEfecto();
        assertEquals(95, envenenado.getVida());

        // Aplicar efecto - segundo turno
        envenenado.aplicarEfecto();
        assertEquals(90, envenenado.getVida());

        // Aplicar efecto - tercer turno
        envenenado.aplicarEfecto();
        assertEquals(85, envenenado.getVida());

        // Aplicar efecto - cuarto turno (efecto ya expiró)
        envenenado.aplicarEfecto();
        assertEquals(85, envenenado.getVida()); // No debe reducir más
    }

    @Test
    void burnEffectAplicaDanioPorFuego() {
        Personaje guerrero = new Guerrero("Héroe", 100, 10);
        BurnEffect quemado = new BurnEffect(guerrero, 8, 2);

        quemado.aplicarEfecto();
        assertEquals(92, quemado.getVida());

        quemado.aplicarEfecto();
        assertEquals(84, quemado.getVida());
    }

    @Test
    void stunEffectImprideAtacar() {
        Personaje atacante = new Guerrero("Atacante", 100, 20);
        Personaje objetivo = new Guerrero("Objetivo", 100, 10);

        StunEffect aturdido = new StunEffect(atacante, 2);

        // Durante el aturdimiento, no debería hacer daño
        var resultado = aturdido.atacar(objetivo);
        assertEquals(0, resultado.danio());
        assertEquals(100, objetivo.getVida());

        // Aplicar efecto para decrementar duración
        aturdido.aplicarEfecto();
        assertTrue(aturdido.efectoActivo());

        aturdido.aplicarEfecto();
        assertFalse(aturdido.efectoActivo());

        // Después del aturdimiento, debería poder atacar normalmente
        var resultadoPostAturdimiento = aturdido.atacar(objetivo);
        assertTrue(resultadoPostAturdimiento.danio() > 0);
    }

    @Test
    void strengthEffectAmplificaDanio() {
        Personaje atacante = new Guerrero("Atacante", 100, 10);
        Personaje objetivo = new Guerrero("Objetivo", 100, 10);

        // Multiplicador de 2.0 = doble daño
        StrengthEffect fortalecido = new StrengthEffect(atacante, 2.0, 3);

        int vidaInicial = objetivo.getVida();
        var resultado = fortalecido.atacar(objetivo);

        // El daño debería ser aproximadamente el doble
        assertTrue(resultado.danio() > 10);

        // Confirmar que el objetivo recibió daño
        assertTrue(objetivo.getVida() < vidaInicial);
    }

    @Test
    void decoradoresSeEncadenanCorrectamente() {
        Personaje guerrero = new Guerrero("Héroe", 100, 10);

        // Aplicar múltiples efectos
        PoisonEffect envenenado = new PoisonEffect(guerrero, 3, 5);
        BurnEffect quemado = new BurnEffect(envenenado, 5, 3);

        // Aplicar ambos efectos
        quemado.aplicarEfecto(); // Aplica burn (5 daño)
        envenenado.aplicarEfecto(); // Aplica poison (3 daño)

        // Total: 100 - 5 - 3 = 92
        assertEquals(92, quemado.getVida());
    }

    @Test
    void decoradorMantieneReferenciaAlPersonajeBase() {
        Personaje guerrero = new Guerrero("Héroe", 100, 10);
        PoisonEffect envenenado = new PoisonEffect(guerrero, 5, 3);

        assertSame(guerrero, envenenado.getPersonajeBase());
        assertEquals("Héroe", envenenado.getPersonajeBase().getNombre());
    }

    @Test
    void efectoNoAplicaSiPersonajeMuere() {
        Personaje guerrero = new Guerrero("Héroe", 10, 10);
        PoisonEffect envenenado = new PoisonEffect(guerrero, 20, 5);

        envenenado.aplicarEfecto();
        // El veneno mata al personaje
        assertEquals(0, envenenado.getVida());
        assertFalse(envenenado.estaVivo());

        // Aplicar efecto nuevamente no debería cambiar nada
        envenenado.aplicarEfecto();
        assertEquals(0, envenenado.getVida());
    }

    @Test
    void nombrePersonajeIncluyeEfectosActivos() {
        Personaje guerrero = new Guerrero("Héroe", 100, 10);
        PoisonEffect envenenado = new PoisonEffect(guerrero, 5, 2);

        assertTrue(envenenado.getNombre().contains("Envenenado"));
        assertTrue(envenenado.efectoActivo());

        // Agotar el efecto
        envenenado.aplicarEfecto();
        envenenado.aplicarEfecto();
        assertFalse(envenenado.efectoActivo());
    }
}
