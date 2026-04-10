package game.application.ports.persistence;

import game.domain.DomainRuleViolationException;

/**
 * Se lanza cuando un slot de guardado no existe fisicamente en disco.
 */
public class SaveSlotNotFoundException extends DomainRuleViolationException {

    public SaveSlotNotFoundException(String message) {
        super(message);
    }
}
