package com.fgiannesini;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        OutputStream out = System.out;
        out.write(("Reminder" + System.lineSeparator()).getBytes());
        InputStream in = System.in;
        Scanner scanner = new Scanner(in);
        var line  = scanner.nextLine();
        out.write(line.getBytes());
    }
}