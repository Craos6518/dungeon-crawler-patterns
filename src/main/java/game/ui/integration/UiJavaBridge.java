package game.ui.integration;

/**
 * Objeto expuesto a JavaScript como window.javabridge.
 */
public class UiJavaBridge {

    private final UiCommandDispatcher dispatcher;

    public UiJavaBridge(UiCommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public String dispatch(String commandJson) {
        return dispatcher.dispatchCommandJsonAsString(commandJson);
    }

    public String dispatch(String action, String payloadJson) {
        String safeAction = action == null ? "" : action;
        String safePayload = payloadJson == null || payloadJson.isBlank() ? "{}" : payloadJson;
        return dispatch("{\"action\":\"" + escapeJson(safeAction) + "\",\"payload\":" + safePayload + "}");
    }

    private static String escapeJson(String input) {
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }
}
