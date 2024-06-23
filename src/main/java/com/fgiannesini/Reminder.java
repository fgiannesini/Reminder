package com.fgiannesini;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Reminder {
    public Reminder(InputStream inputStream, OutputStream outputStream) throws IOException {
        outputStream.write(("Reminder" + System.lineSeparator()).getBytes());
        outputStream.write(("desligar" + System.lineSeparator()).getBytes());
        String s = new Scanner(inputStream).nextLine();
        if (s.equals("éteindre")) {
            outputStream.write(("OK" + System.lineSeparator()).getBytes());
        } else {
            outputStream.write(("KO (éteindre)" + System.lineSeparator()).getBytes());
        }
    }
}
