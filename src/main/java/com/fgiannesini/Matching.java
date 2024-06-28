package com.fgiannesini;

import java.util.List;

public enum Matching {
    MATCHED,
    CLOSED,
    NOT_MATCHED;

    static Matching from(List<Matching> list) {
        if (list.stream().anyMatch(match -> match == CLOSED)) {
            return CLOSED;
        }
        if (list.stream().anyMatch(match -> match == MATCHED)) {
            return MATCHED;
        }
        return NOT_MATCHED;
    }
}
