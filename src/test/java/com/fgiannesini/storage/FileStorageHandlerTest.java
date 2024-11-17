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

        var expected = List.of(new Word("ao inves, em vez de", "au lieu de", 1, null), new Word("au lieu de", "ao inves, em vez de", 2, null), new Word("acender", "allumer", 0, null), new Word("allumer", "acender", 0, null));
        Assertions.assertEquals(expected, wordList);
    }

    @Test
    void should_save(@TempDir Path tempDir) throws IOException {
        writeInTempFile(tempDir, """
                ao inves, em vez de;au lieu de;1;
                ou seja;c'est à dire;5;20240702T115135""");
        var storageHandler = new FileStorageHandler(tempDir);

        var wordsToSave = List.of(
                new Word("ao inves, em vez de", "au lieu de", 2, null),
                new Word("ou seja", "c'est à dire", 5, LocalDateTime.of(2024, 7, 3, 13, 18, 0)
                ));

        storageHandler.save(wordsToSave);

        var actual = readTempFile(tempDir);
        Assertions.assertEquals("""
                ao inves, em vez de;au lieu de;2;
                ou seja;c'est à dire;5;20240703T131800""", actual);
    }

    @Test
    void should_find_a_word(@TempDir Path tempDir) throws IOException {
        writeInTempFile(tempDir, """
                ao inves, em vez de;au lieu de;1;
                ou seja;c'est à dire;3;""");
        var storageHandler = new FileStorageHandler(tempDir);
        storageHandler.load();

        var word = storageHandler.find("ou seja");

        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, null), word);
    }

    @Test
    void should_update_memory_list_and_temp_file(@TempDir Path tempDir) throws IOException {
        writeInTempFile(tempDir, """
                ao inves, em vez de;au lieu de;1;
                ou seja;c'est à dire;2;""");
        var storageHandler = new FileStorageHandler(tempDir);
        storageHandler.load();
        storageHandler.update(new Word("ou seja", "c'est à dire", 3, LocalDateTime.of(2024, 7, 3, 13, 18, 0)));

        var actualWord = storageHandler.find("ou seja");
        Assertions.assertEquals(new Word("ou seja", "c'est à dire", 3, LocalDateTime.of(2024, 7, 3, 13, 18, 0)), actualWord);

        var actualTempFile = readTempFile(tempDir);
        Assertions.assertEquals("""
                ao inves, em vez de;au lieu de;1;
                ou seja;c'est à dire;3;20240703T131800""", actualTempFile);
    }

    @Test
    void should_find_next_words_ordered_by_learnt_moment_null_first(@TempDir Path tempDir) throws IOException {
        writeInTempFile(tempDir, """
                ligar;allumer;3;20240817T114002
                ao inves, em vez de;au lieu de;3;20200817T114002
                ou seja;c'est à dire;2;
                """);

        var storageHandler = new FileStorageHandler(tempDir);
        storageHandler.load();

        var nextWords = storageHandler.getNextWords(2);
        Assertions.assertEquals(List.of(
                new Word("ou seja", "c'est à dire", 2, null),
                new Word("ao inves, em vez de", "au lieu de", 3, LocalDateTime.of(2020, 8, 17, 11, 40, 2))
        ), nextWords);
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