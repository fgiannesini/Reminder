package com.fgiannesini;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileStorageHandler implements StorageHandler {

    public List<Word> load(Path csvFilePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(csvFilePath)) {
            CsvToBean<CsvOriginalWord> cb = new CsvToBeanBuilder<CsvOriginalWord>(reader)
                    .withType(CsvOriginalWord.class)
                    .withSeparator(';')
                    .build();
            List<CsvOriginalWord> csvOriginalWords = cb.parse();
            return csvOriginalWords.stream().map(CsvOriginalWord::toWord).toList();
        }
    }
}
