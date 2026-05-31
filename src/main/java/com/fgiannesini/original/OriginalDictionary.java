package com.fgiannesini.original;

import com.fgiannesini.Word;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<String, List<Word>> findDuplicates(List<Word> words) {
        Map<String, List<Word>> groups = new LinkedHashMap<>();
        for (Word word : words) {
            groups.computeIfAbsent(word.wordToLearn(), k -> new ArrayList<>()).add(word);
            groups.computeIfAbsent(word.translation(), k -> new ArrayList<>()).add(word);
        }
        return groups.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
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
