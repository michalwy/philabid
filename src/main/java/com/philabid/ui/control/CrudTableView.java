package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.BaseModel;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrudTableView<T extends BaseModel<T>> extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(CrudTableView.class);

    private final List<CrudTableViewFilter> filters = new ArrayList<>();
    private final ListProperty<FilterCondition> filterConditions =
            new SimpleListProperty<>(FXCollections.observableArrayList());
    @FXML
    TableView<T> tableView;
    @FXML
    Label titleLabel;
    @FXML
    CrudToolbar crudToolbar;
    @FXML
    ToolBar filterToolbar;

    public CrudTableView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/CrudTableView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(AppContext.getI18nManager().getResourceBundle());

        try {
            fxmlLoader.load();
            filterToolbar.setVisible(false);
            filterToolbar.setManaged(false);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public final ObservableList<TableColumn<T, ?>> getColumns() {
        return tableView.getColumns();
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setTitle(String value) {
        titleLabel.setText(value);
    }

    public TableView<T> getTableView() {
        return tableView;
    }

    public void addFilter(CrudTableViewFilter filter) {
        filters.add(filter);

        filterToolbar.getItems().add(filter.getControl());

        filter.filterConditionProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        filterToolbar.setVisible(true);
        filterToolbar.setManaged(true);

        filter.initialize();
    }

    public void applyFilters() {
        filterConditions.setAll(filters.stream()
                .map(f -> f.filterConditionProperty().get())
                .filter(Objects::nonNull)
                .toList());
    }

    public ListProperty<FilterCondition> getFilterConditions() {
        return filterConditions;
    }

    public void setAddAction(EventHandler<ActionEvent> handler) {
        crudToolbar.setAddAction(handler);
    }

    public void setEditAction(EventHandler<ActionEvent> handler) {
        crudToolbar.setEditAction(handler);
    }

    public void setDeleteAction(EventHandler<ActionEvent> handler) {
        crudToolbar.setDeleteAction(handler);
    }

    public void bindButtonsDisabledProperty(BooleanBinding binding) {
        crudToolbar.bindButtonsDisabledProperty(binding);
    }

    public void setItems(ObservableList<T> items) {
        tableView.setItems(items);
    }

    public void sort() {
        tableView.sort();
    }
}
