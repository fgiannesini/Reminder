package com.fgiannesini;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
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
        for (Iterator<Word> it = dictionary.iterator(); it.hasNext(); ) {
            var word = it.next();
            write(this.outputStream, word.portugues());
            String s = scanner.nextLine();
            if (s.equals("quit")) {
                write(this.outputStream, "Bye");
                return;
            }
            if (word.isFrench(s)) {
                write(this.outputStream, "OK");
            } else {
                write(this.outputStream, "KO (éteindre)");
            }
        }
    }

}
