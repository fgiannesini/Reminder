package com.fgiannesini;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

public class RecentWordsWindow {
    static final int WINDOW_SIZE = 20;
    private final Deque<String> recentWords = new ArrayDeque<>();

    public void add(String word) {
        recentWords.addLast(word);
        if (recentWords.size() > WINDOW_SIZE) recentWords.removeFirst();
    }

    public boolean contains(String word) {
        return recentWords.stream().anyMatch(word::equals);
    }

    public Collection<String> getWordsForExclusion() {
        return List.copyOf(recentWords);
    }
}
