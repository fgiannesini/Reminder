package com.fgiannesini;

import java.util.Objects;
import java.util.random.RandomGenerator;

class NextGenerator implements RandomGenerator {
    private int counter = 0;

    @Override
    public long nextLong() {
        return 0;
    }

    @Override
    public int nextInt(int n) {
        return counter++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NextGenerator that = (NextGenerator) o;
        return counter == that.counter;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(counter);
    }
}
