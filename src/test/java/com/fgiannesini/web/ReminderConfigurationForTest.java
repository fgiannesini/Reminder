package com.fgiannesini.web;

import com.fgiannesini.Dictionary;
import com.fgiannesini.NextGenerator;
import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.storage.StorageHandler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@TestConfiguration
public class ReminderConfigurationForTest {

    @Bean
    public OriginalDictionary originalDictionary() {
        var originalFileInputStream = getClass().getClassLoader().getResourceAsStream("dictionary-for-test.csv");
        return new OriginalDictionary(originalFileInputStream);
    }

    @Bean
    public Dictionary dictionary(StorageHandler storageHandler, OriginalDictionary originalDictionary) throws IOException {
        var dictionary = new Dictionary(new NextGenerator(), storageHandler);
        var originalWords = originalDictionary.load();
        dictionary.load(originalWords);
        return dictionary;
    }
}
