package game.state.domain;

import game.command.actions.CommandInvoker;
import game.events.observer.EventManager;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameOriginator;

import java.util.Scanner;

/**
 * Clase base abstracta para estados de dominio.
 * Proporciona acceso a las dependencias inyectadas y métodos utilitarios comunes.
 */
public abstract class AbstractDomainGameState implements DomainGameState {
    
    protected Scanner scanner;
    protected EventManager eventManager;
    protected CommandInvoker commandInvoker;
    protected GameOriginator originator;
    protected GameCaretaker caretaker;
    
    @Override
    public void inyectarDependencias(
        Scanner scanner,
        EventManager eventManager,
        CommandInvoker commandInvoker,
        GameOriginator originator,
        GameCaretaker caretaker
    ) {
        this.scanner = scanner;
        this.eventManager = eventManager;
        this.commandInvoker = commandInvoker;
        this.originator = originator;
        this.caretaker = caretaker;
    }
    
    /**
     * Lee una opción válida del usuario
     */
    protected int leerOpcion(int min, int max) {
        while (true) {
            try {
                System.out.print("> ");
                String input = leerLineaRequerida().trim();
                int opcion = Integer.parseInt(input);

                if (opcion >= min && opcion <= max) {
                    return opcion;
                }
                System.out.println("❌ Opción inválida. Elige entre " + min + " y " + max);
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida. Ingresa un número.");
            } catch (java.util.NoSuchElementException e) {
                throw new InputClosedException();
            }
        }
    }
    
    /**
     * Lee una línea del usuario
     */
    protected String leerLineaRequerida() {
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine();
    }
    
    /**
     * Espera a que el usuario presione Enter si es posible
     */
    protected void esperarEnterSiDisponible() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
    
    /**
     * Excepción para indicar que la entrada se cerró anormalmente
     */
    public static class InputClosedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
