package serhii.malov.currencies.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import serhii.malov.currencies.model.dto.ExchangeRateDtoRq;

import java.util.Collections;
import java.util.Map;


@Component
public class RestAdapter {

    private static final Logger logger = LoggerFactory.getLogger(RestAdapter.class);

    private final RestTemplate restTemplate;

    @Value("${routes.endpoint.currencylayer.exchange-rate}")
    private String urlExchangeRate;

    @Value("${clients.currencylayer.accessKey}")
    private String accessKey;

    public RestAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> getExchangeRateMap(String currencyCode)  {
        String url = UriComponentsBuilder.fromHttpUrl(urlExchangeRate)
                .queryParam("access_key", accessKey)
                .queryParam("source", currencyCode)
                .toUriString();
        HttpEntity<Object> entity = new HttpEntity<>(createHeaders());
        ExchangeRateDtoRq body = restTemplate.exchange(url, HttpMethod.GET, entity, ExchangeRateDtoRq.class).getBody();
        logger.info("call getExchangeRateMap: body for {} sucess == {}", currencyCode, body.getSuccess());
        return body.getQuotes();
    }

    public HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
