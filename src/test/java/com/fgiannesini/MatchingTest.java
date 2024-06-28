package com.fgiannesini;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class MatchingTest {

    @Test
    void should_get_Matched_over_Not_Matched() {
        Assertions.assertEquals(Matching.MATCHED, Matching.from(List.of(Matching.MATCHED, Matching.NOT_MATCHED)));
    }

    @Test
    void should_get_Closed_over_Not_Matched() {
        Assertions.assertEquals(Matching.CLOSED, Matching.from(List.of(Matching.CLOSED, Matching.NOT_MATCHED)));
    }

    @Test
    void should_get_Closed_over_Matched() {
        Assertions.assertEquals(Matching.CLOSED, Matching.from(List.of(Matching.CLOSED, Matching.MATCHED)));
    }
}