package com.philabid.ui.control;

import javafx.scene.Node;

import java.util.Collection;
import java.util.List;

public abstract class CrudTableViewTextFilter extends CrudTableViewLabeledFilter {
    protected final javafx.scene.control.TextField textField = new javafx.scene.control.TextField();

    public CrudTableViewTextFilter(String labelText) {
        super(labelText);
    }

    @Override
    public Collection<Node> getFilterControls() {
        return List.of(textField);
    }
}
