package com.fgiannesini.storage;

import com.fgiannesini.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class FileStorageHandlerTest {

    private static String readTempFile(Path tempDir) throws IOException {
        File[] files = tempDir.toFile().listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.length);
        File file = files[0];
        List<String> elements = Files.readAllLines(file.toPath());
        return String.join("\n", elements);
    }

    @Test
    void should_load_from_resource_file_and_store_a_copy(@TempDir Path tempDir) throws URISyntaxException, IOException {
        var path = Paths.get(ClassLoader.getSystemResource("dictionary-for-test.csv").toURI());

        var storageHandler = new FileStorageHandler(tempDir, path);
        List<Word> wordList = storageHandler.load();

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("ou seja", "c'est à dire")
        );

        Assertions.assertEquals(expected, wordList);

        String actual = readTempFile(tempDir);
        Assertions.assertEquals(actual, """
                ao inves, em vez de;au lieu de;0
                au lieu de;ao inves, em vez de;0
                ou seja;c'est à dire;0
                c'est à dire;ou seja;0""");
    }

    @Test
    void should_load_the_copy_if_exists(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("dictionary-for-test.csv"), """
                ao inves, em vez de;au lieu de;1
                ou seja;c'est à dire;2""");
        var storageHandler = new FileStorageHandler(tempDir, Paths.get("dictionary-for-test.csv"));
        List<Word> wordList = storageHandler.load();

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de", 1),
                new Word("ou seja", "c'est à dire", 2)
        );
        Assertions.assertEquals(expected, wordList);
    }

    @Test
    void should_save_the_updated_copy(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("dictionary-for-test.csv"), """
                ao inves, em vez de;au lieu de;1
                ou seja;c'est à dire;2""");
        var storageHandler = new FileStorageHandler(tempDir, Paths.get("dictionary-for-test.csv"));

        var wordsToSave = List.of(
                new Word("ao inves, em vez de", "au lieu de", 1),
                new Word("ou seja", "c'est à dire", 3)
        );

        storageHandler.save(wordsToSave);

        String actual = readTempFile(tempDir);
        Assertions.assertEquals(actual, """
                ao inves, em vez de;au lieu de;1
                ou seja;c'est à dire;3""");
    }
}