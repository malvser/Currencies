package serhii.malov.currencies.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import serhii.malov.currencies.model.Currency;
import serhii.malov.currencies.model.dto.CurrencyDtoRq;
import serhii.malov.currencies.model.dto.CurrencyDtoRs;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    CurrencyDtoRs currencyToCurrencyDtoRs(Currency currency);

    List<CurrencyDtoRs> currencyListToCurrencyDtoRsList(List<Currency> currencyList);

    Currency currencyDtoRqToCurrency(CurrencyDtoRq currencyDtoRq);
}
