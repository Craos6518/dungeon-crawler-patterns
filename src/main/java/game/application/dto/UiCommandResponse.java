package game.application.dto;

import game.ui.GameViewModel;

/**
 * Contrato de salida backend -> UI para cada comando.
 */
public class UiCommandResponse {

    public String status;
    public String message;
    public GameViewModel data;

    public static UiCommandResponse ok(GameViewModel data) {
        UiCommandResponse response = new UiCommandResponse();
        response.status = "ok";
        response.data = data;
        return response;
    }

    public static UiCommandResponse error(String message, GameViewModel data) {
        UiCommandResponse response = new UiCommandResponse();
        response.status = "error";
        response.message = message;
        response.data = data;
        return response;
    }
}