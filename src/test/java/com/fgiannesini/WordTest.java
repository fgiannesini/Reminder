package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
        Assertions.assertEquals(word.getMatching(input), Matching.MATCHED);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "c'est à dir",
            "",
            " ",
    })
    void should_not_validate_french_if_input_has_mistakes(String input) {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(word.getMatching(input), Matching.NOT_MATCHED);
    }

    @Test
    void should_be_closed_to_matching_french_if_input_has_accents() {
        Word word = new Word("ou seja", "c'est à dire");
        Assertions.assertEquals(word.getMatching("c est a dire"), Matching.CLOSED);
    }

    @Test
    void should_handle_two_translations() {
        Word word = new Word("conferir", "confirmer, vérifier");
        Assertions.assertEquals(word.getMatching("confirmer"), Matching.MATCHED);
        Assertions.assertEquals(word.getMatching("vérifier"), Matching.MATCHED);
        Assertions.assertEquals(word.getMatching("confirmer, vérifier"), Matching.MATCHED);
        Assertions.assertEquals(word.getMatching("vérifier, confirmer"), Matching.MATCHED);
    }
}