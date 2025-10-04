package com.philabid.ui;

import com.philabid.util.TriConsumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTableViewController<T> {
    protected final ObservableList<T> tableItems = FXCollections.observableArrayList();
    private final List<TriConsumer<TableRow<T>, T, Boolean>> rowFormatters = new ArrayList<>();

    @FXML
    protected TableView<T> table;

    @FXML
    protected void initialize() {
        table.setItems(tableItems);
        initializeView();
        setRowFactories();
        setupContextMenu();
        refreshTable();
    }

    protected void refreshTable() {
        tableItems.setAll(loadTableItems());
        table.sort();
    }

    protected void initializeView() {
    }

    protected void addRowFormatter(TriConsumer<TableRow<T>, T, Boolean> formatter) {
        rowFormatters.add(formatter);
    }

    private void setRowFactories() {
        table.setRowFactory(param -> {
            TableRow<T> row = new TableRow<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);

                    rowFormatters.forEach(formatter -> formatter.accept(this, item, isEmpty()));
                }
            };

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    handleDoubleClick();
                }
            });

            return row;
        });
    }

    private void setupContextMenu() {
        List<MenuItem> menuItems = getContextMenuItems();

        if (menuItems.isEmpty()) {
            return;
        }

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(menuItems);

        // Show context menu only for non-empty rows
        table.setContextMenu(contextMenu);
        contextMenu.setOnShowing(e -> {
            if (table.getSelectionModel().isEmpty()) {
                e.consume(); // Don't show the menu
            }
        });
    }

    protected List<MenuItem> getContextMenuItems() {
        return List.of();
    }

    protected abstract List<T> loadTableItems();

    protected void handleDoubleClick() {
    }
}
