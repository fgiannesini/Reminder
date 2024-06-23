package com.fgiannesini;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;

public class Reminder {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public Reminder(InputStream inputStream, OutputStream outputStream) throws IOException {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        outputStream.write(("Reminder" + System.lineSeparator()).getBytes());
    }

    public void run() throws IOException {
        var dictionnary = List.of(new Word("desligar", "éteindre"), new Word("acender", "allumer"));
        Scanner scanner = new Scanner(inputStream);
        for (var word : dictionnary) {
            outputStream.write((word.portugues + System.lineSeparator()).getBytes());
            String s = scanner.nextLine();
            if (s.equals("quit")) {
                outputStream.write(("Bye" + System.lineSeparator()).getBytes());
                return;
            }
            if (word.french.equals(s)) {
                outputStream.write(("OK" + System.lineSeparator()).getBytes());
            } else {
                outputStream.write(("KO (éteindre)" + System.lineSeparator()).getBytes());
            }
        }
    }

    private record Word(String portugues, String french) {
    }
}
