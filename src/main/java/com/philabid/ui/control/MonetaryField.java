package com.philabid.ui.control;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

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

        // Add an event filter to intercept the Escape key press before the formatter does.
        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                // Find the stage (dialog) this control is in and close it.
                if (getScene() != null && getScene().getWindow() instanceof Stage) {
                    ((Stage) getScene().getWindow()).close();
                }
                event.consume(); // Consume the event so the formatter doesn't see it.
            }
        });
    }

    public BigDecimal getAmount() {
        String text = getText().replace(',', '.');
        if (text.isBlank()) {
            return null;
        }
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
