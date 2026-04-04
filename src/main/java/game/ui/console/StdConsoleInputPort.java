package game.ui.console;

import game.application.ports.GameInputPort;

import java.util.Scanner;

/**
 * Adaptador de entrada estandar para consola.
 */
public class StdConsoleInputPort implements GameInputPort {

    private final Scanner scanner;

    public StdConsoleInputPort() {
        this(new Scanner(System.in));
    }

    public StdConsoleInputPort(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String readLine() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }
}