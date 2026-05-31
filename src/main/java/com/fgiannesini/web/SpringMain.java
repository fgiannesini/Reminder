package com.fgiannesini.web;

import com.fgiannesini.Dictionary;
import com.fgiannesini.RecentWordsWindow;
import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.web.storage.DatabaseStorageHandler;
import com.fgiannesini.web.storage.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.SecureRandom;

@SpringBootApplication
public class SpringMain {
    static void main(String[] args) {
        SpringApplication.run(SpringMain.class, args);
    }

    @Configuration
    public static class ReminderConfiguration {

        private static final Logger log = LoggerFactory.getLogger(ReminderConfiguration.class);

        @Bean
        public StorageHandler storageHandler(WordRepository wordRepository) {
            return new DatabaseStorageHandler(wordRepository);
        }

        @Bean
        public OriginalDictionary originalDictionary() {
            var originalFileInputStream = getClass().getClassLoader().getResourceAsStream("dictionary.csv");
            return new OriginalDictionary(originalFileInputStream);
        }

        @Bean
        public Dictionary dictionary(StorageHandler storageHandler, OriginalDictionary originalDictionary) throws IOException {
            var dictionary = new Dictionary(
                    new SecureRandom(),
                    storageHandler,
                    new RecentWordsWindow()
            );
            var originalWords = originalDictionary.load();
            var duplicates = originalDictionary.findDuplicates(originalWords);
            if (!duplicates.isEmpty()) {
                log.warn("Duplicate words detected in dictionary: {}", duplicates);
            }
            dictionary.load(originalWords);
            return dictionary;
        }
    }
}
