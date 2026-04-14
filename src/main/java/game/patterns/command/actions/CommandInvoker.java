package game.patterns.command.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Invoker del patrón Command
 * 
 * Responsable de:
 * - Ejecutar comandos
 * - Mantener historial de comandos ejecutados
 * - Soportar deshacer (undo) operaciones anteriores
 */
public class CommandInvoker {
    private final List<Command> historial;
    private final Stack<Command> comandosEjecutados;
    
    public CommandInvoker() {
        this.historial = new ArrayList<>();
        this.comandosEjecutados = new Stack<>();
    }
    
    /**
     * Ejecuta un comando y lo registra en el historial
     */
    public void ejecutarComando(Command comando) {
        if (!comando.canExecute()) {
            throw new IllegalStateException(
                "El comando no puede ser ejecutado: " + comando.getDescription()
            );
        }
        
        comando.execute();
        historial.add(comando);
        comandosEjecutados.push(comando);
    }

    /**
     * Alias en ingles para flujos que consumen API uniforme execute(...).
     */
    public void execute(Command comando) {
        ejecutarComando(comando);
    }
    
    /**
     * Deshace el último comando ejecutado
     */
    public void undoLastCommand() {
        if (comandosEjecutados.isEmpty()) {
            throw new IllegalStateException("No hay comandos para deshacer");
        }

        Command comando = comandosEjecutados.pop();
        try {
            comando.undo();
            if (!historial.isEmpty()) {
                historial.remove(historial.size() - 1);
            }
        } catch (RuntimeException ex) {
            // Revertimos el pop para mantener el invoker consistente ante un undo fallido.
            comandosEjecutados.push(comando);
            throw ex;
        }
    }
    
    /**
     * Deshace los últimos N comandos
     */
    public void undoLastN(int n) {
        if (n > comandosEjecutados.size()) {
            throw new IllegalArgumentException(
                "No hay suficientes comandos para deshacer. Disponibles: " + 
                comandosEjecutados.size()
            );
        }
        
        for (int i = 0; i < n; i++) {
            undoLastCommand();
        }
    }
    
    /**
     * Obtiene el historial completo de comandos (inmutable)
     */
    public List<Command> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    /**
     * Alias en ingles para integración con tests/contratos que esperan getHistory().
     */
    public List<Command> getHistory() {
        return getHistorial();
    }
    
    /**
     * Obtiene la cantidad de comandos ejecutados
     */
    public int getCantidadComandos() {
        return historial.size();
    }
    
    /**
     * Limpia el historial de comandos
     */
    public void limpiarHistorial() {
        historial.clear();
        comandosEjecutados.clear();
    }
    
    /**
     * Obtiene una representación del historial como texto
     */
    public String getHistorialTexto() {
        if (historial.isEmpty()) {
            return "No hay comandos en el historial";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIAL DE COMANDOS ===\n");
        for (int i = 0; i < historial.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, historial.get(i).getDescription()));
        }
        return sb.toString();
    }
}
