package game.domain.combat;

import game.domain.character.Player;
import game.domain.turn.TurnManager;
import game.effects.status.GuardEffect;
import game.effects.status.PoisonEffect;
import game.effects.status.StrengthEffect;
import game.effects.status.BurnEffect;
import game.effects.status.StunEffect;

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

    TurnManager.BurnTick applyBurnTick(Player player, TurnManager turnManager) {
        TurnManager.BurnTick tick = turnManager.tickBurn();
        if (!tick.active()) {
            return tick;
        }

        BurnEffect burn = new BurnEffect(player.character(), tick.damage(), 1);
        burn.aplicarEfecto();
        return tick;
    }

    TurnManager.StunStatus resolveStun(Player player, TurnManager turnManager) {
        TurnManager.StunStatus status = turnManager.checkStun();
        if (!status.active()) {
            return status;
        }

        StunEffect stun = new StunEffect(player.character(), 1);
        // El efecto de aturdimiento se materializa para coherencia del patrón,
        // aunque la lógica de saltar turno la maneja Combat.
        return status;
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
