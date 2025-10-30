package com.philabid.ui.cell;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

import java.util.function.Function;

public class CatalogNumberWithWarningItemCell<T> extends TableCell<T, CatalogNumberColumnValue> {

    private final Label textLabel = new Label();
    private final SVGPath warningIcon = new SVGPath();

    private final Function<T, Boolean> warningGetter;

    public CatalogNumberWithWarningItemCell(Function<T, Boolean> warningGetter) {
        this.warningGetter = warningGetter;

        warningIcon.setContent(
                "M10,2c-4.42,0-8,3.58-8,8s3.58,8,8,8s8-3.58,8-8S14.42,2,10,2z M11,16H9v-2h2V16z M11,12H9V6h2V12z");
        warningIcon.getStyleClass().add("warning-icon");

        HBox graphicBox = new HBox(5);
        graphicBox.getChildren().addAll(textLabel, warningIcon);
        graphicBox.setAlignment(Pos.CENTER_LEFT);

        setGraphic(graphicBox);
    }

    @Override
    protected void updateItem(CatalogNumberColumnValue item, boolean empty) {
        super.updateItem(item, empty);

        textLabel.setText("");
        warningIcon.setVisible(false);
        warningIcon.setManaged(false);

        if (!empty && item != null) {
            textLabel.setText(item.catalogNumber());
            Boolean warning = warningGetter.apply(getTableRow().getItem());
            warningIcon.setVisible(warning);
            warningIcon.setManaged(warning);
        }
    }
}