package com.fgiannesini;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecentWordsWindowTest {

    @Test
    void should_contain_added_word() {
        var window = new RecentWordsWindow();
        window.add("desligar");
        assertTrue(window.contains("desligar"));
    }

    @Test
    void should_not_contain_word_not_added() {
        var window = new RecentWordsWindow();
        window.add("desligar");
        assertFalse(window.contains("acender"));
    }

    @Test
    void should_evict_oldest_word_after_window_size() {
        var window = new RecentWordsWindow();
        window.add("evicted");
        for (int i = 0; i < 10; i++) window.add("word" + i);
        assertFalse(window.contains("evicted"));
    }

    @Test
    void should_keep_word_within_window_size() {
        var window = new RecentWordsWindow();
        window.add("kept");
        for (int i = 0; i < 9; i++) window.add("word" + i);
        assertTrue(window.contains("kept"));
    }
}
