package game.persistence.memento;

import game.domain.DomainRuleViolationException;

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

        File archivo = resolveSaveFile(nombreArchivo);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(archivo))) {
            oos.writeObject(memento);
            System.out.println("Partida guardada exitosamente en: " + archivo.getPath());
        } catch (IOException e) {
            throw new DomainRuleViolationException("No se pudo guardar la partida.", e);
        }
    }
    
    /**
     * Carga un memento desde disco
     */
    public GameMemento cargarDesdeDisco(String nombreArchivo) {
        return cargarDesdeDisco(nombreArchivo, true);
    }

    /**
     * Carga un memento desde disco sin imprimir mensaje de exito.
     *
     * Uso principal: lectura de metadatos para UI (ranuras/estadisticas)
     * sin confundir los logs con "cargas" de partida reales.
     */
    public GameMemento cargarDesdeDiscoSilencioso(String nombreArchivo) {
        return cargarDesdeDisco(nombreArchivo, false);
    }

    private GameMemento cargarDesdeDisco(String nombreArchivo, boolean logSuccess) {
        File archivo = resolveSaveFile(nombreArchivo);

        if (!archivo.exists() || !archivo.isFile()) {
            throw new SaveSlotNotFoundException("Slot vacio: " + archivo.getName() + " no existe.");
        }
        if (!archivo.canRead()) {
            throw new DomainRuleViolationException("No se puede leer el slot " + archivo.getName() + ".");
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            Object loaded = ois.readObject();
            if (!(loaded instanceof GameMemento memento)) {
                throw new SaveDataCorruptionException("Guardado corrupto: formato de datos incompatible.");
            }
            if (logSuccess) {
                System.out.println("Partida cargada exitosamente desde: " + archivo.getPath());
            }
            return memento;
        } catch (SaveDataCorruptionException ex) {
            throw ex;
        } catch (FileNotFoundException e) {
            throw new SaveSlotNotFoundException("Slot vacio: " + archivo.getName() + " no existe.");
        } catch (InvalidClassException | StreamCorruptedException | OptionalDataException
                 | ClassNotFoundException | ClassCastException e) {
            throw new SaveDataCorruptionException("Guardado corrupto: formato incompatible o datos invalidos.", e);
        } catch (IOException e) {
            throw new DomainRuleViolationException("No se pudo cargar la partida.", e);
        }
    }

    public boolean existeEnDisco(String nombreArchivo) {
        return resolveSaveFile(nombreArchivo).isFile();
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
        File archivo = resolveSaveFile(nombreArchivo);
        
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

    private File resolveSaveFile(String nombreArchivo) {
        String normalizedName = normalizeSaveName(nombreArchivo);
        return new File(directoriGuardado, normalizedName + ".save");
    }

    private static String normalizeSaveName(String nombreArchivo) {
        if (nombreArchivo == null) {
            throw new IllegalArgumentException("El nombre de archivo no puede ser null");
        }
        String normalized = nombreArchivo.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("El nombre de archivo no puede estar vacio");
        }
        if (normalized.contains("/") || normalized.contains("\\")) {
            throw new IllegalArgumentException("El nombre de archivo no puede contener separadores de ruta");
        }
        return normalized;
    }
}
