package serhii.malov.currencies.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import serhii.malov.currencies.model.dto.CurrencyDtoRq;
import serhii.malov.currencies.model.dto.CurrencyDtoRs;
import serhii.malov.currencies.service.CurrencyService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15.3")
                    .withDatabaseName("test_db")
                    .withUsername("test_user")
                    .withPassword("test_password")
                    .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        currencyService.addCurrency(new CurrencyDtoRq("USD", "United States Dollar"));
        currencyService.fetchExchangeRates();
    }

    @Test
    void testPostgresContainerStarts() {
        assertTrue(postgresContainer.isRunning(), "Postgres container should be running");
    }

    @Test
    void testGetCurrencies() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/currencies", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> currencies = response.getBody();
        assertEquals(2, currencies.size());
    }

    @Test
    void testGetExchangeRate() {
        ResponseEntity<List> response = restTemplate.getForEntity("/api/currencies/USD/exchange-rate", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<?> exchangeRates = response.getBody();
        assertEquals(6, exchangeRates.size()); // from getRandomExchangeRateDtoRs
//        assertEquals(169, exchangeRates.size()); // from getExchangeRateFromExternalAPI
    }

    @Test
    void testAddCurrency() {
        CurrencyDtoRq newCurrency = new CurrencyDtoRq("EUR", "Euro");

        ResponseEntity<CurrencyDtoRs> response = restTemplate.postForEntity(
                "/api/currencies", newCurrency, CurrencyDtoRs.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        CurrencyDtoRs createdCurrency = response.getBody();
        assertEquals("EUR", createdCurrency.getCode());
        assertEquals("Euro", createdCurrency.getName());
    }
}