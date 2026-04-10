package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;

/**
 * Caso de uso: intentar retirarse de un combate activo.
 */
public class RetreatCombatUseCase {

    private final GameSession session;

    public RetreatCombatUseCase(GameSession session) {
        this.session = session;
    }

    public void execute() {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes retirarte: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            var enemy = session.combat().currentEnemy();
            var result = session.combat().retreatAttempt(session.heroType(), session.dungeon().themeKey());

            if (result.retreatSuccessful) {
                session.appendCombat("Lograste retirarte del combate contra " + enemy.name() + ".");
                CombatUseCaseSupport.appendResourceFlow(session, result);
                session.appendEvent("Te reagrupas tras una retirada tactica.");
                session.setActiveScreen("exploration");

                session.eventManager().notificar(new GameEvent(EventType.ACCION_REALIZADA)
                    .agregarDato("personaje", session.player().name())
                    .agregarDato("accion", "retirada")
                    .agregarDato("resultado", "exitosa")
                    .agregarDato("enemigo", enemy.name()));
                return;
            }

            session.appendCombat("No lograste retirarte. " + enemy.name() + " aprovecha para atacar.");
            CombatUseCaseSupport.appendResourceFlow(session, result);
            CombatUseCaseSupport.appendEnemyTurnEffects(session, result, enemy);

            if (result.playerDefeated) {
                CombatUseCaseSupport.handleDefeat(session);
                return;
            }

            session.eventManager().notificar(new GameEvent(EventType.ACCION_REALIZADA)
                .agregarDato("personaje", session.player().name())
                .agregarDato("accion", "retirada")
                .agregarDato("resultado", "fallida")
                .agregarDato("enemigo", enemy.name()));

            session.combat().resolveTurn();
            session.setActiveScreen("combat");
        });
    }
}
