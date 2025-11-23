package com.philabid.ui.control;

import com.philabid.database.util.EntityFilterCondition;
import com.philabid.database.util.query.QueryOrder;
import com.philabid.model.BaseModel;
import com.philabid.service.AbstractCrudService;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.util.Collection;
import java.util.List;

public abstract class CrudTableViewComboBoxFilter<T extends BaseModel<T>> extends CrudTableViewLabeledFilter {

    protected final ComboBox<T> comboBox = new ComboBox<>();
    private final String filterField;
    private final Collection<T> allItems;

    public CrudTableViewComboBoxFilter(String labelText, String filterField, AbstractCrudService<T> crudService) {
        this(labelText, filterField, crudService, List.of());
    }

    public CrudTableViewComboBoxFilter(String labelText, String filterField, AbstractCrudService<T> crudService,
                                       Collection<QueryOrder> orders) {
        super(labelText);
        this.filterField = filterField;
        this.allItems = crudService.getAll(List.of(), orders);
    }

    @Override
    public Collection<Node> getFilterControls() {
        return List.of(comboBox);
    }

    @Override
    public void initialize() {
        super.initialize();

        comboBox.setItems(FXCollections.observableArrayList(allItems));

        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterConditionProperty().set(
                    newValue != null ? new EntityFilterCondition<T>(filterField, newValue) : null);
        });
    }

    @Override
    public void clear() {
        comboBox.getSelectionModel().clearSelection();
    }

    public void select(T entity) {
        comboBox.getSelectionModel().select(entity);
    }

    public void select(Long id) {
        allItems.stream()
                .filter(i -> i.getId().equals(id))
                .findAny()
                .ifPresent(this::select);
    }
}
