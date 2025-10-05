package com.philabid.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeRate {
    private Long id;
    private LocalDate date;
    private CurrencyUnit sourceCurrency;
    private CurrencyUnit targetCurrency;
    private BigDecimal rate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public CurrencyUnit getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(CurrencyUnit sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrencyCode) {
        this.sourceCurrency = Monetary.getCurrency(sourceCurrencyCode);
    }

    public CurrencyUnit getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrencyUnit targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public void setTargetCurrency(String targetCurrencyCode) {
        this.targetCurrency = Monetary.getCurrency(targetCurrencyCode);
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}