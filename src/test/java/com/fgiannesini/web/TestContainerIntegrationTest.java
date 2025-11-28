package com.fgiannesini.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@ContextConfiguration(classes = ReminderConfigurationForTest.class)
@Testcontainers
public interface TestContainerIntegrationTest {

    @Container
    @ServiceConnection
    PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres");
}
