package com.fgiannesini.console;

import com.fgiannesini.Dictionary;
import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.storage.FileStorageHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        var originalFileInputStream = ClassLoader.getSystemResourceAsStream("dictionary.csv");
        var storageDir = Path.of(System.getProperty("user.home")).resolve("Reminder");
        var storageHandler = new FileStorageHandler(storageDir);
        Dictionary dictionary = new Dictionary(
                new SecureRandom(LocalDateTime.now().toString().getBytes()),
                storageHandler,
                new OriginalDictionary(originalFileInputStream)
        );
        ReminderConsole reminderConsole = new ReminderConsole(System.in, System.out);
        reminderConsole.run(dictionary);
    }
}