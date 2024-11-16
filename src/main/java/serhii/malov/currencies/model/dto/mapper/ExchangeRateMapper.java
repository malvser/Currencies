package serhii.malov.currencies.model.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import serhii.malov.currencies.model.ExchangeRate;
import serhii.malov.currencies.model.dto.ExchangeRateDtoRs;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {

    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    ExchangeRateDtoRs exchangeRateToExchangeRateDtoRs(ExchangeRate exchangeRate);
}
