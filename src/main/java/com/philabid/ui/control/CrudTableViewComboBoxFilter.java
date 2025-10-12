package com.philabid.ui.control;

import com.philabid.model.BaseModel;
import com.philabid.service.CrudService;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.List;

public abstract class CrudTableViewComboBoxFilter<T extends BaseModel<T>> extends CrudTableViewLabeledFilter {

    protected final ComboBox<T> comboBox = new ComboBox<>();
    protected final Button clearButton = new Button("Clear");
    private final String filterField;
    private final CrudService<T> crudService;

    public CrudTableViewComboBoxFilter(String labelText, String filterField, CrudService<T> crudService) {
        super(labelText);
        this.filterField = filterField;
        this.crudService = crudService;
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
