package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.BaseModel;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class CrudTableView<T extends BaseModel<T>> extends VBox {
    @FXML
    TableView<T> tableView;

    @FXML
    Label titleLabel;

    @FXML
    CrudToolbar crudToolbar;

    public CrudTableView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/CrudTableView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(AppContext.getI18nManager().getResourceBundle());

        try {
            fxmlLoader.load();
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
