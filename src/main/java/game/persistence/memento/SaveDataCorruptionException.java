package game.persistence.memento;

import game.domain.DomainRuleViolationException;

/**
 * Se lanza cuando un archivo de guardado no puede deserializarse de forma valida.
 */
public class SaveDataCorruptionException extends DomainRuleViolationException {

    public SaveDataCorruptionException(String message) {
        super(message);
    }

    public SaveDataCorruptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
