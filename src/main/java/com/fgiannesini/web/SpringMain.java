package com.fgiannesini.web;

import com.fgiannesini.Dictionary;
import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.storage.FileStorageHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@SpringBootApplication
public class SpringMain {
    public static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }

    @Configuration
    public static class ReminderConfiguration {

        @Bean
        public Dictionary dictionary() throws IOException {
            var originalFileInputStream = getClass().getClassLoader().getResourceAsStream("dictionary.csv");
            var storageDir = Path.of(System.getProperty("user.home")).resolve("Reminder");
            var storageHandler = new FileStorageHandler(storageDir);
            return new Dictionary(
                    new SecureRandom(LocalDateTime.now().toString().getBytes()),
                    storageHandler,
                    new OriginalDictionary(originalFileInputStream)
            );
        }
    }
}
