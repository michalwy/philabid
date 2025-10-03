package com.philabid.ui.util;

import org.jetbrains.annotations.NotNull;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.util.Comparator;
import java.util.Locale;

public record MonetaryColumnValue(MonetaryAmount monetaryAmount) {
    public static Comparator<MonetaryColumnValue> SORT_COMPARATOR =
            Comparator.comparing(MonetaryColumnValue::monetaryAmount);

    @NotNull
    @Override
    public String toString() {
        if (monetaryAmount != null) {
            MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.getDefault());
            return format.format(monetaryAmount);
        } else {
            return "";
        }
    }
}
