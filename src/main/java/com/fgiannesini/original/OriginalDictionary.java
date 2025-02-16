package com.fgiannesini.original;

import com.fgiannesini.Word;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class OriginalDictionary {

    private final InputStream csvInputStream;

    public OriginalDictionary(InputStream csvInputStream) {
        this.csvInputStream = csvInputStream;
    }

    public List<Word> load() throws IOException {
        return readCsvFile(csvInputStream)
                .stream()
                .peek(CsvOriginalWord::check)
                .map(CsvOriginalWord::toWord)
                .toList();
    }

    private List<CsvOriginalWord> readCsvFile(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            CsvToBean<CsvOriginalWord> cb = new CsvToBeanBuilder<CsvOriginalWord>(reader)
                    .withType(CsvOriginalWord.class)
                    .withSeparator(';')
                    .build();
            return cb.parse();
        }
    }
}
