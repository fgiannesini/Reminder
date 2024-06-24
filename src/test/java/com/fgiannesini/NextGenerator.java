package com.fgiannesini;

import java.util.random.RandomGenerator;

class NextGenerator implements RandomGenerator {
    private int counter = 0;

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public int nextInt() {
        return counter++;
    }
}
