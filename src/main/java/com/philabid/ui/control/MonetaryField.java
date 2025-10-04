package com.philabid.ui.control;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

public class MonetaryField extends TextField {
    public MonetaryField() {
        super();

        setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("^\\d*([.,]\\d*)?$")) {
                return change;
            }
            return null;
        }));
    }

    public BigDecimal getAmount() {
        String text = getText().replace(',', '.');
        return new BigDecimal(text);
    }

    public void setAmount(BigDecimal amount) {
        setText(amount.toString());
    }

    public void setAmount(MonetaryAmount amount) {
        setAmount(amount.getNumber().numberValue(BigDecimal.class));
    }

    public boolean isEmpty() {
        return getText().isEmpty() || getText().isBlank();
    }
}
