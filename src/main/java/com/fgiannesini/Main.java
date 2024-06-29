package com.fgiannesini;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var path = Paths.get(ClassLoader.getSystemResource("dictionary.csv").toURI());
        Dictionary dictionary = Dictionary.from(
                new SecureRandom(new Date().toString().getBytes()),
                path
        );
        Reminder reminder = new Reminder(System.in, System.out);
        reminder.run(dictionary);
    }
}