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
                boolean wordToLearn = getSplit(word.wordToLearn()).stream().anyMatch(s -> getSplit(word2.wordToLearn()).contains(s));
                if (wordToLearn) {
                    System.out.println(word.wordToLearn() + " " + i + " " + word2.wordToLearn() + " " + j);
                }
                boolean translation = getSplit(word.translation()).stream().anyMatch(s -> getSplit(word2.translation()).contains(s));
                if (translation) {
                    System.out.println(word.translation() + " " + i + " " + word2.translation() + " " + j);
                }
            }
        }
    }

    public static List<String> getSplit(String word) {
        return Arrays.stream(word.split(";")[0].split(",")).filter(string -> !string.trim().startsWith("(")).toList();
    }
}