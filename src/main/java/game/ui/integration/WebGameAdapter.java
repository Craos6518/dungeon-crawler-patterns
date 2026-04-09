package game.ui.integration;

import game.application.dto.UiCommandResponse;
import game.application.runtime.GameRuntime;
import game.ui.GameViewModel;

/**
 * Adaptador web para conectar JavaFX WebView con el runtime unico.
 */
public class WebGameAdapter {

    private final GameRuntime runtime;
    private final UiCommandDispatcher dispatcher;

    public WebGameAdapter(GameRuntime runtime, Runnable onStateChanged) {
        this(runtime, onStateChanged, () -> {
            // No-op por compatibilidad.
        });
    }

    public WebGameAdapter(GameRuntime runtime, Runnable onStateChanged, Runnable onExitRequested) {
        this.runtime = runtime;
        this.dispatcher = new UiCommandDispatcher(runtime, onStateChanged, onExitRequested);
    }

    public UiCommandResponse dispatchAlertMessage(String rawMessage) {
        return dispatcher.dispatchAlertMessage(rawMessage);
    }

    public UiJavaBridge createBridge() {
        return new UiJavaBridge(dispatcher);
    }

    public GameViewModel presentViewModel() {
        return runtime.presentViewModel();
    }
}