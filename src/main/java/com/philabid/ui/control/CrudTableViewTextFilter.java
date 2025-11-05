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
    private boolean immediateSet = false;

    public CrudTableViewTextFilter(String labelText) {
        super(labelText);
        // Initialize the timer with a 300ms delay
        this.debounceTimer = new PauseTransition(Duration.millis(300));
    }

    @Override
    public void initialize() {
        super.initialize();

        // When the timer finishes, update the filter condition
        debounceTimer.setOnFinished(event -> propagateFilterCondition());

        // On every text change, restart the timer
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (immediateSet) {
                propagateFilterCondition();
            } else {
                debounceTimer.playFromStart();
            }
        });
    }

    private void propagateFilterCondition() {
        filterConditionProperty().set(getFilterCondition());
    }

    @Override
    public Collection<Node> getFilterControls() {
        return List.of(textField);
    }

    protected abstract FilterCondition getFilterCondition();

    protected String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        immediateRefresh(() -> textField.setText(text));
    }

    @Override
    public void clear() {
        immediateRefresh(textField::clear);
    }

    protected void immediateRefresh(Runnable runnable) {
        immediateSet = true;
        runnable.run();
        immediateSet = false;
    }
}
