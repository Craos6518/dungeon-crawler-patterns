package game.application.usecase;

import game.application.state.GameSession;
import game.application.state.GameFlowState;
import game.domain.DomainRuleViolationException;
import game.application.ports.events.EventType;
import game.application.ports.events.GameEvent;

/**
 * Caso de uso: avanzar la progresion de turno/sala en exploracion.
 */
public class AdvanceTurnUseCase {

    private final GameSession session;

    public AdvanceTurnUseCase(GameSession session) {
        this.session = session;
    }

    public void execute() {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes avanzar: el heroe esta derrotado.");
        }

        if (session.hasActiveEnemy()) {
            throw new DomainRuleViolationException("No puedes avanzar mientras haya un combate activo.");
        }

        if (!session.dungeon().canAdvanceRoom()) {
            if (isCurrentDungeonCompleted()) {
                UseCaseTransactionSupport.runAtomically(session, () -> {
                    session.markThemeCompleted(session.dungeon().themeKey());
                    session.setHeroSelectionLocked(true);
                    String dungeonName = session.dungeon().model().getNombre();
                    boolean campaignCompleted = session.nextCampaignTheme().isBlank();

                    session.transitionTo(campaignCompleted ? GameFlowState.MENU : GameFlowState.HERO);
                    if (campaignCompleted) {
                        session.appendEvent("Conquistaste " + dungeonName + ". Has completado la campana de Eranthia.");
                    } else {
                        session.appendEvent("Conquistaste " + dungeonName + ". Elige tu siguiente mazmorra.");
                    }

                    session.eventManager().notificar(new GameEvent(EventType.SALA_COMPLETADA)
                        .agregarDato("resultado", "mazmorra_completada")
                        .agregarDato("mazmorra", dungeonName)
                        .agregarDato("tema", session.dungeon().themeKey())
                        .agregarDato("salas", session.dungeon().totalRooms())
                        .agregarDato("campanaCompleta", campaignCompleted));
                });
                return;
            }

            throw new DomainRuleViolationException("Ya estas en la ultima sala de la mazmorra.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            session.dungeon().advanceRoom();
            session.transitionTo(GameFlowState.EXPLORATION);

            var room = session.dungeon().currentRoom();
            session.appendEvent("Avanzas a la sala " + (session.dungeon().currentRoomIndex() + 1) + ": " + room.name());

            session.eventManager().notificar(new game.application.ports.events.GameEvent(game.application.ports.events.EventType.SALA_ENTRAR)
                .agregarDato("sala", session.dungeon().currentRoomIndex() + 1)
                .agregarDato("nombre", room.name()));

            if (session.dungeon().isEnemyPendingInCurrentRoom(false)) {
                CombatUseCaseSupport.ensureCombatStarted(session, false, false);
                return;
            }

            if (session.dungeon().shouldRollRandomEncounterOnAdvance()) {
                CombatUseCaseSupport.ensureCombatStarted(session, true, false);
            }
        });
    }

    private boolean isCurrentDungeonCompleted() {
        return session.dungeon().isCurrentRoomBoss()
            && !session.hasActiveEnemy()
            && !session.isEnemyPendingInCurrentRoom();
    }
}
