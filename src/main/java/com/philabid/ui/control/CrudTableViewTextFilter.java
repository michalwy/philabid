package com.philabid.ui.control;

import com.philabid.database.util.FilterCondition;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.Collection;
import java.util.List;

public abstract class CrudTableViewTextFilter extends CrudTableViewLabeledFilter {
    protected final javafx.scene.control.TextField textField = new javafx.scene.control.TextField();

    private final PauseTransition debounceTimer;

    public CrudTableViewTextFilter(String labelText) {
        super(labelText);
        // Initialize the timer with a 300ms delay
        this.debounceTimer = new PauseTransition(Duration.millis(300));
    }

    @Override
    public void initialize() {
        super.initialize();

        // When the timer finishes, update the filter condition
        debounceTimer.setOnFinished(event -> filterConditionProperty().set(getFilterCondition()));

        // On every text change, restart the timer
        textField.textProperty().addListener((observable, oldValue, newValue) -> debounceTimer.playFromStart());
    }

    @Override
    public Collection<Node> getFilterControls() {
        return List.of(textField);
    }

    protected abstract FilterCondition getFilterCondition();

    protected String getText() {
        return textField.getText();
    }

    @Override
    public void clear() {
        textField.clear();
    }
}
