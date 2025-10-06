package com.philabid.ui.cell;

import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.scene.control.Label;

import java.util.List;
import java.util.function.Function;

public class ThresholdMultiCurrencyMonetaryAmountCell<T> extends MultiCurrencyMonetaryAmountCell<T> {
    public ThresholdMultiCurrencyMonetaryAmountCell(Function<T, MultiCurrencyMonetaryAmount> warningThresholdGetter,
                                                    Function<T, MultiCurrencyMonetaryAmount> criticalThresholdGetter) {
        super((value, row, labels) -> {
            if (row == null || value == null) return;

            MultiCurrencyMonetaryAmount warningThreshold = warningThresholdGetter.apply(row);
            MultiCurrencyMonetaryAmount criticalThreshold = criticalThresholdGetter.apply(row);

            if (criticalThreshold != null &&
                    value.defaultCurrencyAmount().isGreaterThan(criticalThreshold.defaultCurrencyAmount())) {
                applyStyleToLabels("-fx-text-fill: red; -fx-font-weight: bold;", labels);
            } else if (warningThreshold != null &&
                    value.defaultCurrencyAmount().isGreaterThan(warningThreshold.defaultCurrencyAmount())) {
                applyStyleToLabels("-fx-text-fill: orange;", labels);
            }
        });
    }

    private static void applyStyleToLabels(String cssStyle, List<Label> labels) {
        labels.forEach(label -> label.setStyle(cssStyle));
    }
}
