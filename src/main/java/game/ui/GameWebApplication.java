package game.ui;

import com.google.gson.Gson;
import game.ui.integration.GamePresenter;
import game.ui.integration.UiCommandDispatcher;
import game.ui.integration.UiGameController;
import game.ui.integration.UiJavaBridge;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point JavaFX para la UI HTML/CSS renderizada en WebView.
 */
public class GameWebApplication extends Application {

    private final Gson gson = new Gson();
    private final UiGameController controller = new UiGameController();
    private final GamePresenter presenter = new GamePresenter();

    private UiCommandDispatcher dispatcher;
    private WebEngine engine;

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        engine = webView.getEngine();
        dispatcher = new UiCommandDispatcher(controller, this::pushStateToUi);

        engine.setOnAlert(event -> dispatcher.dispatchAlertMessage(event.getData()));

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                registerBridge();
                pushStateToUi();
            }
        });

        engine.load(resolveGameHtmlLocation());

        stage.setTitle("Dungeon Crawler - UI JavaFX");
        stage.setScene(new Scene(webView, 1366, 768));
        stage.show();
    }

    private void registerBridge() {
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("javabridge", new UiJavaBridge(dispatcher));
    }

    private void pushStateToUi() {
        if (engine == null) {
            return;
        }

        Runnable task = () -> {
            try {
                String json = gson.toJson(presenter.present(controller));
                engine.executeScript("window.updateGameState(" + json + ");");
            } catch (RuntimeException ignored) {
                // Ignorado: la pagina puede no estar lista temporalmente.
            }
        };

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    private String resolveGameHtmlLocation() {
        var resource = getClass().getResource("/game/ui/game.html");
        if (resource != null) {
            return resource.toExternalForm();
        }

        Path fallback = Path.of(System.getProperty("user.dir"), "src", "main", "java", "game", "ui", "game.html");
        if (Files.exists(fallback)) {
            return fallback.toUri().toString();
        }

        throw new IllegalStateException("No se encontro game.html en classpath ni en src/main/java/game/ui.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
