package com.fgiannesini;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Reminder {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public Reminder(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        write(this.outputStream, "Reminder");
    }

    private static void write(OutputStream outputStream, String text) throws IOException {
        outputStream.write((text + "\n").getBytes());
    }

    public void run(Words dictionary) throws IOException {
        Scanner scanner = new Scanner(inputStream);
        for (; ; ) {
            var word = dictionary.next();
            write(this.outputStream, word.portugues());
            String s = scanner.nextLine();
            if (s.equals("quit")) {
                write(this.outputStream, "Bye");
                return;
            }
            switch (word.isFrenchMatching(s)) {
                case MATCHED -> write(this.outputStream, "OK\n");
                case CLOSED -> write(this.outputStream, "CLOSED (" + word.french() + ")\n");
                case NOT_MATCHED -> write(this.outputStream, "KO (" + word.french() + ")\n");
            }
        }
    }

}
