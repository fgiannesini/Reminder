package com.fgiannesini.web;

import com.fgiannesini.Dictionary;
import com.fgiannesini.Matching;
import com.fgiannesini.NextGenerator;
import com.fgiannesini.original.OriginalDictionary;
import com.fgiannesini.storage.StorageHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
public class ReminderControllerIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void Should_get_next_word_to_translate() {
        var response = restTemplate.getForEntity("http://localhost:%d/reminder/word/next".formatted(port), WordDto.class);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new WordDto("ao inves, em vez de"), response.getBody());
    }

    @Test
    public void Should_post_a_valid_translation() {
        var response = restTemplate.postForEntity("http://localhost:%d/reminder/word/check".formatted(port), new TranslationDto("ou seja", "c'est à dire"), TranslationResponseDto.class);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new TranslationResponseDto(Matching.MATCHED, "c'est à dire", true), response.getBody());
    }

    @TestConfiguration
    public static class ReminderConfigurationForTest {
        @Bean
        public Dictionary dictionary(StorageHandler storageHandler) throws IOException {
            var dictionary = new Dictionary(new NextGenerator(), storageHandler);
            var originalFileInputStream = getClass().getClassLoader().getResourceAsStream("dictionary-for-test.csv");
            var originalWords = new OriginalDictionary(originalFileInputStream).load();
            dictionary.load(originalWords);
            return dictionary;
        }
    }
}
