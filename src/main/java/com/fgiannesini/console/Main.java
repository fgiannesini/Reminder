package com.fgiannesini.console;

import com.fgiannesini.Dictionary;
import com.fgiannesini.Word;
import com.fgiannesini.console.storage.FileStorageHandler;
import com.fgiannesini.original.OriginalDictionary;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        var originalFileInputStream = ClassLoader.getSystemResourceAsStream("dictionary.csv");
        var storageDir = Path.of(System.getProperty("user.home")).resolve("Reminder");
        var storageHandler = new FileStorageHandler(storageDir);
        List<Word> originalWords = new OriginalDictionary(originalFileInputStream).load();
        Dictionary dictionary = new Dictionary(
                new SecureRandom(LocalDateTime.now().toString().getBytes()),
                storageHandler
        );
        dictionary.load(originalWords);
        ReminderConsole reminderConsole = new ReminderConsole(System.in, System.out);
        reminderConsole.run(dictionary);
    }
}