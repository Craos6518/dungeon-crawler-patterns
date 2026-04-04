package game.application.usecase;

import game.application.dto.UseSkillCommandRequest;
import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.events.observer.EventType;
import game.events.observer.GameEvent;

/**
 * Caso de uso: ejecutar habilidad especial durante combate.
 */
public class UseSkillUseCase {

    private final GameSession session;

    public UseSkillUseCase(GameSession session) {
        this.session = session;
    }

    public void execute(UseSkillCommandRequest request) {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes usar habilidad: el heroe esta derrotado.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            CombatUseCaseSupport.requireActiveCombat(session);

            String skill = request == null ? null : request.skill;
            var enemy = session.combat().currentEnemy();
            var result = session.combat().useSkill(skill, session.dungeon().themeKey());

            if (result.warning != null && !result.warning.isBlank()) {
                session.appendSystemMessage(result.warning);
            }

            if (!result.actionExecuted) {
                if (result.playerDefeated) {
                    CombatUseCaseSupport.handleDefeat(session);
                }
                return;
            }

            String usedSkill = result.skillName == null ? "Golpe Especial" : result.skillName;
            session.appendCombat(session.player().name() + " usa " + usedSkill + " e inflige " + result.playerDamage + " de dano.");

            session.eventManager().notificar(new GameEvent(EventType.ACCION_REALIZADA)
                .agregarDato("personaje", session.player().name())
                .agregarDato("accion", "habilidad")
                .agregarDato("nombre", usedSkill)
                .agregarDato("danio", result.playerDamage));

            if (result.enemyDefeated) {
                CombatUseCaseSupport.handleVictory(session, enemy, result);
                return;
            }

            CombatUseCaseSupport.appendEnemyTurnEffects(session, result, enemy);

            if (result.playerDefeated) {
                CombatUseCaseSupport.handleDefeat(session);
            }

            session.combat().resolveTurn();
        });
    }
}
