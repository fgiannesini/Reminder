package com.fgiannesini.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = ReminderConfigurationForTest.class)
@Testcontainers
public abstract class TestContainerIntegrationTest {


    static final PostgreSQLContainer<?> postgresDB;

    static {
        postgresDB = new PostgreSQLContainer<>("postgres")
                .withDatabaseName("testdb")
                .withUsername("user")
                .withPassword("password");
        postgresDB.start();
    }
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDB::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDB::getUsername);
        registry.add("spring.datasource.password", postgresDB::getPassword);
    }


}
