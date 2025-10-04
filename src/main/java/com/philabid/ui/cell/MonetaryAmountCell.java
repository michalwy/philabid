package com.philabid.ui.cell;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

public class MonetaryAmountCell<T> extends TableCell<T, MonetaryAmount> {
    private final HBox hbox = new HBox(5);
    private final Label currencyLabel = new Label();
    private final Label amountLabel = new Label();
    private final BiConsumer<T, List<Label>> styler;

    public MonetaryAmountCell() {
        this(null);
    }

    public MonetaryAmountCell(BiConsumer<T, List<Label>> styler) {
        this.styler = styler;

        // The amount should grow and push the currency to the right.
        HBox.setHgrow(amountLabel, Priority.ALWAYS);
        amountLabel.setMaxWidth(Double.MAX_VALUE);
        amountLabel.setAlignment(Pos.CENTER_RIGHT);
        hbox.getChildren().addAll(amountLabel, currencyLabel);
    }

    @Override
    protected void updateItem(MonetaryAmount item, boolean empty) {
        super.updateItem(item, empty);

        // Reset state
        currencyLabel.setStyle("");
        amountLabel.setStyle("");
        setGraphic(null);

        if (empty || item == null) {
            setText(null);
        } else {
            currencyLabel.setText(item.getCurrency().getCurrencyCode());

            // Format the number to always show two decimal places
            BigDecimal number = item.getNumber().numberValue(BigDecimal.class);
            amountLabel.setText(String.format(java.util.Locale.ROOT, "%.2f", number));

            // Apply custom styling if a styler function is provided
            if (styler != null) {
                T rowData = getTableRow().getItem();
                styler.accept(rowData, List.of(currencyLabel, amountLabel));
            }
            setGraphic(hbox);
        }
    }
}
