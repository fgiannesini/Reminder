package com.fgiannesini.web;

import com.fgiannesini.Matching;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class ReminderControllerIntegrationTest implements TestContainerIntegrationTest {

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

}

