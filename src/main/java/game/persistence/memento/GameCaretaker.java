package game.persistence.memento;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Memento Pattern - Caretaker
 * 
 * Gestiona los mementos, guardando y cargando partidas.
 * Responsable de la persistencia física de los mementos.
 */
public class GameCaretaker {
    private final List<GameMemento> historial;
    private final String directoriGuardado;
    
    public GameCaretaker() {
        this("./saves/");
    }
    
    public GameCaretaker(String directoriGuardado) {
        this.historial = new ArrayList<>();
        this.directoriGuardado = directoriGuardado;
        
        // Crear directorio si no existe
        File dir = new File(directoriGuardado);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Guarda un memento en memoria
     */
    public void guardarEnMemoria(GameMemento memento) {
        if (memento == null) {
            throw new IllegalArgumentException("El memento no puede ser null");
        }
        historial.add(memento);
    }
    
    /**
     * Obtiene un memento del historial por índice
     */
    public GameMemento obtenerMemento(int indice) {
        if (indice < 0 || indice >= historial.size()) {
            throw new IndexOutOfBoundsException("Índice de memento inválido: " + indice);
        }
        return historial.get(indice);
    }
    
    /**
     * Obtiene el último memento guardado
     */
    public GameMemento obtenerUltimoMemento() {
        if (historial.isEmpty()) {
            throw new IllegalStateException("No hay mementos guardados");
        }
        return historial.get(historial.size() - 1);
    }
    
    /**
     * Guarda un memento en disco
     */
    public void guardarEnDisco(GameMemento memento, String nombreArchivo) {
        if (memento == null) {
            throw new IllegalArgumentException("El memento no puede ser null");
        }
        
        String rutaCompleta = directoriGuardado + nombreArchivo + ".save";
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(rutaCompleta))) {
            oos.writeObject(memento);
            System.out.println("Partida guardada exitosamente en: " + rutaCompleta);
        } catch (IOException e) {
            System.err.println("Error al guardar la partida: " + e.getMessage());
            throw new RuntimeException("No se pudo guardar la partida", e);
        }
    }
    
    /**
     * Carga un memento desde disco
     */
    public GameMemento cargarDesdeDisco(String nombreArchivo) {
        String rutaCompleta = directoriGuardado + nombreArchivo + ".save";
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(rutaCompleta))) {
            GameMemento memento = (GameMemento) ois.readObject();
            System.out.println("Partida cargada exitosamente desde: " + rutaCompleta);
            return memento;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar la partida: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar la partida", e);
        }
    }
    
    /**
     * Lista todos los archivos de guardado disponibles
     */
    public List<String> listarGuardados() {
        List<String> guardados = new ArrayList<>();
        File dir = new File(directoriGuardado);
        File[] archivos = dir.listFiles((d, nombre) -> nombre.endsWith(".save"));
        
        if (archivos != null) {
            for (File archivo : archivos) {
                guardados.add(archivo.getName().replace(".save", ""));
            }
        }
        
        return guardados;
    }
    
    /**
     * Elimina un archivo de guardado
     */
    public boolean eliminarGuardado(String nombreArchivo) {
        String rutaCompleta = directoriGuardado + nombreArchivo + ".save";
        File archivo = new File(rutaCompleta);
        
        if (archivo.exists()) {
            boolean eliminado = archivo.delete();
            if (eliminado) {
                System.out.println("Guardado eliminado: " + nombreArchivo);
            }
            return eliminado;
        }
        
        return false;
    }
    
    /**
     * Obtiene la cantidad de mementos en memoria
     */
    public int getCantidadMementos() {
        return historial.size();
    }
    
    /**
     * Limpia todos los mementos del historial en memoria
     */
    public void limpiarHistorial() {
        historial.clear();
    }
    
    /**
     * Obtiene el historial completo
     */
    public List<GameMemento> getHistorial() {
        return new ArrayList<>(historial);
    }
}
