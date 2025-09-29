package com.philabid.ui;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

record CatalogNumberColumnValue(String catalogNumber, Long orderNumber) {

    public static Comparator<CatalogNumberColumnValue> SORT_COMPARATOR =
            Comparator.comparingLong(CatalogNumberColumnValue::orderNumber);

    @NotNull
    public String toString() {
        return catalogNumber;
    }
}
