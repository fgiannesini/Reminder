package com.fgiannesini;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
        outputStream.write((text + "\n").getBytes(StandardCharsets.UTF_8));
    }

    public void run(Dictionary dictionary) throws IOException {
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        for (; ; ) {
            var word = dictionary.next(20);
            write(this.outputStream, word.word());
            String input = scanner.nextLine();
            if (input.equals("quit")) {
                write(this.outputStream, "Bye");
                return;
            }
            Matching matching = word.getMatching(input);
            switch (matching) {
                case MATCHED -> write(this.outputStream, "OK (" + word.translation() + ")\n");
                case CLOSED -> write(this.outputStream, "CLOSED (" + word.translation() + ")\n");
                case NOT_MATCHED -> write(this.outputStream, "KO (" + word.translation() + ")\n");
            }

            Word newWord = switch (matching) {
                case MATCHED, CLOSED -> word.checked();
                case NOT_MATCHED -> word.reset();
            };
            if (newWord.isLearned()) {
                write(this.outputStream, "Translation '" + newWord.word() + " -> " + newWord.translation() + "' learned\n");
            }
            dictionary.update(newWord);
        }
    }

}
