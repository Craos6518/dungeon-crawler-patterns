package game;

/**
 * Wrapper de compatibilidad para mantener {@code game.Main} como entry point historico.
 */
public class Main {
    public static void main(String[] args) {
        game.ui.GameWebApplication.main(args);
    }
}
