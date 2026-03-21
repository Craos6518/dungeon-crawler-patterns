package game.state.domain;

import game.events.observer.GameObserver;
import game.persistence.memento.GameCaretaker;
import game.persistence.memento.GameOriginator;
import game.persistence.memento.GameMemento;
import game.command.actions.CommandInvoker;
import game.events.observer.EventManager;

import java.util.Scanner;

/**
 * Interfaz base para estados de dominio que encapsulan lógica procedimental
 * específica de gameplay (exploración, combate, setup, etc).
 * 
 * Los estados de dominio son reutilizables y NO dependen de Scanner directamente,
 * permitiendo su uso en diferentes interfaces (consola, 2D, web, etc).
 */
public interface DomainGameState {
    
    /**
     * Ejecuta la lógica principal del estado.
     * @return true si continuar al siguiente estado, false si salir
     */
    boolean ejecutar();
    
    /**
     * Obtiene el nombre del estado para propósitos de logging/debugging.
     */
    String getNombreEstado();
    
    /**
     * Inyecta dependencias comunes para los estados de dominio.
     */
    void inyectarDependencias(
        Scanner scanner,
        EventManager eventManager,
        CommandInvoker commandInvoker,
        GameOriginator originator,
        GameCaretaker caretaker
    );
}
