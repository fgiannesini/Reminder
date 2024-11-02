package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.fgiannesini.console.storage.FileStorageHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

class FileStorageHandlerTest {

    @Test
    void should_create_resource_file_if_not_existing(@TempDir Path tempDir) throws IOException {
        var storageDir = buildTestStorageDir(tempDir);
        var storageHandler = new FileStorageHandler(storageDir);
        var wordList = storageHandler.load();
        Assertions.assertEquals(List.of(), wordList);
    }


    @Test
    void should_load_existing_temp_file(@TempDir Path tempDir) throws IOException {
        writeInTempFile(tempDir, """
                ao inves, em vez de;au lieu de;1;
                au lieu de;ao inves, em vez de;2;
                acender;allumer;0;
                allumer;acender;0;""");

        var storageHandler = new FileStorageHandler(tempDir);
        var wordList = storageHandler.load();

        var expected = List.of(
                new Word("ao inves, em vez de", "au lieu de", 1, null),
                new Word("au lieu de", "ao inves, em vez de", 2, null),
                new Word("acender", "allumer", 0, null),
                new Word("allumer", "acender", 0, null));
        Assertions.assertEquals(expected, wordList);
    }

    @Test
    void should_save_the_updated_copy(@TempDir Path tempDir) throws IOException {
        writeInTempFile(tempDir, """
                ao inves, em vez de;au lieu de;1;
                ou seja;c'est à dire;5;20240702T115135""");
        var storageHandler = new FileStorageHandler(tempDir);

        var wordsToSave = List.of(new Word("ao inves, em vez de", "au lieu de", 2, null), new Word("ou seja", "c'est à dire", 5, LocalDateTime.of(2024, 7, 3, 13, 18, 0)));

        storageHandler.save(wordsToSave);

        var actual = readTempFile(tempDir);
        Assertions.assertEquals(actual, """
                ao inves, em vez de;au lieu de;2;
                ou seja;c'est à dire;5;20240703T131800""");
    }

    private String readTempFile(Path testStorageDir) throws IOException {
        var files = testStorageDir.toFile().listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.length);
        var file = files[0];
        var elements = Files.readAllLines(file.toPath());
        return String.join("\n", elements);
    }

    private void writeInTempFile(Path tempDir, String csq) throws IOException {
        Files.writeString(tempDir.resolve("dictionary.csv"), csq);
    }

    private Path buildTestStorageDir(Path tempDir) {
        return tempDir.resolve("Reminder");
    }
}