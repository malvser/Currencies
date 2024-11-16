package serhii.malov.currencies.model.dto.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import serhii.malov.currencies.model.Currency;
import serhii.malov.currencies.model.dto.CurrencyDtoRq;
import serhii.malov.currencies.model.dto.CurrencyDtoRs;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyMapperTest {

    private final CurrencyMapper mapper = Mappers.getMapper(CurrencyMapper.class);


    @Test
    void shouldMapCurrencyListToDtoRs() {
        Currency currency = new Currency("USD", "United States Dollar");
        List<CurrencyDtoRs> currencyDtoRsList = mapper.currencyListToCurrencyDtoRsList(List.of(currency));

        assertEquals("USD", currencyDtoRsList.get(0).getCode());
        assertEquals("United States Dollar", currencyDtoRsList.get(0).getName());
    }

    @Test
    void shouldMapCurrencyToDtoRs() {
        Currency currency = new Currency("USD", "United States Dollar");
        CurrencyDtoRs currencyDtoRs = mapper.currencyToCurrencyDtoRs(currency);

        assertEquals("USD", currencyDtoRs.getCode());
        assertEquals("United States Dollar", currencyDtoRs.getName());
    }

    @Test
    void shouldMapDtoRqToCurrency() {
        CurrencyDtoRq currencyDtoRq = new CurrencyDtoRq("USD", "United States Dollar");
        Currency currency = mapper.currencyDtoRqToCurrency(currencyDtoRq);

        assertEquals("USD", currency.getCode());
        assertEquals("United States Dollar", currency.getName());
    }
}