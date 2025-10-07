package com.philabid.ui.cell;

import com.philabid.util.MultiCurrencyMonetaryAmount;
import com.philabid.util.TriConsumer;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

import java.math.BigDecimal;
import java.util.List;

public class MultiCurrencyMonetaryAmountCell<T> extends TableCell<T, MultiCurrencyMonetaryAmount> {
    private final Label primaryAmountLabel = new Label();
    private final Label foreignCurrencyAmountLabel = new Label();
    private final TriConsumer<MultiCurrencyMonetaryAmount, T, List<Label>> styler;
    private final SVGPath icon = new SVGPath();

    public MultiCurrencyMonetaryAmountCell() {
        this(null);
    }

    public MultiCurrencyMonetaryAmountCell(TriConsumer<MultiCurrencyMonetaryAmount, T, List<Label>> styler) {
        this.styler = styler;

        icon.setVisible(false);

        foreignCurrencyAmountLabel.getStyleClass().add("secondary-currency-label");

        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.getChildren().addAll(icon, primaryAmountLabel);

        VBox vbox = new VBox(0);
        vbox.setAlignment(Pos.CENTER_RIGHT);
        vbox.getChildren().addAll(hbox, foreignCurrencyAmountLabel);

        setGraphic(vbox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    protected void updateItem(MultiCurrencyMonetaryAmount item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            // Hide the graphic content for empty rows
            if (getGraphic() != null) {
                getGraphic().setVisible(false);
            }
        } else {
            // Make sure the graphic is visible for non-empty rows
            if (getGraphic() != null) {
                getGraphic().setVisible(true);
            }

            // Always display the default currency amount as the primary value
            BigDecimal defaultNumber = item.defaultCurrencyAmount().getNumber().numberValue(BigDecimal.class);
            primaryAmountLabel.setText(String.format(java.util.Locale.ROOT, "%.2f %s", defaultNumber,
                    item.defaultCurrencyAmount().getCurrency().getCurrencyCode()));

            // If the currency is not the default, show the converted value as secondary info
            if (!item.isDefaultCurrency()) {
                BigDecimal originalNumber = item.originalAmount().getNumber().numberValue(BigDecimal.class);
                foreignCurrencyAmountLabel.setText(
                        String.format(java.util.Locale.ROOT, "(%.2f %s)", originalNumber,
                                item.originalAmount().getCurrency().getCurrencyCode()));
                foreignCurrencyAmountLabel.setVisible(true);
            } else {
                foreignCurrencyAmountLabel.setVisible(false);
                foreignCurrencyAmountLabel.setText(""); // Clear text to prevent old values from showing
            }

            // Apply custom styling if a styler function is provided
            if (styler != null) {
                T rowData = getTableRow().getItem();
                // Styler now affects all labels in the cell
                styler.accept(item, rowData, List.of(primaryAmountLabel, foreignCurrencyAmountLabel));
            }
        }
    }

    public SVGPath getIcon() {
        return icon;
    }
}
