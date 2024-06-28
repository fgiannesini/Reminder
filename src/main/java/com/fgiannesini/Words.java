package com.fgiannesini;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

public record Words(RandomGenerator randomProvider, List<Word> words) {

    public Words(RandomGenerator randomProvider, Word... words) {
        this(randomProvider, Arrays.stream(words).toList());
    }

    public static Words from(RandomGenerator randomProvider, Path csvFilePath) throws IOException {
        try (Reader reader = Files.newBufferedReader(csvFilePath)) {
            CsvToBean<CsvWord> cb = new CsvToBeanBuilder<CsvWord>(reader)
                    .withType(CsvWord.class)
                    .withSeparator(';')
                    .build();
            List<CsvWord> csvWords = cb.parse();
            List<Word> words = csvWords.stream().map(CsvWord::toWord).toList();
            return new Words(randomProvider, words);
        }
    }

    public Word next() {
        return words.get(randomProvider.nextInt(words.size()));
    }
}
