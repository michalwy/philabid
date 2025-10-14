package com.philabid.ui.control;

import com.philabid.database.util.EntityFilterCondition;
import com.philabid.model.BaseModel;
import com.philabid.service.AbstractCrudService;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.SVGPath;

import java.util.Collection;
import java.util.List;

public abstract class CrudTableViewComboBoxFilter<T extends BaseModel<T>> extends CrudTableViewLabeledFilter {

    protected final ComboBox<T> comboBox = new ComboBox<>();
    protected final Button clearButton = new Button();
    private final String filterField;
    private final AbstractCrudService<T> crudService;

    public CrudTableViewComboBoxFilter(String labelText, String filterField, AbstractCrudService<T> crudService) {
        super(labelText);
        this.filterField = filterField;
        this.crudService = crudService;

        // Create a simple "X" icon for the clear button
        SVGPath crossIcon = new SVGPath();
        crossIcon.setContent("M 0 0 L 7 7 M 7 0 L 0 7"); // A simple 'X'
        crossIcon.setStyle("-fx-stroke: -fx-text-main; -fx-stroke-width: 1.5;");

        // Set icon and tooltip for better UX
        clearButton.setGraphic(crossIcon);
        clearButton.setTooltip(new Tooltip("Clear filter"));
    }

    @Override
    public Collection<Node> getFilterControls() {
        return List.of(comboBox, clearButton);
    }

    @Override
    public void initialize() {
        super.initialize();

        comboBox.setItems(FXCollections.observableArrayList(crudService.getAll()));

        clearButton.setOnAction(event -> {
            comboBox.getSelectionModel().clearSelection();
        });
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterConditionProperty().set(
                    newValue != null ? new EntityFilterCondition<T>(filterField, newValue) : null);
        });
    }
}
