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
import java.time.LocalDateTime;
import java.util.List;

class FileStorageHandlerTest {


    @Test
    void should_load_from_resource_file_and_store_a_copy_if_not_existing(@TempDir Path tempDir) throws URISyntaxException, IOException {
        Path storageDir = buildTestStorageDir(tempDir);
        var storageHandler = new FileStorageHandler(storageDir, getTestOriginalCsvFilePath());
        List<Word> wordList = storageHandler.load();

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de"),
                new Word("au lieu de", "ao inves, em vez de"),
                new Word("ou seja", "c'est à dire"),
                new Word("c'est à dire", "ou seja")
        );

        Assertions.assertEquals(expected, wordList);

        String actual = readTempFile(storageDir);
        Assertions.assertEquals(actual, """
                ao inves, em vez de;au lieu de;0;
                au lieu de;ao inves, em vez de;0;
                ou seja;c'est à dire;0;
                c'est à dire;ou seja;0;""");
    }


    @Test
    void should_load_the_copy_if_exists_and_synchronize_words(@TempDir Path tempDir) throws IOException, URISyntaxException {
        writeInTempFile(tempDir, """
                ao inves, em vez de;au lieu de;1;
                au lieu de;ao inves, em vez de;2;
                acender;allumer;3;
                allumer;acender;4;""");

        var storageHandler = new FileStorageHandler(tempDir, getTestOriginalCsvFilePath());
        List<Word> wordList = storageHandler.load();

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de", 1, null),
                new Word("au lieu de", "ao inves, em vez de", 2, null),
                new Word("ou seja", "c'est à dire", 0, null),
                new Word("c'est à dire", "ou seja", 0, null)
        );
        Assertions.assertEquals(expected, wordList);

        String actual = readTempFile(tempDir);
        Assertions.assertEquals(actual, """
                ao inves, em vez de;au lieu de;1;
                au lieu de;ao inves, em vez de;2;
                ou seja;c'est à dire;0;
                c'est à dire;ou seja;0;""");
    }

    @Test
    void should_save_the_updated_copy(@TempDir Path tempDir) throws IOException {
        writeInTempFile(tempDir, """
                ao inves, em vez de;au lieu de;1;
                ou seja;c'est à dire;5;20240702T115135""");
        var storageHandler = new FileStorageHandler(tempDir, Paths.get("dictionary-for-test.csv"));

        var wordsToSave = List.of(
                new Word("ao inves, em vez de", "au lieu de", 2, null),
                new Word("ou seja", "c'est à dire", 5, LocalDateTime.of(2024, 7, 3, 13, 18, 0))
        );

        storageHandler.save(wordsToSave);

        String actual = readTempFile(tempDir);
        Assertions.assertEquals(actual, """
                ao inves, em vez de;au lieu de;2;
                ou seja;c'est à dire;5;20240703T131800""");
    }

    private String readTempFile(Path testStorageDir) throws IOException {
        File[] files = testStorageDir.toFile().listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.length);
        File file = files[0];
        List<String> elements = Files.readAllLines(file.toPath());
        return String.join("\n", elements);
    }

    private void writeInTempFile(Path tempDir, String csq) throws IOException {
        Files.writeString(tempDir.resolve("dictionary-for-test.csv"), csq);
    }

    private Path getTestOriginalCsvFilePath() throws URISyntaxException {
        return Paths.get(ClassLoader.getSystemResource("dictionary-for-test.csv").toURI());
    }

    private Path buildTestStorageDir(Path tempDir) {
        return tempDir.resolve("Reminder");
    }
}