package com.fgiannesini.web.storage;

import com.fgiannesini.Word;
import com.fgiannesini.storage.StorageHandler;
import com.fgiannesini.web.ReminderConfigurationForTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = ReminderConfigurationForTest.class)
@Testcontainers
class DatabaseStorageHandlerTest {

    @Container
    public static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");
    @Autowired
    private StorageHandler storageHandler;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
    }

    @Test
    void Should_save_and_load_words() throws IOException {
        var words = List.of(
                new Word("ao inves, em vez de", "au lieu de", 2, null),
                new Word("ou seja", "c'est à dire", 5, LocalDateTime.of(2024, 7, 3, 13, 18, 0)
                ));
        this.storageHandler.save(words);

        var actual = this.storageHandler.load();
        var expected = List.of(
                new Word("au lieu de", "ao inves, em vez de", 3, null),
                new Word("c'est à dire", "ou seja", 3, null),
                new Word("ao inves, em vez de", "au lieu de", 2, null),
                new Word("ou seja", "c'est à dire", 5, LocalDateTime.of(2024, 7, 3, 13, 18, 0))
        );
        Assertions.assertEquals(expected, actual);
    }
}