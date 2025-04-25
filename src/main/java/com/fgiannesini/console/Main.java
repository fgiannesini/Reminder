package com.fgiannesini.console;

import com.fgiannesini.Word;
import com.fgiannesini.original.OriginalDictionary;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        var originalFileInputStream = ClassLoader.getSystemResourceAsStream("dictionary.csv");
        List<Word> load = new OriginalDictionary(originalFileInputStream).load();
        for (int i = 0; i < load.size() - 1; i++) {
            Word word = load.get(i);
            for (int j = i + 1; j < load.size(); j++) {
                Word word2 = load.get(j);
                boolean wordToLearn = Arrays.stream(getSplit(word.wordToLearn())).anyMatch(s -> Arrays.asList(getSplit(word2.wordToLearn())).contains(s));
                if (wordToLearn) {
                    System.out.println(word.wordToLearn() + " " + i + " " + word2.wordToLearn() + " " + j);
                }
                boolean translation = Arrays.stream(getSplit(word.translation())).anyMatch(s -> Arrays.asList(getSplit(word2.translation())).contains(s));
                if (translation) {
                    System.out.println(word.translation() + " " + i + " " + word2.translation() + " " + j);
                }
            }
        }
    }

    private static String[] getSplit(String word) {
        return word.split(";")[0].split(",");
    }
}