package com.fgiannesini.console;

import com.fgiannesini.Dictionary;
import com.fgiannesini.Matching;
import com.fgiannesini.Word;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ReminderConsole {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public ReminderConsole(InputStream inputStream, OutputStream outputStream) throws IOException {
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
            write(this.outputStream, word.wordToLearn());
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
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
            if (newWord.shouldBeMarkedAsLearnt()) {
                write(this.outputStream, "Translation '" + newWord.wordToLearn() + " -> " + newWord.translation() + "' learned\n");
            }
            dictionary.update(newWord);
        }
    }

}
