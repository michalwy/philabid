package com.philabid.ui.cell;

import com.philabid.util.MultiCurrencyMonetaryAmount;
import com.philabid.util.TriConsumer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class MultiCurrencyMonetaryAmountCell<T> extends TableCell<T, MultiCurrencyMonetaryAmount> {
    private final VBox vbox = new VBox(0); // Use VBox for vertical layout
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
        HBox primaryAmountBox = new HBox(5);
        HBox.setHgrow(primaryAmountBox, Priority.ALWAYS);
        primaryAmountBox.setAlignment(Pos.CENTER_RIGHT);
        primaryAmountBox.getChildren().addAll(amountLabel, currencyLabel);

        foreignCurrencyAmountLabel.getStyleClass().add("secondary-currency-label");
        vbox.getChildren().addAll(primaryAmountBox, foreignCurrencyAmountLabel);
        vbox.setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    protected void updateItem(MultiCurrencyMonetaryAmount item, boolean empty) {
        super.updateItem(item, empty);

        // Reset state
        currencyLabel.setStyle("");
        amountLabel.setStyle("");
        foreignCurrencyAmountLabel.setText(""); // Clear secondary label
        setGraphic(null);

        if (empty || item == null) {
            setText(null);
        } else {
            // Always display the default currency amount as the primary value
            BigDecimal defaultNumber = item.defaultCurrencyAmount().getNumber().numberValue(BigDecimal.class);
            amountLabel.setText(String.format(java.util.Locale.ROOT, "%.2f", defaultNumber));
            currencyLabel.setText(item.defaultCurrencyAmount().getCurrency().getCurrencyCode());

            // If the currency is not the default, show the converted value as secondary info
            if (!item.isDefaultCurrency()) {
                BigDecimal originalNumber = item.originalAmount().getNumber().numberValue(BigDecimal.class);
                foreignCurrencyAmountLabel.setText(
                        String.format(java.util.Locale.ROOT, "(%.2f %s)", originalNumber,
                                item.originalAmount().getCurrency().getCurrencyCode()));
            }

            // Apply custom styling if a styler function is provided
            if (styler != null) {
                T rowData = getTableRow().getItem();
                // Styler now affects all labels in the cell
                styler.accept(item, rowData, List.of(amountLabel, currencyLabel, foreignCurrencyAmountLabel));
            }
            setGraphic(vbox);
        }
    }
}
