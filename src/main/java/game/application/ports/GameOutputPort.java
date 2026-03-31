package game.application.ports;

/**
 * Puerto de salida para adapters (consola, web, logs, etc.).
 */
public interface GameOutputPort {

    void printLine(String text);
}