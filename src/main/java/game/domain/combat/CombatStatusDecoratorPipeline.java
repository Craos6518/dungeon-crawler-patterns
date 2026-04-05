package game.domain.combat;

import game.domain.character.Player;
import game.domain.turn.TurnManager;
import game.effects.status.GuardEffect;
import game.effects.status.PoisonEffect;
import game.effects.status.StrengthEffect;

/**
 * Aplica Decorator en el flujo real de combate.
 *
 * Responsabilidad:
 * - Materializar efectos de veneno, buff ofensivo y mitigacion de guardia usando CharacterDecorator.
 * - Entregar valores efectivos al agregado Combat sin acoplarlo a clases concretas de efectos.
 *
 * No hace:
 * - control de turnos ni seleccion de acciones.
 * - emision de eventos o transiciones de estado.
 */
final class CombatStatusDecoratorPipeline {

    TurnManager.PoisonTick applyPoisonTick(Player player, TurnManager turnManager) {
        TurnManager.PoisonTick tick = turnManager.tickPoison();
        if (!tick.active()) {
            return tick;
        }

        PoisonEffect poison = new PoisonEffect(player.character(), tick.damage(), 1);
        poison.aplicarEfecto();
        return tick;
    }

    double resolveOffensiveMultiplier(Player player, int offensiveStacks) {
        if (offensiveStacks <= 0) {
            return 1.0;
        }

        double multiplier = 1.0 + (Math.max(0, offensiveStacks) * 0.15);
        StrengthEffect strength = new StrengthEffect(player.character(), multiplier, 1);
        return strength.getMultiplicadorDanio();
    }

    int resolveGuardMitigation(Player player, int incomingDamage, int guardStacks) {
        if (guardStacks <= 0 || incomingDamage <= 0) {
            return 0;
        }

        GuardEffect guard = new GuardEffect(player.character(), guardStacks);
        return guard.mitigarDanio(incomingDamage);
    }
}
