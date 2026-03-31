package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.items.model.SimpleItem;

/**
 * Caso de uso: explorar sala y buscar tesoros.
 */
public class SearchTreasureUseCase {

    private final GameSession session;

    public SearchTreasureUseCase(GameSession session) {
        this.session = session;
    }

    public void execute() {
        if (!session.player().isAlive()) {
            throw new DomainRuleViolationException("No puedes buscar tesoros: el heroe esta derrotado.");
        }

        if (session.hasActiveEnemy()) {
            throw new DomainRuleViolationException("No puedes buscar tesoros durante combate.");
        }

        if (session.dungeon().wasCurrentRoomTreasureResolved()) {
            throw new DomainRuleViolationException("Esta sala ya fue explorada.");
        }

        UseCaseTransactionSupport.runAtomically(session, () -> {
            var loot = session.dungeon().searchTreasureInCurrentRoom();
            if (loot.isEmpty()) {
                session.appendEvent("Exploraste la sala pero no encontraste ningun tesoro.");
                return;
            }

            SimpleItem item = loot.get();
            try {
                session.inventory().add(item);
                session.player().addGold(item.getValorTotal());

                session.appendEvent("Tesoro obtenido: " + item.getNombre() + " (+" + item.getValorTotal() + " oro).");
                session.eventManager().notificar(new GameEvent(EventType.TESORO_ENCONTRADO)
                    .agregarDato("item", item.getNombre())
                    .agregarDato("oro", item.getValorTotal()));
            } catch (RuntimeException ex) {
                session.appendEvent("Encontraste " + item.getNombre() + ", pero el inventario esta lleno.");
            }
        });
    }
}
