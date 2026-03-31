package game.ui.console;

import game.application.ports.GameOutputPort;

import java.io.PrintStream;

/**
 * Adaptador de salida estandar para consola.
 */
public class StdConsoleOutputPort implements GameOutputPort {

    private final PrintStream out;

    public StdConsoleOutputPort() {
        this(System.out);
    }

    public StdConsoleOutputPort(PrintStream out) {
        this.out = out;
    }

    @Override
    public void printLine(String text) {
        out.println(text);
    }
}