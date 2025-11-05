package com.philabid.util;

import com.philabid.AppContext;
import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

/**
 * A record that holds a monetary amount in its original currency and also its converted value
 * in the application's default currency. This class is immutable.
 *
 * @param originalAmount        The amount in its original currency.
 * @param defaultCurrencyAmount The amount converted to the application's default currency.
 *                              If conversion is not possible or not needed, it will be the same as originalAmount.
 */
public record MultiCurrencyMonetaryAmount(MonetaryAmount originalAmount, MonetaryAmount defaultCurrencyAmount)
        implements Comparable<MultiCurrencyMonetaryAmount> {

    public static MultiCurrencyMonetaryAmount ZERO =
            MultiCurrencyMonetaryAmount.of(Money.of(0, AppContext.getConfigurationService().getDefaultCurrency()));

    /**
     * Factory method to create a new instance of MultiCurrencyMonetaryAmount.
     * It handles the logic of currency conversion.
     *
     * @param amount The original monetary amount.
     * @return A new instance of MultiCurrencyMonetaryAmount.
     */
    public static MultiCurrencyMonetaryAmount of(MonetaryAmount amount) {
        if (amount == null) {
            return null;
        }

        CurrencyUnit defaultCurrency = AppContext.getConfigurationService().getDefaultCurrency();
        CurrencyUnit itemCurrency = amount.getCurrency();

        if (!itemCurrency.equals(defaultCurrency)) {
            MonetaryAmount convertedAmount = AppContext.getExchangeRateService()
                    .exchange(amount, defaultCurrency)
                    .orElse(amount); // Fallback to original if conversion fails
            return new MultiCurrencyMonetaryAmount(amount, convertedAmount);
        } else {
            // No conversion needed, both amounts are the same
            return new MultiCurrencyMonetaryAmount(amount, amount);
        }
    }

    public static MultiCurrencyMonetaryAmount of(MonetaryAmount amount, MonetaryAmount defaultCurrencyAmount) {
        if (amount == null) {
            return null;
        }
        return new MultiCurrencyMonetaryAmount(amount, defaultCurrencyAmount);
    }

    public boolean isDefaultCurrency() {
        return originalAmount.getCurrency().equals(defaultCurrencyAmount.getCurrency());
    }

    public CurrencyUnit getOriginalCurrency() {
        return originalAmount.getCurrency();
    }

    @Override
    public int compareTo(@NotNull MultiCurrencyMonetaryAmount o) {
        return defaultCurrencyAmount.compareTo(o.defaultCurrencyAmount);
    }

    public MultiCurrencyMonetaryAmount add(MultiCurrencyMonetaryAmount other) {
        if (other == null) {
            return this;
        }
        if (getOriginalCurrency().equals(other.getOriginalCurrency())) {
            return MultiCurrencyMonetaryAmount.of(originalAmount.add(other.originalAmount));
        } else {
            return MultiCurrencyMonetaryAmount.of(defaultCurrencyAmount.add(other.defaultCurrencyAmount));
        }
    }
}
