package game.unit.structural;

import game.patterns.combat.facade.CombatFacade;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del patrón Facade aplicado al sistema de combate.
 * Verifica que la fachada simplifique correctamente la interacción con el motor de combate.
 */
class FacadePatternTest {

    private CombatFacade facade;

    @BeforeEach
    void setUp() {
        facade = new CombatFacade();
    }

    @Test
    void facadePermiteIniciarCombateFacilmente() {
        Personaje heroe = new Guerrero("Héroe", 100, 20);
        Personaje enemigo = new EnemigoBasico("Goblin", 50, 10);

        assertDoesNotThrow(() -> {
            facade.iniciarCombate(heroe, enemigo);
        });

        assertFalse(facade.estaFinalizado());
    }

    @Test
    void facadeEjecutaRondaCorrectamente() {
        Personaje heroe = new Guerrero("Héroe", 100, 20);
        Personaje enemigo = new EnemigoBasico("Goblin", 50, 10);

        facade.iniciarCombate(heroe, enemigo);
        var resultado = facade.ejecutarRonda();

        assertNotNull(resultado);
        assertTrue(resultado.danio() > 0);
    }

    @Test
    void facadeEjecutaCombateCompletoHastaElFinal() {
        Personaje heroe = new Guerrero("Héroe", 100, 25);
        Personaje enemigo = new EnemigoBasico("Slime", 30, 5);

        facade.iniciarCombate(heroe, enemigo);
        Personaje ganador = facade.ejecutarCombateCompleto();

        assertNotNull(ganador);
        assertTrue(facade.estaFinalizado());
        assertEquals("Héroe", ganador.getNombre());
        assertTrue(ganador.estaVivo());
    }

    @Test
    void facadeGeneraLogDetalladoDelCombate() {
        Personaje heroe = new Guerrero("Héroe", 100, 30);
        Personaje enemigo = new EnemigoBasico("Débil", 20, 5);

        facade.iniciarCombate(heroe, enemigo);
        facade.ejecutarCombateCompleto();

        var log = facade.obtenerLogCombate();

        assertFalse(log.isEmpty());
        assertTrue(log.stream().anyMatch(line -> line.contains("COMBATE INICIADO")));
        assertTrue(log.stream().anyMatch(line -> line.contains("COMBATE FINALIZADO")));
        assertTrue(log.stream().anyMatch(line -> line.contains("Ganador")));
    }

    @Test
    void facadeProporcionaEstadisticasDelCombate() {
        Personaje heroe = new Guerrero("Héroe", 100, 20);
        Personaje enemigo = new EnemigoBasico("Enemigo", 60, 10);

        facade.iniciarCombate(heroe, enemigo);
        facade.ejecutarCombateCompleto();

        var stats = facade.obtenerEstadisticas();

        assertNotNull(stats);
        assertTrue(stats.totalRondas() > 0);
        assertTrue(stats.danioTotalInfligido() > 0);
        assertNotNull(stats.ganador());
    }

    @Test
    void facadeNoPermiteDosComatesSimultaneos() {
        Personaje heroe1 = new Guerrero("Héroe 1", 100, 20);
        Personaje enemigo1 = new EnemigoBasico("Enemigo 1", 50, 10);

        Personaje heroe2 = new Guerrero("Héroe 2", 100, 20);
        Personaje enemigo2 = new EnemigoBasico("Enemigo 2", 50, 10);

        facade.iniciarCombate(heroe1, enemigo1);

        assertThrows(IllegalStateException.class, () -> {
            facade.iniciarCombate(heroe2, enemigo2);
        });
    }

    @Test
    void facadePermiteReiniciarParaNuevoCombate() {
        Personaje heroe = new Guerrero("Héroe", 100, 50);
        Personaje enemigo = new EnemigoBasico("Enemigo", 20, 5);

        facade.iniciarCombate(heroe, enemigo);
        facade.ejecutarCombateCompleto();

        facade.reiniciar();

        // Ahora debería poder iniciar un nuevo combate
        Personaje nuevoHeroe = new Guerrero("Nuevo Héroe", 80, 15);
        Personaje nuevoEnemigo = new EnemigoBasico("Nuevo Enemigo", 40, 8);

        assertDoesNotThrow(() -> {
            facade.iniciarCombate(nuevoHeroe, nuevoEnemigo);
        });
    }

    @Test
    void facadeLanzaExcepcionSiEjecutaRondaSinIniciarCombate() {
        assertThrows(IllegalStateException.class, () -> {
            facade.ejecutarRonda();
        });
    }

    @Test
    void facadeRetornaNullParaGanadorSiCombateNoHaFinalizado() {
        Personaje heroe = new Guerrero("Héroe", 100, 15);
        Personaje enemigo = new EnemigoBasico("Enemigo", 100, 15);

        facade.iniciarCombate(heroe, enemigo);
        facade.ejecutarRonda();

        // El combate probablemente no ha terminado después de una sola ronda
        if (!facade.estaFinalizado()) {
            assertNull(facade.obtenerGanador());
        }
    }

    @Test
    void facadeOcultaComplejidadDelMotorDeCombate() {
        // Este test verifica que la facade proporciona una API simple
        // sin exponer detalles internos del motor

        Personaje heroe = new Guerrero("Héroe", 100, 20);
        Personaje enemigo = new EnemigoBasico("Enemigo", 50, 10);

        // API simple de 3 pasos
        facade.iniciarCombate(heroe, enemigo);
        Personaje ganador = facade.ejecutarCombateCompleto();
        var stats = facade.obtenerEstadisticas();

        // Verificación simple
        assertNotNull(ganador);
        assertNotNull(stats);
        assertTrue(stats.totalRondas() > 0);
    }
}
