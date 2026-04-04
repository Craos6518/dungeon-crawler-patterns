package game;

/**
 * Entry point de consola para ejecutar el runtime unico mediante InteractiveGame.
 */
public final class ConsoleMain {

    private ConsoleMain() {
    }

    public static void main(String[] args) {
        new InteractiveGame().iniciar();
    }
}