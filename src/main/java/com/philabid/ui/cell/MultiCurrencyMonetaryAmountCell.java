package com.philabid.ui.cell;

import com.philabid.util.MultiCurrencyMonetaryAmount;
import com.philabid.util.TriConsumer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.math.BigDecimal;
import java.util.List;

public class MultiCurrencyMonetaryAmountCell<T> extends TableCell<T, MultiCurrencyMonetaryAmount> {
    private final HBox hbox = new HBox(5);
    private final Label currencyLabel = new Label();
    private final Label amountLabel = new Label();
    private final Label foreignCurrencyAmountLabel = new Label();
    private final TriConsumer<MultiCurrencyMonetaryAmount, T, List<Label>> styler;

    public MultiCurrencyMonetaryAmountCell() {
        this(null);
    }

    public MultiCurrencyMonetaryAmountCell(TriConsumer<MultiCurrencyMonetaryAmount, T, List<Label>> styler) {
        this.styler = styler;

        // The amount should grow and push the currency to the right.
        HBox.setHgrow(amountLabel, Priority.ALWAYS);
        amountLabel.setMaxWidth(Double.MAX_VALUE);
        amountLabel.setAlignment(Pos.CENTER_RIGHT);
        hbox.getChildren().addAll(foreignCurrencyAmountLabel, amountLabel, currencyLabel);
    }

    @Override
    protected void updateItem(MultiCurrencyMonetaryAmount item, boolean empty) {
        super.updateItem(item, empty);

        // Reset state
        currencyLabel.setStyle("");
        amountLabel.setStyle("");
        foreignCurrencyAmountLabel.setStyle("");
        setGraphic(null);

        if (empty || item == null) {
            setText(null);
        } else {
            currencyLabel.setText(item.defaultCurrencyAmount().getCurrency().getCurrencyCode());

            BigDecimal number = item.defaultCurrencyAmount().getNumber().numberValue(BigDecimal.class);
            amountLabel.setText(String.format(java.util.Locale.ROOT, "%.2f", number));

            if (!item.isDefaultCurrency()) {
                number = item.originalAmount().getNumber().numberValue(BigDecimal.class);
                foreignCurrencyAmountLabel.setText(
                        String.format(java.util.Locale.ROOT, "[%.2f %s]", number,
                                item.originalAmount().getCurrency().getCurrencyCode()));
            } else {
                foreignCurrencyAmountLabel.setText("");
            }

            // Apply custom styling if a styler function is provided
            if (styler != null) {
                T rowData = getTableRow().getItem();
                styler.accept(item, rowData, List.of(foreignCurrencyAmountLabel, currencyLabel, amountLabel));
            }
            setGraphic(hbox);
        }
    }
}
