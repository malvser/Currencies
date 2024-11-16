package serhii.malov.currencies.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import serhii.malov.currencies.adapter.RestAdapter;
import serhii.malov.currencies.model.Currency;
import serhii.malov.currencies.model.ExchangeRate;
import serhii.malov.currencies.model.dto.CurrencyDtoRq;
import serhii.malov.currencies.model.dto.CurrencyDtoRs;
import serhii.malov.currencies.model.dto.ExchangeRateDtoRs;
import serhii.malov.currencies.model.dto.mapper.CurrencyMapper;
import serhii.malov.currencies.model.dto.mapper.ExchangeRateMapper;
import serhii.malov.currencies.repository.CurrencyRepository;
import serhii.malov.currencies.repository.ExchangeRateRepository;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private RestAdapter restAdapter;

    @Mock
    private CurrencyMapper currencyMapper;

    @Mock
    private ExchangeRateMapper exchangeRateMapper;

    @Spy
    @InjectMocks
    private CurrencyService currencyService;

    @Test
    void init_shouldInitializeExchangeRatesCache() {
        ExchangeRate mockExchangeRate = new ExchangeRate();
        mockExchangeRate.setCurrency(new Currency("USD", "United States Dollar"));

        when(exchangeRateRepository.findAll()).thenReturn(List.of(mockExchangeRate));
        when(exchangeRateMapper.exchangeRateToExchangeRateDtoRs(mockExchangeRate))
                .thenReturn(new ExchangeRateDtoRs("USD/EUR", 1.1));

        currencyService.init();

        List<ExchangeRateDtoRs> cachedRates = currencyService.getExchangeRate("USD");
        assertFalse(cachedRates.isEmpty());
        assertEquals("USD/EUR", cachedRates.get(0).getCurrencyPair());
    }

    @Test
    void getCurrencies_shouldReturnListOfCurrencyDtoRs() {
        Currency currency = new Currency("USD", "US Dollar");
        List<Currency> currencies = List.of(currency);
        List<CurrencyDtoRs> currencyDtoRsList = List.of(new CurrencyDtoRs("USD", "US Dollar"));

        when(currencyRepository.findAll()).thenReturn(currencies);
        when(currencyMapper.currencyListToCurrencyDtoRsList(currencies)).thenReturn(currencyDtoRsList);

        List<CurrencyDtoRs> result = currencyService.getCurrencies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USD", result.get(0).getCode());
        verify(currencyRepository, times(1)).findAll();
        verify(currencyMapper, times(1)).currencyListToCurrencyDtoRsList(currencies);
    }

    @Test
    void addCurrency_shouldSaveCurrencyAndReturnDto() {
        CurrencyDtoRq currencyDtoRq = new CurrencyDtoRq("GBP", "British Pound");
        Currency savedCurrency = new Currency("GBP", "British Pound");
        CurrencyDtoRs currencyDtoRs = new CurrencyDtoRs("GBP", "British Pound");

        when(currencyMapper.currencyDtoRqToCurrency(currencyDtoRq)).thenReturn(savedCurrency);
        when(currencyRepository.save(savedCurrency)).thenReturn(savedCurrency);
        when(currencyMapper.currencyToCurrencyDtoRs(savedCurrency)).thenReturn(currencyDtoRs);

        CurrencyDtoRs result = currencyService.addCurrency(currencyDtoRq);

        assertNotNull(result);
        assertEquals("GBP", result.getCode());
        verify(currencyRepository, times(1)).save(savedCurrency);
        verify(currencyMapper, times(1)).currencyDtoRqToCurrency(currencyDtoRq);
    }

    @Test
    void getExchangeRate_shouldReturnExchangeRatesForGivenCurrencyCode() {
        Map<String, List<ExchangeRateDtoRs>> mockedCache = Mockito.mock(Map.class);
        ReflectionTestUtils.setField(currencyService, "exchangeRatesCache", mockedCache);

        ExchangeRateDtoRs mockRate = new ExchangeRateDtoRs("USD/EUR", 1.1);
        when(mockedCache.get("USD")).thenReturn(List.of(mockRate));

        // Act
        List<ExchangeRateDtoRs> exchangeRates = currencyService.getExchangeRate("USD");

        // Assert
        assertEquals(1, exchangeRates.size());
        assertEquals("USD/EUR", exchangeRates.get(0).getCurrencyPair());
        assertEquals(1.1, exchangeRates.get(0).getRate());
    }

    @Test
    void fetchExchangeRates_shouldUpdateRepositoryAndCache() {
        // Arrange
        Currency mockCurrency = new Currency("USD", "United States Dollar");
        when(currencyRepository.findAll()).thenReturn(List.of(mockCurrency));

        // Мокаємо метод getRandomExchangeRateDtoRs
        ExchangeRateDtoRs mockRate = new ExchangeRateDtoRs("USD/EUR", 1.1);
        doReturn(List.of(mockRate)).when(currencyService).getRandomExchangeRateDtoRs("USD");

        // Act
        currencyService.fetchExchangeRates();

        // Assert
        verify(exchangeRateRepository, times(1)).saveAll(anyList());
        List<ExchangeRateDtoRs> cachedRates = currencyService.getExchangeRate("USD");
        assertEquals(1, cachedRates.size());
        assertEquals("USD/EUR", cachedRates.get(0).getCurrencyPair());
    }

    @Test
    void getExchangeRateFromExternalAPI_shouldReturnExchangeRateDtoList() {
        Map<String, Double> mockRateMap = Map.of("USD/EUR", 1.1, "USD/GBP", 0.8);
        when(restAdapter.getExchangeRateMap("USD")).thenReturn(mockRateMap);

        List<ExchangeRateDtoRs> exchangeRateDtoList = currencyService.getExchangeRateFromExternalAPI("USD");

        assertEquals(2, exchangeRateDtoList.size());
        assertTrue(exchangeRateDtoList.stream().anyMatch(dto -> dto.getCurrencyPair().equals("USD/EUR")));
    }

    @Test
    void getCurrencyPairList_shouldReturnListOfCurrencyPairs() {
        // Arrange
        String baseCurrency = "USD";

        // Act
        List<String> currencyPairs = currencyService.getCurrencyPairList(baseCurrency);

        // Assert
        assertNotNull(currencyPairs);
        assertTrue(currencyPairs.contains("USDAED"));
        assertTrue(currencyPairs.contains("USDBAM"));
        assertEquals(CurrencyService.SUPPORTED_CURRENCIES.size(), currencyPairs.size());
    }

    @Test
    void convertMapToExchangeRateList_shouldConvertMapToDtoList() {
        Map<String, Double> rateMap = Map.of(
                "USDEUR", 1.1,
                "USDGBP", 0.8
        );

        List<ExchangeRateDtoRs> exchangeRateDtos = currencyService.convertMapToExchangeRateList(rateMap);

        assertNotNull(exchangeRateDtos);
        assertTrue(
                exchangeRateDtos.stream().anyMatch(dto -> dto.getRate() == 1.1 && "USDEUR".equals(dto.getCurrencyPair())),
                "Expected rate 1.1 for USDEUR not found"
        );
        assertTrue(
                exchangeRateDtos.stream().anyMatch(dto -> dto.getRate() == 0.8 && "USDGBP".equals(dto.getCurrencyPair())),
                "Expected rate 0.8 for USDGBP not found"
        );
    }
}