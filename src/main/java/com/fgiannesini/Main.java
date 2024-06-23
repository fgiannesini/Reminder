package com.fgiannesini;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new Reminder(System.in, new StringOutputStream(System.out));
    }
}