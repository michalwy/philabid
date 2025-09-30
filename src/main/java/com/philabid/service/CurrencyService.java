package com.philabid.service;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Collection;
import java.util.stream.Stream;

public class CurrencyService {
    public Collection<CurrencyUnit> getCurrencies() {
        return Stream.of("PLN", "EUR", "USD").map(Monetary::getCurrency).toList();
    }
}
