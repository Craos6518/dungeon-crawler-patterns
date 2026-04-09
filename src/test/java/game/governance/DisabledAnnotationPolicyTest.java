package game.governance;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DisabledAnnotationPolicyTest {

    private static final Pattern DISABLED_ANNOTATION = Pattern.compile("(?m)^\\s*@Disabled\\b");
    private static final Pattern DISABLED_WITH_REASON = Pattern.compile(
        "(?m)^\\s*@Disabled\\s*\\(\\s*\"([^\"]+)\"\\s*\\)"
    );

    @Test
    void disabledAnnotationsMustDeclareExplicitReason() throws IOException {
        Path testsRoot = Path.of("src", "test", "java");
        if (!Files.exists(testsRoot)) {
            return;
        }

        List<String> violations = new ArrayList<>();
        try (Stream<Path> files = Files.walk(testsRoot)) {
            files
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> inspectFile(path, violations));
        }

        assertTrue(
            violations.isEmpty(),
            "Se detectaron @Disabled sin razón explícita: " + String.join(" | ", violations)
        );
    }

    private static void inspectFile(Path path, List<String> violations) {
        String content;
        try {
            content = Files.readString(path);
        } catch (IOException ex) {
            violations.add(path + " (no se pudo leer)");
            return;
        }

        int disabledCount = countMatches(DISABLED_ANNOTATION.matcher(content));
        int disabledWithReasonCount = 0;

        Matcher matcher = DISABLED_WITH_REASON.matcher(content);
        while (matcher.find()) {
            String reason = matcher.group(1);
            if (reason != null && !reason.trim().isEmpty()) {
                disabledWithReasonCount++;
            }
        }

        if (disabledCount > disabledWithReasonCount) {
            violations.add(path + " [@Disabled=" + disabledCount + ", conRazon=" + disabledWithReasonCount + "]");
        }
    }

    private static int countMatches(Matcher matcher) {
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
