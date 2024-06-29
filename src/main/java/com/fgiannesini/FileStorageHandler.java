package com.fgiannesini;

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

public class FileStorageHandler implements StorageHandler {

    private final Path storageDir;

    public FileStorageHandler(Path storageDir) {
        this.storageDir = storageDir;
    }

    private static List<CsvOriginalWord> readCsvFile(Path csvFilePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(csvFilePath)) {
            CsvToBean<CsvOriginalWord> cb = new CsvToBeanBuilder<CsvOriginalWord>(reader)
                    .withType(CsvOriginalWord.class)
                    .withSeparator(';')
                    .build();
            return cb.parse();
        }
    }

    public List<Word> load(Path csvFilePath) throws IOException {
        var words = readCsvFile(csvFilePath)
                .stream()
                .map(CsvOriginalWord::toWord)
                .toList();

        writeCsvFile(words, storageDir.resolve(csvFilePath.getFileName()));
        return words;
    }

    private void writeCsvFile(List<Word> words, Path filePath) throws IOException {
        var csvWords = words.stream().map(w -> {
            CsvWord csvWord = new CsvWord();
            csvWord.setWord(w.word());
            csvWord.setTranslation(w.translation());
            return csvWord;
        }).toList();

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
