package com.fgiannesini;

import java.util.ArrayDeque;
import java.util.Deque;

public class RecentWordsWindow {
    private static final int WINDOW_SIZE = 10;
    private final Deque<String> recentWords = new ArrayDeque<>();

    public void add(String word) {
        recentWords.addLast(word);
        if (recentWords.size() > WINDOW_SIZE) recentWords.removeFirst();
    }

    public boolean containsTranslation(String translation) {
        return recentWords.stream().anyMatch(translation::equals);
    }
}
