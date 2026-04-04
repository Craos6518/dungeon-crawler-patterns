package game.application.dto;

/**
 * Payload tipado para accion startGame / heroNewGame.
 */
public class StartGameCommandRequest {

    public String theme;
    /** "guerrero" | "mago" | "arquero" — opcional, solo presente en heroNewGame */
    public String heroType;
}
