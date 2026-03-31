package game.application.dto;

import com.google.gson.JsonObject;

/**
 * Contrato de comando de UI: { action: string, payload: object }.
 */
public class UiCommand {

    public String action;
    public JsonObject payload;

    public String actionOrBlank() {
        return action == null ? "" : action.trim();
    }

    public JsonObject payloadOrEmpty() {
        return payload == null ? new JsonObject() : payload;
    }
}
