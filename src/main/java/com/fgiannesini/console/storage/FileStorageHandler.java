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

public class FileStorageHandler implements StorageHandler {

    private final Path tempFilePath;

    public FileStorageHandler(Path storageDir) throws IOException {
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
        tempFilePath = storageDir.resolve("dictionary.csv");
    }

    @Override
    public List<Word> load() throws IOException {
        if (!Files.exists(tempFilePath)) {
            return List.of();
        }
        return readCsvFile(Files.newInputStream(tempFilePath))
                .stream()
                .map(CsvWord::toWord)
                .toList();
    }

    @Override
    public void save(List<Word> words) throws IOException {
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
