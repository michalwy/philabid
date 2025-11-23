package com.philabid.ui.cell;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public record CatalogNumberColumnValue(String catalogNumber, Long orderNumber, String categoryCode) {

    public static Comparator<CatalogNumberColumnValue> SORT_COMPARATOR =
            Comparator.comparing(CatalogNumberColumnValue::categoryCode, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(CatalogNumberColumnValue::catalogNumber, String.CASE_INSENSITIVE_ORDER);

    @NotNull
    public String toString() {
        return catalogNumber;
    }
}
