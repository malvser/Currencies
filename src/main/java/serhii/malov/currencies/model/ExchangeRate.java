package serhii.malov.currencies.model;

import jakarta.persistence.*;

@Entity
public class ExchangeRate {
    @Id
    private String currencyPair;
    private double rate;
    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    public ExchangeRate() {}

    public ExchangeRate(String currencyPair, double rate, Currency currency) {
        this.currencyPair = currencyPair;
        this.rate = rate;
        this.currency = currency;
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

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "currencyPair='" + currencyPair + '\'' +
                ", rate=" + rate +
                '}';
    }
}
