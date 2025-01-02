package com.fgiannesini.console.storage;

import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;
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

import static java.util.Comparator.*;

public class FileStorageHandler implements StorageHandler {

    private final Path tempFilePath;
    private List<Word> words;

    public FileStorageHandler(Path storageDir) throws IOException {
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        tempFilePath = storageDir.resolve("dictionary.csv");
        this.words = List.of();
    }

    @Override
    public List<Word> load() throws IOException {
        if (Files.exists(tempFilePath)) {
            this.words = readCsvFile(Files.newInputStream(tempFilePath))
                    .stream()
                    .map(CsvWord::toWord)
                    .toList();
        }
        return words;
    }

    @Override
    public void save(List<Word> words) throws IOException {
        writeCsvFile(words, tempFilePath);
    }

    @Override
    public Word find(String wordToLearn) {
        return words.stream()
                .filter(word -> word.wordToLearn().equals(wordToLearn))
                .findAny()
                .orElse(null);
    }

    @Override
    public void update(Word newWord) throws IOException {
        this.words = words.stream()
                .map(word -> word.isSimilarTo(newWord) ? newWord : word)
                .toList();
        this.save(this.words);
    }

    @Override
    public List<Word> getNextWords(int limit) {
        return this.words.stream()
                .sorted(comparing(Word::learnedMoment, nullsFirst(naturalOrder())))
                .limit(limit)
                .toList();
    }

    @Override
    public void delete(List<Word> wordsToDelete) throws IOException {
        this.words = words.stream()
                .filter(word -> wordsToDelete.stream().noneMatch(word::isSimilarTo))
                .toList();
        this.save(this.words);
    }

    @Override
    public long getRemainingWordsCountToLearn() {
        return words.stream().filter(word -> !word.isLearnt()).count();
    }

    private void writeCsvFile(List<Word> words, Path filePath) throws IOException {
        var csvWords = words.stream()
                .map(CsvWord::fromWord)
                .toList();

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

    private List<CsvWord> readCsvFile(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            CsvToBean<CsvWord> cb = new CsvToBeanBuilder<CsvWord>(reader)
                    .withType(CsvWord.class)
                    .withSeparator(';')
                    .build();
            return cb.parse();
        }
    }
}
