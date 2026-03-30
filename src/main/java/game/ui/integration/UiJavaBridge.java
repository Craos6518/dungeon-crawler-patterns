package game.ui.integration;

/**
 * Objeto expuesto a JavaScript como window.javabridge.
 */
public class UiJavaBridge {

    private final UiCommandDispatcher dispatcher;

    public UiJavaBridge(UiCommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void dispatch(String action, String payloadJson) {
        dispatcher.dispatch(action, payloadJson);
    }
}
