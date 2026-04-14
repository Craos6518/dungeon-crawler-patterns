package game.application.ports.persistence;

import game.application.state.GameMemento;

public interface SessionSnapshotStore {

    void guardarEnMemoria(GameMemento memento);

    void guardarEnDisco(GameMemento memento, String nombreArchivo);

    GameMemento cargarDesdeDisco(String nombreArchivo);

    GameMemento cargarDesdeDiscoSilencioso(String nombreArchivo);

    boolean existeEnDisco(String nombreArchivo);
}
