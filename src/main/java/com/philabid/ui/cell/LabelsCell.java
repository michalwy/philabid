package com.philabid.ui.cell;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class LabelsCell<T, V> extends TableCell<T, List<V>> {
    private final List<Label> labels;

    public LabelsCell(int maxLabels) {
        labels = new ArrayList<>(maxLabels);
        for (int i = 0; i < maxLabels; i++) {
            labels.add(new Label());
        }

        HBox graphicBox = new HBox(5);
        graphicBox.setAlignment(Pos.CENTER);
        graphicBox.getChildren().addAll(labels);

        setGraphic(graphicBox);
    }

    @Override
    protected void updateItem(List<V> item, boolean empty) {
        super.updateItem(item, empty);

        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).setVisible(false);
            labels.get(i).setManaged(false);
            if (item != null && i < item.size()) {
                labels.get(i).setVisible(true);
                labels.get(i).setManaged(true);
                labels.get(i).setText(item.get(i).toString());
            }
        }
    }
}