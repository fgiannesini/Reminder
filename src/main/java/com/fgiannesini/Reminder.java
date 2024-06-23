package com.fgiannesini;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class Reminder {
    private final InputStream inputStream;
    private final StringOutputStream outputStream;

    public Reminder(InputStream inputStream, StringOutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        outputStream.writeWithLineBreak("Reminder");
    }

    public void run() throws IOException {
        var dictionnary = List.of(new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        Scanner scanner = new Scanner(inputStream);
        for (var word : dictionnary) {
            outputStream.writeWithLineBreak(word.portugues);
            String s = scanner.nextLine();
            if (s.equals("quit")) {
                outputStream.writeWithLineBreak("Bye");
                return;
            }
            if (word.french.equals(s)) {
                outputStream.writeWithLineBreak("OK");
            } else {
                outputStream.writeWithLineBreak("KO (éteindre)");
            }
        }
    }

    private record Word(String portugues, String french) {
    }
}
