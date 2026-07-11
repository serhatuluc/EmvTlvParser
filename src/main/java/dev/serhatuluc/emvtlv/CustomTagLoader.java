package dev.serhatuluc.emvtlv;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads user-editable tag/name overrides and additions from a JSON file in the current working
 * directory (i.e. the project root when launched via {@code run.bat} or {@code mvn javafx:run}
 * from there), so new (proprietary, regional, or translated) tags can be added without touching
 * source code or rebuilding. If the file doesn't exist yet, a template with one example entry is
 * written so the expected format is self-documenting.
 *
 * <p>Deliberately NOT placed next to the compiled classes ({@code target/classes}) - that
 * location is wiped by {@code mvn clean} and isn't somewhere a user would think to look.
 */
public final class CustomTagLoader {

    public static final String FILE_NAME = "custom-tags.json";

    private CustomTagLoader() {
    }

    public record LoadResult(Map<String, String> tags, Path filePath, String error) {
        public boolean hasError() {
            return error != null;
        }
    }

    public static LoadResult load() {
        return load(locateFile());
    }

    /** Package-visible overload used directly by tests, bypassing jar-location detection. */
    static LoadResult load(Path file) {
        if (!Files.exists(file)) {
            writeTemplate(file);
            return new LoadResult(Map.of(), file, null);
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Type mapType = new TypeToken<LinkedHashMap<String, String>>() {
            }.getType();
            Map<String, String> parsed = new Gson().fromJson(reader, mapType);
            return new LoadResult(parsed == null ? Map.of() : Map.copyOf(parsed), file, null);
        } catch (JsonSyntaxException ex) {
            return new LoadResult(Map.of(), file, "custom-tags.json contains invalid JSON: " + ex.getMessage());
        } catch (IOException | UncheckedIOException ex) {
            return new LoadResult(Map.of(), file, "custom-tags.json could not be read: " + ex.getMessage());
        }
    }

    private static void writeTemplate(Path file) {
        String template = """
                {
                  "FFFF": "Example entry - delete this and add your own tags as (hex tag: description).",
                  "DF7F": "Example proprietary tag description"
                }
                """;
        try {
            Files.writeString(file, template, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            // Best-effort only - if we can't write the template (read-only install dir etc.),
            // the app still runs fine with just the built-in tag list.
        }
    }

    private static Path locateFile() {
        return Path.of(System.getProperty("user.dir"), FILE_NAME);
    }
}
