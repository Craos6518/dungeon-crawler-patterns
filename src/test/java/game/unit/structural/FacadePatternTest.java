package game.unit.structural;

import game.patterns.combat.facade.CombatFacade;
import game.domain.character.Enemy;
import game.domain.character.Player;
import game.domain.combat.Combat;
import game.domain.inventory.Inventory;
import game.domain.personaje.EnemigoBasico;
import game.domain.personaje.Guerrero;
import game.domain.personaje.Personaje;
import game.domain.turn.TurnManager;
import game.effects.status.StrengthEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

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
    void facadeAplicaCharacterDecoratorEnEjecutarRonda() {
        Personaje heroeBase = new Guerrero("Héroe", 100, 10);
        StrengthEffect heroeFortalecido = new StrengthEffect(heroeBase, 2.0, 2);
        Personaje enemigo = new EnemigoBasico("Goblin", 50, 8);

        facade.iniciarCombate(heroeFortalecido, enemigo);
        var resultado = facade.ejecutarRonda();

        assertNotNull(resultado);
        assertEquals(20, resultado.danio());
        assertEquals(30, enemigo.getVida());
        assertTrue(
            facade.obtenerLogCombate().stream().anyMatch(line -> line.contains("Fortalecido")),
            "El log de combate debe reflejar el efecto del decorator aplicado"
        );
    }

    @Test
    void facadeReplicaEstadoFinalFrenteASubsistemasDirectos() {
        int randomSeed = 42;

        Player jugadorFacade = new Player(new Guerrero("Héroe", 140, 22), Inventory.demo(), "guerrero");
        Enemy enemigoFacade = new Enemy(new EnemigoBasico("Orco", 240, 16), 18, 14, 13);
        CombatFacade facadeRuntime = new CombatFacade(jugadorFacade, new TurnManager(), new Random(randomSeed));
        facadeRuntime.start(enemigoFacade, false);

        var facadeAtaque = facadeRuntime.attack("current", "fire");
        var facadeDefensa = facadeRuntime.defend("fire");
        var facadeSkill = facadeRuntime.useSkill("Embate de Acero", "fire");

        Player jugadorDirecto = new Player(new Guerrero("Héroe", 140, 22), Inventory.demo(), "guerrero");
        Enemy enemigoDirecto = new Enemy(new EnemigoBasico("Orco", 240, 16), 18, 14, 13);
        Combat combateDirecto = new Combat(jugadorDirecto, new TurnManager(), new Random(randomSeed));
        combateDirecto.start(enemigoDirecto, false);

        var directoAtaque = combateDirecto.attack("current", "fire");
        var directoDefensa = combateDirecto.defend("fire");
        var directoSkill = combateDirecto.useSkill("Embate de Acero", "fire");

        assertEquals(jugadorDirecto.hp(), jugadorFacade.hp());
        assertEquals(enemigoDirecto.hp(), enemigoFacade.hp());
        assertEquals(jugadorDirecto.experience(), jugadorFacade.experience());

        assertEquals(directoAtaque.playerDamage, facadeAtaque.playerDamage);
        assertEquals(directoDefensa.enemyDamage, facadeDefensa.enemyDamage);
        assertEquals(directoSkill.enemyDefeated, facadeSkill.enemyDefeated);
        assertEquals(directoSkill.playerDefeated, facadeSkill.playerDefeated);
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
