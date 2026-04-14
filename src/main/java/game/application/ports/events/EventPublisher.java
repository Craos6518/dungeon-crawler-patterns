package game.application.ports.events;

public interface EventPublisher {

    void suscribir(GameObserver observer);

    void suscribir(EventType tipo, GameObserver observer);

    void desuscribir(GameObserver observer);

    void desuscribir(EventType tipo, GameObserver observer);

    void notificar(GameEvent evento);
}
