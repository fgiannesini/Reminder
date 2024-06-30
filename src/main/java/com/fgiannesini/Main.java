package com.fgiannesini;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var path = Paths.get(ClassLoader.getSystemResource("dictionary.csv").toURI());
        var storageHandler = new FileStorageHandler(Path.of(System.getProperty("user.home")).resolve("Reminder"));
        List<Word> words = storageHandler.load(path);
        Dictionary dictionary = new Dictionary(
                new SecureRandom(new Date().toString().getBytes()),
                words
        );
        Reminder reminder = new Reminder(System.in, System.out);
        reminder.run(dictionary);
    }
}