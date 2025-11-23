package com.philabid.ui.cell;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public record CatalogNumberColumnValue(String catalogNumber, Long orderNumber, Long categoryOrderNumber) {

    public static Comparator<CatalogNumberColumnValue> SORT_COMPARATOR =
            Comparator.comparing(CatalogNumberColumnValue::categoryOrderNumber)
                    .thenComparing(CatalogNumberColumnValue::orderNumber);

    @NotNull
    public String toString() {
        return catalogNumber;
    }
}
