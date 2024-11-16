package serhii.malov.currencies.model.dto.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import serhii.malov.currencies.model.Currency;
import serhii.malov.currencies.model.ExchangeRate;
import serhii.malov.currencies.model.dto.CurrencyDtoRs;
import serhii.malov.currencies.model.dto.ExchangeRateDtoRs;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ExchangeRateMapperTest {

    private final ExchangeRateMapper mapper = Mappers.getMapper(ExchangeRateMapper.class);

    @Test
    void shouldMapExchangeRateToExchangeRateDtoRs() {
        Currency currency = new Currency("USD", "United States Dollar");
        ExchangeRate exchangeRate = new ExchangeRate("USD/UAH", 41.5, currency);
        ExchangeRateDtoRs exchangeRateDtoRs = mapper.exchangeRateToExchangeRateDtoRs(exchangeRate);

        assertEquals("USD/UAH", exchangeRateDtoRs.getCurrencyPair());
        assertEquals(41.5, exchangeRateDtoRs.getRate());
    }
}