package game.application.usecase;

import game.application.state.GameSession;
import game.domain.DomainRuleViolationException;
import game.domain.character.Enemy;
import game.domain.combat.CombatResult;
import game.events.observer.EventType;
import game.events.observer.GameEvent;
import game.items.model.SimpleItem;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class CombatUseCaseSupport {

    private static final Logger LOGGER = Logger.getLogger(CombatUseCaseSupport.class.getName());

    private CombatUseCaseSupport() {
    }

    static void requireActiveCombat(GameSession session) {
        if (session.combat().isActive()) {
            session.setActiveScreen("combat");
            return;
        }

        throw new DomainRuleViolationException("No hay un enemigo activo en esta sala.");
    }

    static boolean ensureCombatStarted(GameSession session, boolean forced, boolean reportIfMissing) {
        if (session.combat().isActive()) {
            session.setActiveScreen("combat");
            return true;
        }

        var spawn = session.dungeon().spawnEnemyForCurrentRoom(forced);
        if (spawn.isEmpty()) {
            if (reportIfMissing) {
                session.appendSystemMessage("No hay un enemigo activo en esta sala.");
            } else {
                session.appendEvent("No hay enemigos en esta sala por ahora.");
            }
            return false;
        }

        Enemy enemy = spawn.get();
        session.combat().start(enemy, session.dungeon().isCurrentRoomBoss());
        session.setActiveScreen("combat");

        LOGGER.log(Level.INFO, "Combate iniciado en sala {0} contra {1}",
            new Object[]{session.dungeon().currentRoomIndex() + 1, enemy.name()});

        session.appendCombat("Combate iniciado contra " + enemy.name() + ".");
        session.eventManager().notificar(new GameEvent(EventType.COMBATE_INICIADO)
            .agregarDato("heroe", session.player().name())
            .agregarDato("enemigo", enemy.name())
            .agregarDato("sala", session.dungeon().currentRoomIndex() + 1));

        return true;
    }

    static void appendEnemyTurnEffects(GameSession session, CombatResult result, Enemy enemy) {
        if (result.enemyDamage > 0 || result.mitigatedDamage > 0) {
            session.appendCombat(enemy.name() + " ataca. Dano recibido: " + result.enemyDamage + ".");
        }
        if (result.mitigatedDamage > 0) {
            session.appendCombat("Tu defensa mitigo " + result.mitigatedDamage + " puntos de dano.");
        }
        if (result.poisonApplied) {
            session.appendCombat("Has sido envenenado. Busca un antidoto.");
        }
    }

    static void appendResourceFlow(GameSession session, CombatResult result) {
        if (result.resourceType == null || result.resourceType.isBlank()) {
            return;
        }

        if (result.resourceBefore == result.resourceAfter) {
            return;
        }

        String resourceName = result.resourceType.toUpperCase();
        session.appendCombat(
            resourceName + ": " + result.resourceBefore + " -> " + result.resourceAfter + "."
        );
    }

    static void handleVictory(GameSession session, Enemy enemy, CombatResult result) {
        session.player().registerDefeatedEnemy();
        session.dungeon().markCurrentRoomEnemyResolved();

        session.appendCombat("Derrotaste a " + enemy.name() + ". Ganaste " + result.gainedXp + " XP.");
        if (result.gainedLevels > 0) {
            session.appendCombat("Subiste " + result.gainedLevels + " nivel(es). Nivel actual: "
                + session.player().level() + ".");
        }

        boolean bossFight = session.combat().isBossFight();
        List<SimpleItem> combatLoot = buildCombatLoot(session, bossFight);

        session.combat().finish();
        session.openTreasureRoom(enemy.name(), result.gainedXp, bossFight, combatLoot);
        session.appendEvent("Sala de tesoro desbloqueada. Selecciona el botin de combate.");

        LOGGER.log(Level.INFO, "Combate finalizado por victoria. Enemigo={0}", enemy.name());

        session.eventManager().notificar(new GameEvent(EventType.COMBATE_FINALIZADO)
            .agregarDato("ganador", session.player().name())
            .agregarDato("enemigosDerrotados", session.player().defeatedEnemies()));
    }

    private static List<SimpleItem> buildCombatLoot(GameSession session, boolean bossFight) {
        List<SimpleItem> loot = new ArrayList<>();
        loot.add(session.dungeon().randomCombatReward());
        loot.add(session.dungeon().randomCombatReward());
        if (bossFight) {
            loot.add(session.dungeon().randomCombatReward());
        }
        return loot;
    }

    static void handleDefeat(GameSession session) {
        session.combat().finish();
        session.setActiveScreen("gameover");
        session.appendCombat("Has sido derrotado. El combate ha terminado.");
        session.appendEvent("Game Over en sala " + (session.dungeon().currentRoomIndex() + 1) + ".");

        LOGGER.log(Level.INFO, "Combate finalizado por derrota del jugador en sala {0}",
            session.dungeon().currentRoomIndex() + 1);

        session.eventManager().notificar(new GameEvent(EventType.JUEGO_TERMINADO)
            .agregarDato("resultado", "Derrota")
            .agregarDato("sala", session.dungeon().currentRoomIndex() + 1));
    }
}
