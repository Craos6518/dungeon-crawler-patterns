package game.ui.integration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game.application.dto.UiCommand;
import game.application.dto.UiCommandResponse;
import game.application.runtime.GameCommandHandler;
import game.application.runtime.InvalidRuntimeCommandException;
import game.domain.DomainRuleViolationException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Traduce mensajes JS del WebView a comandos tipados de aplicacion.
 */
public class UiCommandDispatcher {

    private static final Logger LOGGER = Logger.getLogger(UiCommandDispatcher.class.getName());

    private final GameCommandHandler commandHandler;
    private final Runnable onStateChanged;
    private final GlobalCommandExceptionHandler exceptionHandler;
    private final Gson gson;

    public UiCommandDispatcher(GameCommandHandler commandHandler, Runnable onStateChanged) {
        this.commandHandler = commandHandler;
        this.onStateChanged = onStateChanged;
        this.exceptionHandler = new GlobalCommandExceptionHandler();
        this.gson = new Gson();
    }

    public UiCommandResponse dispatchAlertMessage(String rawMessage) {
        return dispatchRaw(rawMessage);
    }

    public UiCommandResponse dispatchCommandJson(String commandJson) {
        return dispatchRaw(commandJson);
    }

    private UiCommandResponse dispatchRaw(String rawCommand) {
        String action = "<unknown>";
        LOGGER.log(Level.INFO, "Comando recibido desde UI: {0}", summarize(rawCommand));

        try {
            UiCommand command = parseStructuredCommand(rawCommand);
            action = command.actionOrBlank();

            if (!commandHandler.supportsAction(action)) {
                throw new InvalidCommandException("Accion no soportada: " + action);
            }

            commandHandler.handleCommand(command);
            return UiCommandResponse.ok(commandHandler.presentViewModel());
        } catch (RuntimeException ex) {
            if (ex instanceof InvalidCommandException
                || ex instanceof InvalidRuntimeCommandException
                || ex instanceof DomainRuleViolationException) {
                commandHandler.registrarMensajeSistema(ex.getMessage());
            } else {
                commandHandler.registrarMensajeSistema("Error inesperado procesando comando.");
            }
            return exceptionHandler.handle(action, ex, commandHandler.presentViewModel());
        } finally {
            onStateChanged.run();
        }
    }

    private UiCommand parseStructuredCommand(String rawCommand) {
        if (rawCommand == null || rawCommand.isBlank()) {
            throw new InvalidCommandException("Comando vacio recibido desde UI.");
        }

        try {
            JsonElement root = JsonParser.parseString(rawCommand);
            if (!root.isJsonObject()) {
                throw new InvalidCommandException("Comando invalido: se esperaba objeto JSON.");
            }

            JsonObject obj = root.getAsJsonObject();
            JsonElement actionEl = obj.get("action");
            if (actionEl == null || actionEl.isJsonNull()) {
                throw new InvalidCommandException("Comando invalido: falta campo action.");
            }

            if (!actionEl.isJsonPrimitive() || !actionEl.getAsJsonPrimitive().isString()) {
                throw new InvalidCommandException("Comando invalido: action debe ser string.");
            }
            String action = actionEl.getAsString();
            if (action == null || action.isBlank()) {
                throw new InvalidCommandException("Comando invalido: action vacia.");
            }

            JsonElement payloadEl = obj.get("payload");
            if (payloadEl == null || payloadEl.isJsonNull()) {
                throw new InvalidCommandException("Comando invalido: payload obligatorio.");
            }

            if (!payloadEl.isJsonObject()) {
                throw new InvalidCommandException("Comando invalido: payload debe ser objeto JSON.");
            }

            UiCommand command = new UiCommand();
            command.action = action.trim();
            command.payload = payloadEl.getAsJsonObject();
            return command;
        } catch (RuntimeException ex) {
            if (ex instanceof InvalidCommandException) {
                throw ex;
            }
            throw new InvalidCommandException("JSON de comando invalido: " + summarize(rawCommand));
        }
    }

    public String dispatchCommandJsonAsString(String commandJson) {
        UiCommandResponse response = dispatchCommandJson(commandJson);
        return gson.toJson(response);
    }

    private static String summarize(String rawCommand) {
        if (rawCommand == null) {
            return "<null>";
        }
        String compact = rawCommand.replaceAll("\\s+", " ").trim();
        return compact.length() <= 160 ? compact : compact.substring(0, 157) + "...";
    }
}
