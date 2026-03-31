package game.application.ports;

/**
 * Puerto de entrada para adapters (consola, web, tests, etc.).
 */
public interface GameInputPort {

    String readLine();
}