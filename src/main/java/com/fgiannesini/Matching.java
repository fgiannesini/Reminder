package com.fgiannesini;

import java.util.List;

public enum Matching {
    MATCHED(5),
    CLOSED(3),
    NOT_MATCHED(0);

    private final int quality;

    Matching(int quality) {
        this.quality = quality;
    }

    public int quality() {
        return quality;
    }

    static Matching from(List<Matching> list) {
        if (list.stream().anyMatch(match -> match == MATCHED)) {
            return MATCHED;
        }
        if (list.stream().anyMatch(match -> match == CLOSED)) {
            return CLOSED;
        }
        return NOT_MATCHED;
    }
}
