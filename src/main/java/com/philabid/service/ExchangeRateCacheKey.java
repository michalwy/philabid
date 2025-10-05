package com.philabid.service;

import javax.money.CurrencyUnit;
import java.time.LocalDate;

public record ExchangeRateCacheKey(LocalDate date, CurrencyUnit source, CurrencyUnit target) {
}