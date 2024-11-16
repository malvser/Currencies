package serhii.malov.currencies.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExchangeRateDtoRq {
    private Boolean success;
    private String terms;
    private String privacy;
    private Long timestamp;
    private String source;
    private Map<String, Double> quotes;
}
