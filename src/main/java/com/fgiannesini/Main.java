package com.fgiannesini;

import com.fgiannesini.storage.FileStorageHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        var path = Paths.get(ClassLoader.getSystemResource("dictionary.csv").toURI());
        var storageDir = Path.of(System.getProperty("user.home")).resolve("Reminder");
        var storageHandler = new FileStorageHandler(storageDir, path);
        Dictionary dictionary = new Dictionary(
                new SecureRandom(LocalDateTime.now().toString().getBytes()),
                storageHandler
        );
        Reminder reminder = new Reminder(System.in, System.out);
        reminder.run(dictionary);
    }
}