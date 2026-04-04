package game.unit.application;

import game.application.state.GameSessionFactory;
import game.application.usecase.SaveGameUseCase;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionFactorySaveDirectoryTest {

    @Test
    void saveUsesProjectGameSavesWhenRunningFromParentDirectory() throws Exception {
        String previousUserDir = System.getProperty("user.dir");

        Path workspaceRoot = Files.createTempDirectory("workspace-root");
        Path projectRoot = workspaceRoot.resolve("dungeon-crawler-patterns");
        Path projectSaves = projectRoot.resolve("game-saves");

        Files.createDirectories(projectSaves);

        try {
            System.setProperty("user.dir", workspaceRoot.toString());

            var session = GameSessionFactory.createDemoSession();
            session.setActiveScreen("exploration");
            new SaveGameUseCase(session).execute(1);

            assertTrue(Files.exists(projectSaves.resolve("Slot_1.save")));
        } finally {
            System.setProperty("user.dir", previousUserDir);
        }
    }
}
