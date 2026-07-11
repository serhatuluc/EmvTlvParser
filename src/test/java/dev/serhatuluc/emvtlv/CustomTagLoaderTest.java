package dev.serhatuluc.emvtlv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomTagLoaderTest {

    @Test
    void writesTemplateWhenFileMissing(@TempDir Path tempDir) {
        Path file = tempDir.resolve(CustomTagLoader.FILE_NAME);
        assertFalse(Files.exists(file));

        CustomTagLoader.LoadResult result = CustomTagLoader.load(file);

        assertFalse(result.hasError());
        assertTrue(result.tags().isEmpty());
        assertTrue(Files.exists(file), "a template file should have been written");
    }

    @Test
    void loadsCustomTagsFromExistingFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(CustomTagLoader.FILE_NAME);
        Files.writeString(file, """
                {
                  "9F7C": "Custom description - Merchant Custom Data",
                  "DFAA": "Our own proprietary tag"
                }
                """, StandardCharsets.UTF_8);

        CustomTagLoader.LoadResult result = CustomTagLoader.load(file);

        assertFalse(result.hasError());
        assertEquals(2, result.tags().size());
        assertEquals("Custom description - Merchant Custom Data", result.tags().get("9F7C"));
        assertEquals("Our own proprietary tag", result.tags().get("DFAA"));
    }

    @Test
    void invalidJsonReportsErrorInsteadOfCrashing(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve(CustomTagLoader.FILE_NAME);
        Files.writeString(file, "{ this is not valid json", StandardCharsets.UTF_8);

        CustomTagLoader.LoadResult result = CustomTagLoader.load(file);

        assertTrue(result.hasError());
        assertTrue(result.tags().isEmpty());
    }
}
