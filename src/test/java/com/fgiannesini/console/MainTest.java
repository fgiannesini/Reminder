package com.fgiannesini.console;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class MainTest {

    @Test
    void should_reject_words_with_parenthesis_when_getting_split() {
        var list = Main.getSplit("allumer, (l'électricité)");
        Assertions.assertEquals(List.of("allumer"),list);
    }
}