package com.fgiannesini;

import java.util.random.RandomGenerator;

public class NextGenerator implements RandomGenerator {
    private int counter = 0;

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public int nextInt(int n) {
        return counter++ % n;
    }
}
