package game.ui;

import com.google.gson.Gson;
import game.application.runtime.GameRuntime;
import game.ui.integration.WebGameAdapter;
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
    private final GameRuntime runtime = new GameRuntime();

    private WebGameAdapter webAdapter;
    private WebEngine engine;
    private String lastStateJson = "";

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        engine = webView.getEngine();
        webAdapter = new WebGameAdapter(runtime, this::pushStateToUi, this::requestApplicationExit);

        engine.setOnAlert(event -> webAdapter.dispatchAlertMessage(event.getData()));

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                registerBridge();
                pushStateToUi();
            }
        });

        engine.load(resolveGameHtmlLocation());

        stage.setTitle("Dungeon Crawler - UI JavaFX");
        
        // Crear escena sin tamaño fijo para permitir redimensionamiento adaptativo
        Scene scene = new Scene(webView);
        stage.setScene(scene);
        
        // Configurar ventana para ocupar disponible con mín/máx dimensiones
        stage.setWidth(1366);
        stage.setHeight(768);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Maximizar ventana por defecto para adaptarse al monitor
        stage.setMaximized(true);
        
        stage.show();
    }

    private void requestApplicationExit() {
        Runnable exitTask = Platform::exit;
        if (Platform.isFxApplicationThread()) {
            exitTask.run();
        } else {
            Platform.runLater(exitTask);
        }
    }

    private void registerBridge() {
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember("javabridge", webAdapter.createBridge());
        lastStateJson = "";
    }

    private void pushStateToUi() {
        if (engine == null) {
            return;
        }

        Runnable task = () -> {
            try {
                String json = gson.toJson(webAdapter.presentViewModel());

                if (json.equals(lastStateJson)) {
                    return;
                }
                lastStateJson = json;

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
        var resource = getClass().getResource("/ui/game.html");
        if (resource != null) {
            return resource.toExternalForm();
        }

        Path fallback = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "ui", "game.html");
        if (Files.exists(fallback)) {
            return fallback.toUri().toString();
        }

        throw new IllegalStateException("No se encontro game.html en classpath ni en src/main/resources/ui.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
