package com.philabid.ui.util;

import com.philabid.AppContext;
import com.philabid.ui.cell.*;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public class TableViewHelpers {
    public static <T> void setCategoryColumn(TableColumn<T, String> categoryColumn,
                                             Function<T, String> categoryCodeGetter,
                                             Function<T, String> categoryNameGetter) {
        categoryColumn.setCellValueFactory(
                CellValueFactoryProvider.forCategoryInfo(categoryCodeGetter, categoryNameGetter));
    }

    public static <T> void setCatalogNumberColumn(TableColumn<T, CatalogNumberColumnValue> catalogNumberColumn,
                                                  Function<T, String> catalogNumberGetter,
                                                  Function<T, Long> orderNumberGetter,
                                                  Function<T, Long> categoryOrderNumberGetter) {
        catalogNumberColumn.setCellValueFactory(
                CellValueFactoryProvider.forCatalogNumber(catalogNumberGetter, orderNumberGetter,
                        categoryOrderNumberGetter));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);
    }

    public static <T> void setCatalogNumberWithWarningColumn(
            TableColumn<T, CatalogNumberColumnValue> catalogNumberColumn,
            Function<T, String> catalogNumberGetter,
            Function<T, Long> orderNumberGetter,
            Function<T, Long> categoryOrderNumberGetter,
            Function<T, Boolean> warningGetter, Function<T, Boolean> criticalGetter) {
        catalogNumberColumn.setCellValueFactory(
                CellValueFactoryProvider.forCatalogNumber(catalogNumberGetter, orderNumberGetter,
                        categoryOrderNumberGetter));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);
        catalogNumberColumn.setCellFactory(
                column -> new CatalogNumberWithWarningItemCell<>(warningGetter, criticalGetter));
    }

    public static <T, V> void setLabelsColumn(TableColumn<T, List<V>> column, String property, int maxLabels) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(column1 -> new LabelsCell<>(maxLabels));
    }

    public static <T> void setConditionColumn(TableColumn<T, String> conditionColumn,
                                              Function<T, String> conditionCodeGetter,
                                              Function<T, String> conditionNameGetter) {
        conditionColumn.setCellValueFactory(
                CellValueFactoryProvider.forConditionInfo(conditionCodeGetter, conditionNameGetter));
    }

    public static <T> void setCatalogColumn(TableColumn<T, String> catalogColumn, Function<T, String> nameGetter,
                                            Function<T, Integer> issueYearGetter) {
        catalogColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogInfo(nameGetter, issueYearGetter));
    }

    public static <T> void setCatalogValueColumn(TableColumn<T, MultiCurrencyMonetaryAmount> catalogValueColumn,
                                                 String property,
                                                 Function<T, MultiCurrencyMonetaryAmount> catalogValueGetter,
                                                 Function<T, Boolean> catalogActiveGetter) {
        catalogValueColumn.setCellValueFactory(new PropertyValueFactory<>(property));
        catalogValueColumn.setCellFactory(
                column -> new CatalogValueCell<>(catalogValueGetter, catalogActiveGetter));
    }

    public static <T> void setMonetaryColumn(TableColumn<T, MultiCurrencyMonetaryAmount> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(c -> new MultiCurrencyMonetaryAmountCell<>());
    }

    public static <T> void setPriceWithThresholdColumn(TableColumn<T, MultiCurrencyMonetaryAmount> priceColumn,
                                                       String property,
                                                       Function<T, MultiCurrencyMonetaryAmount> warningThresholdGetter,
                                                       Function<T, MultiCurrencyMonetaryAmount> criticalThresholdGetter) {
        priceColumn.setCellValueFactory(new PropertyValueFactory<>(property));
        priceColumn.setCellFactory(
                column -> new ThresholdMultiCurrencyMonetaryAmountCell<>(warningThresholdGetter,
                        criticalThresholdGetter));
    }

    public static <T> void setDateTimeColumn(TableColumn<T, LocalDateTime> column, String property) {
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setCellFactory(c -> new RightAlignedDateCell<>());
    }

    public static <T> void setUrlColumn(TableColumn<T, String> urlColumn, String property) {
        urlColumn.setCellValueFactory(new PropertyValueFactory<>(property));
        urlColumn.setStyle("-fx-alignment: CENTER_LEFT;");
        urlColumn.setCellFactory(column -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setOnAction(event -> {
                    if (!link.getText().isEmpty()) {
                        AppContext.getHostServices().showDocument(link.getText());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : link);
                link.setText(item);
            }
        });
    }
}
