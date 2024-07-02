package com.fgiannesini.storage;

import com.fgiannesini.Word;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileStorageHandler implements StorageHandler {

    private final Path storageDir;
    private final Path originalCsvFilePath;

    public FileStorageHandler(Path storageDir, Path originalCsvFilePath) {
        this.storageDir = storageDir;
        this.originalCsvFilePath = originalCsvFilePath;
    }

    private <T> List<T> readCsvFile(Path csvFilePath, Class<T> type) throws IOException {
        try (Reader reader = Files.newBufferedReader(csvFilePath)) {
            CsvToBean<T> cb = new CsvToBeanBuilder<T>(reader)
                    .withType(type)
                    .withSeparator(';')
                    .build();
            return cb.parse();
        }
    }

    private static Word getOppositeWord(Word word) {
        return new Word(word.translation(), word.word());
    }

    public List<Word> load() throws IOException {
        var words = readCsvFile(originalCsvFilePath, CsvOriginalWord.class)
                .stream()
                .map(CsvOriginalWord::toWord)
                .toList();

        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        Path tempFilePath = storageDir.resolve(originalCsvFilePath.getFileName());
        if (tempFilePath.toFile().exists()) {
            List<Word> existingWords = readCsvFile(tempFilePath, CsvWord.class)
                    .stream()
                    .map(CsvWord::toWord)
                    .toList();
            var existingWordsWithoutRemoved = existingWords.stream().filter(existingWord -> {
                return words.stream().anyMatch(word -> existingWord.isSimilarTo(word) || existingWord.isSimilarTo(getOppositeWord(word)));
            }).toList();
            List<Word> wordsToAdd = words.stream().filter(word -> {
                        return existingWordsWithoutRemoved.stream().noneMatch(existingWord -> existingWord.isSimilarTo(word) || existingWord.isSimilarTo(getOppositeWord(word)));
                    })
                    .flatMap(word -> Stream.of(
                            word,
                            getOppositeWord(word)
                    ))
                    .toList();
            List<Word> list = Stream.concat(existingWordsWithoutRemoved.stream(), wordsToAdd.stream()).toList();
            writeCsvFile(list, tempFilePath);
            return list;
        } else {
            var wordsWithDuplicates = words.stream()
                    .flatMap(word -> Stream.of(
                            word,
                            getOppositeWord(word)
                    ))
                    .toList();
            writeCsvFile(wordsWithDuplicates, tempFilePath);
            return wordsWithDuplicates;
        }
    }

    @Override
    public void save(List<Word> words) throws IOException {
        Path tempFilePath = storageDir.resolve(originalCsvFilePath.getFileName());
        writeCsvFile(words, tempFilePath);
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
}
