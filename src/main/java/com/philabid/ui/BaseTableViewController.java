package com.philabid.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.List;

public abstract class BaseTableViewController<T> {
    protected final ObservableList<T> tableItems = FXCollections.observableArrayList();

    @FXML
    protected TableView<T> table;

    @FXML
    protected void initialize() {
        setupTableColumns();
        table.setItems(tableItems);

        initializeView();

        refreshTable();
    }

    protected void refreshTable() {
        tableItems.setAll(loadTableItems());
        table.sort();
    }

    protected void initializeView() {
    }

    protected void setupTableColumns() {
    }

    protected abstract List<T> loadTableItems();
}
