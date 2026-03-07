package game.combat.model;

public record ResultadoAtaque(
        String atacante,
        String defensor,
        int danio,
        int vidaRestanteDefensor
) {
}
