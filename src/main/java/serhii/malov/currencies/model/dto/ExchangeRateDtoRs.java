package serhii.malov.currencies.model.dto;

public class ExchangeRateDtoRs {

    private String currencyPair;
    private double rate;

    public ExchangeRateDtoRs(String currencyPair, double rate) {
        this.currencyPair = currencyPair;
        this.rate = rate;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
