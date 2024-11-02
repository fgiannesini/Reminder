package com.fgiannesini.web;

import com.fgiannesini.Dictionary;
import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.web.storage.DatabaseStorageHandler;
import com.fgiannesini.web.storage.WordRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
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
        public StorageHandler storageHandler(WordRepository wordRepository) {
            return new DatabaseStorageHandler(wordRepository);
        }

        @Bean
        public Dictionary dictionary(StorageHandler storageHandler) throws IOException {
            var dictionary = new Dictionary(
                    new SecureRandom(LocalDateTime.now().toString().getBytes()),
                    storageHandler
            );
            var originalFileInputStream = getClass().getClassLoader().getResourceAsStream("dictionary.csv");
            var originalWords = new OriginalDictionary(originalFileInputStream).load();
            dictionary.load(originalWords);
            return dictionary;
        }
    }
}
