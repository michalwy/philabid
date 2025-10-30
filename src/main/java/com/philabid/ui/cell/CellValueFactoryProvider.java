package com.philabid.ui.cell;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * A utility class that provides reusable CellValueFactory implementations for common column types.
 */
public final class CellValueFactoryProvider {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
}