package com.philabid.ui.cell;

import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.scene.control.Label;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ThresholdMultiCurrencyMonetaryAmountCell<T> extends MultiCurrencyMonetaryAmountCell<T> {

    public static final String CRITICAL_THRESHOLD_STYLE_CLASS = "monetary-threshold-critical";
    public static final String WARNING_THRESHOLD_STYLE_CLASS = "monetary-threshold-warning";

    public ThresholdMultiCurrencyMonetaryAmountCell(Function<T, MultiCurrencyMonetaryAmount> warningThresholdGetter,
                                                    Function<T, MultiCurrencyMonetaryAmount> criticalThresholdGetter) {
        this(warningThresholdGetter, criticalThresholdGetter, List.of());
    }

    public ThresholdMultiCurrencyMonetaryAmountCell(Function<T, MultiCurrencyMonetaryAmount> warningThresholdGetter,
                                                    Function<T, MultiCurrencyMonetaryAmount> criticalThresholdGetter,
                                                    Collection<String> additionalStyleClasses) {
        super((value, row, labels) -> {

            removeStyleClassFromLabels(CRITICAL_THRESHOLD_STYLE_CLASS, labels);
            removeStyleClassFromLabels(WARNING_THRESHOLD_STYLE_CLASS, labels);
            additionalStyleClasses.forEach(styleClass -> removeStyleClassFromLabels(styleClass, labels));

            if (row == null || value == null) return;

            MultiCurrencyMonetaryAmount warningThreshold = warningThresholdGetter.apply(row);
            MultiCurrencyMonetaryAmount criticalThreshold = criticalThresholdGetter.apply(row);

            if (criticalThreshold != null &&
                    value.defaultCurrencyAmount().isGreaterThan(criticalThreshold.defaultCurrencyAmount())) {
                applyStyleClassToLabels(CRITICAL_THRESHOLD_STYLE_CLASS, labels);
            } else if (warningThreshold != null &&
                    value.defaultCurrencyAmount().isGreaterThan(warningThreshold.defaultCurrencyAmount())) {
                applyStyleClassToLabels(WARNING_THRESHOLD_STYLE_CLASS, labels);
            }

            additionalStyleClasses.forEach(styleClass -> applyStyleClassToLabels(styleClass, labels));
        });
    }

    private static void applyStyleClassToLabels(String styleClass, List<Label> labels) {
        labels.forEach(label -> label.getStyleClass().add(styleClass));
    }

    private static void removeStyleClassFromLabels(String styleClass, List<Label> labels) {
        labels.forEach(label -> label.getStyleClass().remove(styleClass));
    }
}
