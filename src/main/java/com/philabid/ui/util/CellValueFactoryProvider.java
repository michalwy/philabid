package com.philabid.ui.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * A utility class that provides reusable CellValueFactory implementations for common column types.
 */
public final class CellValueFactoryProvider {

    /**
     * Private constructor to prevent instantiation.
     */
    private CellValueFactoryProvider() {
    }

    /**
     * Creates a CellValueFactory for a column displaying a catalog's name and issue year.
     *
     * @param nameGetter      A function to get the catalog name from the row's data object.
     * @param issueYearGetter A function to get the issue year from the row's data object.
     * @param <T>             The type of the data object in the table row.
     * @return A Callback for the cell value factory.
     */
    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> forCatalogInfo(
            Function<T, String> nameGetter, Function<T, Integer> issueYearGetter) {
        return cellData -> {
            T value = cellData.getValue();
            if (value == null) return new SimpleStringProperty("");
            String name = nameGetter.apply(value);
            Integer year = issueYearGetter.apply(value);
            if (name != null && year != null) {
                return new SimpleStringProperty(String.format("%s (%d)", name, year));
            } else if (name != null) {
                return new SimpleStringProperty(name);
            }
            return new SimpleStringProperty("");
        };
    }

    /**
     * Creates a CellValueFactory for a column displaying a category's name and code.
     *
     * @param <T>        The type of the data object in the table row.
     * @param codeGetter A function to get the category code.
     * @param nameGetter A function to get the category name.
     * @return A Callback for the cell value factory.
     */
    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> forCategoryInfo(
            Function<T, String> codeGetter, Function<T, String> nameGetter) {
        return cellData -> new SimpleStringProperty(String.format("%s (%s)", nameGetter.apply(cellData.getValue()),
                codeGetter.apply(cellData.getValue())));
    }

    /**
     * Creates a CellValueFactory for the complex "Catalog Number" column that supports custom sorting.
     *
     * @param catalogNumberGetter A function to get the catalog number string.
     * @param orderNumberGetter   A function to get the numeric order number.
     * @param <T>                 The type of the data object in the table row.
     * @return A Callback for the cell value factory.
     */
    public static <T> Callback<TableColumn.CellDataFeatures<T, CatalogNumberColumnValue>,
            ObservableValue<CatalogNumberColumnValue>> forCatalogNumber(
            Function<T, String> catalogNumberGetter, Function<T, Long> orderNumberGetter) {
        return cellData -> new SimpleObjectProperty<>(
                new CatalogNumberColumnValue(catalogNumberGetter.apply(cellData.getValue()),
                        orderNumberGetter.apply(cellData.getValue())));
    }

    /**
     * Creates a CellValueFactory for a column displaying a conditions's name and code.
     *
     * @param codeGetter A function to get the condition code.
     * @param nameGetter A function to get the condition name.
     * @param <T>        The type of the data object in the table row.
     * @return A Callback for the cell value factory.
     */
    public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> forConditionInfo(
            Function<T, String> codeGetter, Function<T, String> nameGetter) {
        return cellData -> new SimpleStringProperty(String.format("%s - %s", codeGetter.apply(cellData.getValue()),
                nameGetter.apply(cellData.getValue())));
    }

    /**
     * Creates a CellValueFactory for a column displaying a monetary value with its currency symbol/code.
     *
     * @param valueGetter A function to get the MonetaryAmount from the row's data object.
     * @param <T>         The type of the data object in the table row.
     * @return A Callback for the cell value factory.
     */
    public static <T> Callback<TableColumn.CellDataFeatures<T, MonetaryColumnValue>,
            ObservableValue<MonetaryColumnValue>> forValueWithCurrency(
            Function<T, MonetaryAmount> valueGetter) {
        return cellData -> new SimpleObjectProperty<>(new MonetaryColumnValue(valueGetter.apply(cellData.getValue())));
    }

    /**
     * Creates a reusable CellFactory for columns displaying MonetaryAmount with custom layout.
     * It formats the value with a right-aligned amount and a currency code.
     *
     * @param styler An optional function to apply custom styles to the labels based on the row's data.
     * @param <T>    The type of the data object in the table row.
     * @return A Callback for creating the TableCell.
     */
    public static <T> Callback<TableColumn<T, MonetaryColumnValue>, TableCell<T, MonetaryColumnValue>> forMonetaryValue(
            java.util.function.BiConsumer<T, List<Label>> styler) {
        return column -> new TableCell<>() {
            private final HBox hbox = new HBox(5);
            private final Label currencyLabel = new Label();
            private final Label amountLabel = new Label();

            {
                // The amount should grow and push the currency to the right.
                HBox.setHgrow(amountLabel, Priority.ALWAYS);
                amountLabel.setMaxWidth(Double.MAX_VALUE);
                amountLabel.setAlignment(Pos.CENTER_RIGHT);
                hbox.getChildren().addAll(amountLabel, currencyLabel);
            }

            @Override
            protected void updateItem(MonetaryColumnValue item, boolean empty) {
                super.updateItem(item, empty);

                // Reset state
                currencyLabel.setStyle("");
                amountLabel.setStyle("");
                setGraphic(null);

                if (empty || item == null || item.monetaryAmount() == null) {
                    setText(null);
                } else {
                    MonetaryAmount amount = item.monetaryAmount();
                    currencyLabel.setText(amount.getCurrency().getCurrencyCode());

                    // Format the number to always show two decimal places
                    BigDecimal number = amount.getNumber().numberValue(BigDecimal.class);
                    amountLabel.setText(String.format(java.util.Locale.ROOT, "%.2f", number));

                    // Apply custom styling if a styler function is provided
                    if (styler != null) {
                        T rowData = getTableRow().getItem();
                        styler.accept(rowData, List.of(currencyLabel, amountLabel));
                    }
                    setGraphic(hbox);
                }
            }
        };
    }
}