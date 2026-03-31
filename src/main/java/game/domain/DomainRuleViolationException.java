package game.domain;

/**
 * Error de dominio para invariantes o reglas semanticas invalidadas.
 */
public class DomainRuleViolationException extends RuntimeException {

    public DomainRuleViolationException(String message) {
        super(message);
    }

    public DomainRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}