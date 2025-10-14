package com.philabid.ui.cell;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import java.util.function.Function;

/**
 * A specialized cell for displaying catalog values in the auction table.
 * It extends the standard multi-currency cell to add a warning icon
 * if the catalog value comes from an inactive catalog.
 */
public class CatalogValueCell<T> extends MultiCurrencyMonetaryAmountCell<T> {

    private final Function<T, MultiCurrencyMonetaryAmount> valueGetter;
    private final Function<T, Boolean> activeStatueGetter;

    public CatalogValueCell(Function<T, MultiCurrencyMonetaryAmount> valueGetter,
                            Function<T, Boolean> activeStatusGetter) {
        this.valueGetter = valueGetter;
        this.activeStatueGetter = activeStatusGetter;

        // A simple warning/info icon
        getIcon().setContent(
                "M10,2c-4.42,0-8,3.58-8,8s3.58,8,8,8s8-3.58,8-8S14.42,2,10,2z M11,16H9v-2h2V16z M11,12H9V6h2V12z");
        getIcon().getStyleClass().add("warning-icon");
    }

    @Override
    protected void updateItem(MultiCurrencyMonetaryAmount item, boolean empty) {
        super.updateItem(item, empty);

        T entity = getTableRow() != null ? getTableRow().getItem() : null;

        // Show the icon only if the auction exists and its catalog is inactive
        getIcon().setVisible(
                !empty && entity != null && valueGetter.apply(entity) != null && !activeStatueGetter.apply(entity));
    }
}