package com.fgiannesini.web;

import com.fgiannesini.Matching;
import com.fgiannesini.RemainingStats;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient;


@AutoConfigureRestTestClient
@Transactional
public class ReminderControllerIntegrationTest implements TestContainerIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    public void Should_get_next_word_to_translate() {
        restTestClient
                .get().uri("/reminder/word/next")
                .exchange()
                .expectStatus().isOk()
                .expectBody(WordDto.class).isEqualTo(new WordDto("ao inves, em vez de"));
    }

    @Test
    public void Should_post_a_valid_translation() {
        restTestClient
                .post().uri("/reminder/word/check")
                .body(new TranslationDto("ou seja", "c'est à dire"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(TranslationResponseDto.class).isEqualTo(new TranslationResponseDto(Matching.MATCHED, "c'est à dire", true));
    }

    @Test
    public void Should_get_remaining_stats() {
        restTestClient
                .get().uri("/reminder/word/remaining")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RemainingStats.class).isEqualTo(new RemainingStats(4, 4));
    }
}

