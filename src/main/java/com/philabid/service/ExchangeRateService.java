package com.philabid.service;

import com.philabid.database.ExchangeRateRepository;
import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import javax.money.convert.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ExchangeRateService {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);

    private final Map<ExchangeRateCacheKey, ExchangeRate> inMemoryCache = new ConcurrentHashMap<>();
    private final ExchangeRateRepository repository;

    public ExchangeRateService(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    private static LocalDate adjustWeekendToPreviousBusinessDay(LocalDate d) {
        if (d.getDayOfWeek() == DayOfWeek.SATURDAY) return d.minusDays(1);
        if (d.getDayOfWeek() == DayOfWeek.SUNDAY) return d.minusDays(2);
        return d;
    }

    public Optional<ExchangeRate> getExchangeRate(LocalDate date, CurrencyUnit from, CurrencyUnit to) {
        Optional<ExchangeRate> localRate = getCachedExchangeRate(date, from, to);
        if (localRate.isPresent()) {
            return localRate;
        }
        localRate = getLocalExchangeRate(date, from, to);
        if (localRate.isPresent()) {
            cacheExchangeRate(date, from, to, localRate.get());
            return localRate;
        }

        for (int i = 0; i < 10; i++) {
            LocalDate rateDate = date.minusDays(i);
            Optional<ExchangeRate> rate = getRemoteExchangeRate(rateDate, from, to);
            if (rate.isPresent()) {
                ExchangeRate exchangeRate = rate.get();
                if (i != 0) {
                    exchangeRate = storeLocalExchangeRate(date, from, to,
                            exchangeRate.getFactor().numberValue(BigDecimal.class));
                }
                return Optional.of(exchangeRate);
            }
        }
        logger.error("Cannot evaluate currency conversion rate found for date: {}, from: {}, to: {}", date, from, to);
        return Optional.empty();
    }

    private Optional<ExchangeRate> getRemoteExchangeRate(LocalDate date, CurrencyUnit from, CurrencyUnit to) {
        try {
            String providerName = "IMF-HIST";
            if (date.equals(LocalDate.now())) {
                providerName = "IMF";
            }

            ConversionQuery conversionQuery = ConversionQueryBuilder.of()
                    .setBaseCurrency(from)
                    .setTermCurrency(to)
                    .setProviderName(providerName)
                    .set(date)
                    .build();
            ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider(conversionQuery);

            BigDecimal factor =
                    provider.getExchangeRate(conversionQuery).getFactor().numberValue(BigDecimal.class)
                            .setScale(4, RoundingMode.HALF_UP);

            com.philabid.model.ExchangeRate newExchangeRate = new com.philabid.model.ExchangeRate();
            newExchangeRate.setDate(date);
            newExchangeRate.setSourceCurrency(from);
            newExchangeRate.setTargetCurrency(to);
            newExchangeRate.setRate(factor);
            repository.saveRate(newExchangeRate);

            return getLocalExchangeRate(date, from, to);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<ExchangeRate> getLocalExchangeRate(LocalDate date, CurrencyUnit from, CurrencyUnit to) {
        try {
            Optional<com.philabid.model.ExchangeRate> localRate = repository.findRate(date, from, to);
            return localRate.map(exchangeRate -> buildExchangeRate(from, to, exchangeRate.getRate()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    private ExchangeRate storeLocalExchangeRate(LocalDate date, CurrencyUnit from, CurrencyUnit to, BigDecimal factor) {
        try {
            com.philabid.model.ExchangeRate newExchangeRate = new com.philabid.model.ExchangeRate();
            newExchangeRate.setDate(date);
            newExchangeRate.setSourceCurrency(from);
            newExchangeRate.setTargetCurrency(to);
            newExchangeRate.setRate(factor);
            repository.saveRate(newExchangeRate);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        ExchangeRate exchangeRate = buildExchangeRate(from, to, factor);
        cacheExchangeRate(date, from, to, exchangeRate);
        return exchangeRate;
    }

    private Optional<ExchangeRate> getCachedExchangeRate(LocalDate date, CurrencyUnit from, CurrencyUnit to) {
        ExchangeRateCacheKey key = new ExchangeRateCacheKey(date, from, to);
        if (inMemoryCache.containsKey(key)) {
            return Optional.of(inMemoryCache.get(key));
        }
        return Optional.empty();
    }

    private void cacheExchangeRate(LocalDate date, CurrencyUnit from, CurrencyUnit to, ExchangeRate rate) {
        ExchangeRateCacheKey key = new ExchangeRateCacheKey(date, from, to);
        inMemoryCache.put(key, rate);
    }

    private ExchangeRate buildExchangeRate(CurrencyUnit from, CurrencyUnit to, BigDecimal factor) {
        return new ExchangeRateBuilder(ConversionContext.DEFERRED_CONVERSION)
                .setBase(from)
                .setTerm(to)
                .setFactor(DefaultNumberValue.of(factor))
                .build();
    }
}