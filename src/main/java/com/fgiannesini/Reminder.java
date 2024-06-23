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
        outputStream.write(("Reminder" + System.lineSeparator()).getBytes());
    }

    public void run() throws IOException {
        outputStream.write(("desligar" + System.lineSeparator()).getBytes());
        String s = new Scanner(inputStream).nextLine();
        if (s.equals("éteindre")) {
            outputStream.write(("OK" + System.lineSeparator()).getBytes());
        } else {
            outputStream.write(("KO (éteindre)" + System.lineSeparator()).getBytes());
        }
    }
}
