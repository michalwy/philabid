package com.philabid.ui.control;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Collection;

public abstract class CrudTableViewLabeledFilter extends CrudTableViewFilter {

    private final HBox hBox = new HBox(3);
    private final Label label = new Label();
    private final String labelText;

    protected CrudTableViewLabeledFilter(String labelText) {
        this.labelText = labelText;
    }

    @Override
    public Node getControl() {
        return hBox;
    }

    @Override
    public void initialize() {
        label.setText(labelText);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().add(label);
        hBox.getChildren().addAll(getFilterControls());
    }

    protected abstract Collection<Node> getFilterControls();
}
