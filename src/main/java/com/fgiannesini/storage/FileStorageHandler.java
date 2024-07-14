package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileStorageHandler implements StorageHandler {

    private final Path storageDir;
    private final InputStream inputStream;
    private final String tempFileName = "dictionary.csv";

    public FileStorageHandler(Path storageDir, InputStream originalFileInputStream) {
        this.storageDir = storageDir;
        this.inputStream = originalFileInputStream;
    }

    private static List<Word> addDuplicates(List<Word> words) {
        return words.stream()
                .flatMap(word -> Stream.of(
                        word,
                        buildDuplicate(word)
                ))
                .toList();
    }

    private static Word buildDuplicate(Word word) {
        return new Word(word.translation(), word.word(), word.checkedCount(), null);
    }

    public List<Word> load() throws IOException {
        var words = readCsvFile(inputStream, CsvOriginalWord.class)
                .stream()
                .map(CsvOriginalWord::toWord)
                .toList();

        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        Path tempFilePath = storageDir.resolve(tempFileName);
        if (tempFilePath.toFile().exists()) {
            var existingWords = readCsvFile(Files.newInputStream(tempFilePath), CsvWord.class)
                    .stream()
                    .map(CsvWord::toWord)
                    .toList();
            var synchronisedWords = synchronize(words, existingWords);
            writeCsvFile(synchronisedWords, tempFilePath);
            return synchronisedWords;
        } else {
            var wordsWithDuplicates = addDuplicates(words);
            writeCsvFile(wordsWithDuplicates, tempFilePath);
            return wordsWithDuplicates;
        }
    }

    @Override
    public void save(List<Word> words) throws IOException {
        Path tempFilePath = storageDir.resolve(tempFileName);
        writeCsvFile(words, tempFilePath);
    }

    private List<Word> synchronize(List<Word> words, List<Word> existingWords) {
        var existingWordsToKeep = existingWords.stream()
                .filter(existingWord -> words.stream().anyMatch(word -> isWordOrDuplicate(word, existingWord)))
                .toList();
        var wordsToAdd = words.stream()
                .filter(word -> existingWords.stream().noneMatch(existingWord -> isWordOrDuplicate(word, existingWord)))
                .toList();
        var wordsToAddWithDuplicates = addDuplicates(wordsToAdd);
        return Stream.concat(existingWordsToKeep.stream(), wordsToAddWithDuplicates.stream()).toList();
    }

    private void writeCsvFile(List<Word> words, Path filePath) throws IOException {
        var csvWords = words.stream().map(CsvWord::fromWord).toList();

        try (Writer writer = new FileWriter(filePath.toFile(), StandardCharsets.UTF_8)) {
            StatefulBeanToCsv<CsvWord> sbc = new StatefulBeanToCsvBuilder<CsvWord>(writer)
                    .withSeparator(';')
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            sbc.write(csvWords);
        } catch (CsvException e) {
            throw new IOException(e);
        }
    }

    private boolean isWordOrDuplicate(Word word1, Word word2) {
        return word1.isSimilarTo(word2) || word1.isSimilarTo(buildDuplicate(word2));
    }

    private <T> List<T> readCsvFile(InputStream inputStream, Class<T> type) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            CsvToBean<T> cb = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withSeparator(';')
                    .build();
            return cb.parse();
        }
    }

}
