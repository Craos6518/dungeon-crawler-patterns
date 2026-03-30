package game.ui.integration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Traduce mensajes JS del WebView hacia acciones del UiGameController.
 */
public class UiCommandDispatcher {

    private final UiGameController controller;
    private final Runnable onStateChanged;

    public UiCommandDispatcher(UiGameController controller, Runnable onStateChanged) {
        this.controller = controller;
        this.onStateChanged = onStateChanged;
    }

    public void dispatchAlertMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            return;
        }

        String action = rawMessage;
        String payloadJson = "{}";

        int split = rawMessage.indexOf(':');
        if (split > 0) {
            action = rawMessage.substring(0, split);
            payloadJson = rawMessage.substring(split + 1);
        }

        dispatch(action, payloadJson);
    }

    public void dispatch(String action, String payloadJson) {
        JsonObject payload = parsePayload(payloadJson);
        controller.handleAction(action, payload);
        onStateChanged.run();
    }

    private JsonObject parsePayload(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return new JsonObject();
        }

        try {
            JsonElement element = JsonParser.parseString(payloadJson);
            if (element.isJsonObject()) {
                return element.getAsJsonObject();
            }

            JsonObject wrapped = new JsonObject();
            wrapped.add("value", element);
            return wrapped;
        } catch (RuntimeException ex) {
            controller.registrarMensajeSistema("Payload JSON invalido: " + payloadJson);
            return new JsonObject();
        }
    }
}
