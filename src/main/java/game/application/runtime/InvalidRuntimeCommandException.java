package game.application.runtime;

/**
 * Excepcion de validacion para comandos invalidos en el runtime de aplicacion.
 */
public class InvalidRuntimeCommandException extends RuntimeException {

    public InvalidRuntimeCommandException(String message) {
        super(message);
    }
}