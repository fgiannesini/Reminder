package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WordTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "c'est à dire",
            "c'est à DIRE",
            "cestàdire",
            "c est à dire",
            "c est0à_dire",
            " c  est  à dire ",
    })
    void should_validate_french_if_input_has_mistakes(String input) {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertTrue(word.isFrench(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "c'est à dir",
            "",
            " ",
    })
    void should_not_validate_french_if_input_has_mistakes(String input) {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertFalse(word.isFrench(input));
    }
}