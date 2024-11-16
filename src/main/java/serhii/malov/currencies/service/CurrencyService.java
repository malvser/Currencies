package serhii.malov.currencies.service;


import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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

import java.util.*;
import java.util.concurrent.*;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private final CurrencyRepository currencyRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final RestAdapter restAdapter;
    private final CurrencyMapper currencyMapper;
    private final ExchangeRateMapper exchangeRateMapper;

    static final List<String> SUPPORTED_CURRENCIES = Arrays.asList(
            "AED", "AFN", "ALL", "AMD", "ANG", "BAM"
    );

    private final Map<String, List<ExchangeRateDtoRs>> exchangeRatesCache = new ConcurrentHashMap<>();

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository,
                           ExchangeRateRepository exchangeRateRepository,
                           RestAdapter restAdapter,
                           CurrencyMapper currencyMapper,
                           ExchangeRateMapper exchangeRateMapper
    ) {
        this.currencyRepository = currencyRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.restAdapter = restAdapter;
        this.currencyMapper = currencyMapper;
        this.exchangeRateMapper = exchangeRateMapper;
    }

    /**
     * Initializes the exchange rates cache by fetching all exchange rates from the repository
     * and caching them in memory for faster access.
     */
    @PostConstruct
    public void init() {
        logger.info("Initializing exchange rates cache...");

        List<ExchangeRate> exchangeRateList = exchangeRateRepository.findAll();
        logger.info("Exchange rates found in database: = {}", exchangeRateList.size());

        exchangeRateList.forEach(exchangeRate -> {
            String currencyCode = exchangeRate.getCurrency().getCode();
            exchangeRatesCache.computeIfAbsent(currencyCode, k -> new ArrayList<>())
                    .add(exchangeRateMapper.exchangeRateToExchangeRateDtoRs(exchangeRate));
        });

        logger.info("Exchange rates cache initialized. Cache size: {}", exchangeRatesCache.size());
    }

    /**
     * Retrieves all currencies from the repository and maps them to DTOs.
     *
     * @return List of CurrencyDtoRs
     */
    public List<CurrencyDtoRs> getCurrencies() {
        logger.info("Fetching all currencies...");

        List<Currency> currencies = currencyRepository.findAll();
        logger.info("Fetched currencies: {}", currencies.size());

        return currencyMapper.currencyListToCurrencyDtoRsList(currencies);
    }

    /**
     * Retrieves the exchange rates for a given currency code from the cache.
     *
     * @param currencyCode The currency code to fetch exchange rates for.
     * @return List of ExchangeRateDtoRs
     */
    public List<ExchangeRateDtoRs> getExchangeRate(String currencyCode) {
        logger.info("Fetching exchange rates for currency code: {}", currencyCode);

        List<ExchangeRateDtoRs> exchangeRates = exchangeRatesCache.get(currencyCode) == null
                ? Collections.emptyList() : exchangeRatesCache.get(currencyCode);
        if (exchangeRates.isEmpty()) {
            logger.warn("No exchange rates found in cache for currency code: {}", currencyCode);
        }
        return exchangeRates;
    }

    /**
     * Adds a new currency to the repository and returns the added currency as a DTO.
     *
     * @param currencyDtoRq The currency DTO to add.
     * @return CurrencyDtoRs
     */
    @Transactional
    public CurrencyDtoRs addCurrency(CurrencyDtoRq currencyDtoRq) {
        logger.info("Adding new currency with code: {} and name: {}",
                currencyDtoRq.getCode(), currencyDtoRq.getName());

        Currency savedCurrency = currencyRepository.save(currencyMapper.currencyDtoRqToCurrency(currencyDtoRq));
        return currencyMapper.currencyToCurrencyDtoRs(savedCurrency);
    }

    /**
     * Fetches exchange rates from an external source and updates the exchange rates in the repository and cache.
     * This method is scheduled to run periodically.
     */
//    @Scheduled(fixedRate = 3600000)  // 1 hour
    @Scheduled(fixedRate = 60000)  // 1 minute
    @Transactional
    public void fetchExchangeRates() {
        logger.info("Fetching exchange rates for all currencies...");

        List<Currency> currencyList = currencyRepository.findAll();
        logger.info("Currencies found: {}", currencyList.size());

        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        currencyList.forEach(currency -> {
//            List<ExchangeRateDtoRs> exchangeRateDtoRsList = getExchangeRateFromExternalAPI(currency.getCode());
            List<ExchangeRateDtoRs> exchangeRateDtoRsList = getRandomExchangeRateDtoRs(currency.getCode());
            exchangeRateDtoRsList.forEach(exchangeRateDtoRs -> {
                ExchangeRate exchangeRate = new ExchangeRate(
                        exchangeRateDtoRs.getCurrencyPair(),
                        exchangeRateDtoRs.getRate(),
                        currency
                );
                exchangeRateList.add(exchangeRate);
            });
            exchangeRatesCache.computeIfAbsent(currency.getCode(), k -> new ArrayList<>()).addAll(exchangeRateDtoRsList);

        });
        if (!exchangeRateList.isEmpty()) {
            exchangeRateRepository.saveAll(exchangeRateList);
            logger.info("Exchange rates saved to repository: {}", exchangeRateList.size());
        } else {
            logger.warn("No exchange rates to save for currencies.");
        }
    }

    /**
     * Fetching exchange rates from an external API for a given currency code.
     *
     * @param currencyCode The currency code to fetch exchange rates for.
     * @return List of ExchangeRateDtoRs
     */
    List<ExchangeRateDtoRs> getExchangeRateFromExternalAPI(String currencyCode) {
        Map<String, Double> result = restAdapter.getExchangeRateMap(currencyCode);
        return convertMapToExchangeRateList(result != null ? result : Collections.emptyMap());
    }

    /**
     * Simulates fetching exchange rates from an external API for a given currency code.
     *
     * @param currencyCode The currency code to fetch exchange rates for.
     * @return List of ExchangeRateDtoRs
     */
    List<ExchangeRateDtoRs> getRandomExchangeRateDtoRs(String currencyCode) {
        logger.debug("Generating random exchange rates for currency code: {}", currencyCode);

        double min = 0.1;
        double max = 85.0;
        List<ExchangeRateDtoRs> exchangeRateDtoRsList = new ArrayList<>();

        getCurrencyPairList(currencyCode).forEach(supportedCurrency -> {
            double rate = min + (Math.random() * (max - min));
            double roundedRate = Math.round(rate * 100.0) / 100.0;  // Round to 2 decimal places
            exchangeRateDtoRsList.add(new ExchangeRateDtoRs(supportedCurrency, roundedRate));
        });

        logger.debug("Generated random exchange rates for {} pairs", exchangeRateDtoRsList.size());
        return exchangeRateDtoRsList;
    }

    /**
     * Retrieves a list of supported currency pairs for a given currency code.
     *
     * @param currencyCode The base currency code.
     * @return List of currency pairs
     */
     List<String> getCurrencyPairList(String currencyCode) {
        logger.debug("Generating currency pairs for base currency: {}", currencyCode);

        return SUPPORTED_CURRENCIES.stream()
                .map(supportedCurrency -> currencyCode + supportedCurrency)
                .toList();
    }

    /**
     * Converts a map of exchange rate values into a list of ExchangeRateDtoRs.
     *
     * @param rateMap A map of currency pairs and exchange rates.
     * @return List of ExchangeRateDtoRs
     */
     List<ExchangeRateDtoRs> convertMapToExchangeRateList(Map<String, Double> rateMap) {
        logger.debug("Converting map of exchange rates into DTOs. Size: {}", rateMap.size());

        return rateMap.entrySet().stream()
                .map(entry -> new ExchangeRateDtoRs(entry.getKey(), entry.getValue()))
                .toList();
    }
}
