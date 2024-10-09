package com.fgiannesini.web;

import com.fgiannesini.*;
import com.fgiannesini.storage.StorageHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
public class ReminderControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void Should_get_next_word_to_translate() {
        var response = restTemplate.getForEntity("http://localhost:%d/reminder/word/next".formatted(port), WordDto.class);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new WordDto("acender"), response.getBody());
    }

    @Test
    public void Should_post_a_valid_translation() {
        var response = restTemplate.postForEntity("http://localhost:%d/reminder/word/check".formatted(port), new TranslationDto("desligar", "éteindre"), TranslationResponseDto.class);
        assertEquals(OK, response.getStatusCode());
        assertEquals(new TranslationResponseDto(Matching.MATCHED, "éteindre"), response.getBody());
    }

    @TestConfiguration
    public static class ReminderConfigurationForTest {
        @Bean
        public Dictionary dictionary() throws IOException {
            StorageHandler storageHandler = new MemoryStorageHandler(new Word("desligar", "éteindre"), new Word("acender", "allumer"));
            return new Dictionary(new NextGenerator(), storageHandler);
        }
    }
}
