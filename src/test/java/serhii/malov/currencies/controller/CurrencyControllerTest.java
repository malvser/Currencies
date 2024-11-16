package serhii.malov.currencies.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import serhii.malov.currencies.model.dto.CurrencyDtoRq;
import serhii.malov.currencies.model.dto.CurrencyDtoRs;
import serhii.malov.currencies.model.dto.ExchangeRateDtoRs;
import serhii.malov.currencies.service.CurrencyService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void testGetCurrencies() throws Exception {
        // Set up mock data
        List<CurrencyDtoRs> mockCurrencies = List.of(new CurrencyDtoRs("USD", "United States Dollar"));
        when(currencyService.getCurrencies()).thenReturn(mockCurrencies);

        // Act & Assert
        mockMvc.perform(get("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].code").value("USD"),
                        jsonPath("$[0].name").value("United States Dollar")
                );

        verify(currencyService, times(1)).getCurrencies();
    }

    @Test
    void testGetExchangeRate() throws Exception {
        //  Set up mock data
        String currencyCode = "USD";
        List<ExchangeRateDtoRs> mockExchangeRates = List.of(new ExchangeRateDtoRs("USD/EUR", 1.2));
        when(currencyService.getExchangeRate(currencyCode)).thenReturn(mockExchangeRates);

        // Act & Assert
        mockMvc.perform(get("/api/currencies/{code}/exchange-rate", currencyCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(status().isOk(),
                        jsonPath("$[0].currencyPair").value("USD/EUR"),
                        jsonPath("$[0].rate").value(1.2)
                );

        verify(currencyService, times(1)).getExchangeRate(currencyCode);
    }

    @Test
    void testAddCurrency() throws Exception {
        // Set up mock data
        CurrencyDtoRs mockResponse = new CurrencyDtoRs("USD", "United States Dollar");
        when(currencyService.addCurrency(any(CurrencyDtoRq.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "code": "USD",
                                    "name": "United States Dollar"
                                }
                                """))
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.code").value("USD"),
                        jsonPath("$.name").value("United States Dollar")
                );

        verify(currencyService, times(1)).addCurrency(any(CurrencyDtoRq.class));
    }
}
