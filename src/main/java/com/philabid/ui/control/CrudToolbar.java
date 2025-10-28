package com.philabid.ui.control;

import com.philabid.AppContext;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;

import java.io.IOException;

public class CrudToolbar extends ToolBar {

    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;

    public CrudToolbar() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/CrudToolbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(AppContext.getI18nManager().getResourceBundle()); // Pass the resource bundle

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setAddAction(EventHandler<ActionEvent> handler) {
        addButton.setOnAction(handler);
    }

    public void setEditAction(EventHandler<ActionEvent> handler) {
        editButton.setOnAction(handler);
    }

    public void setDeleteAction(EventHandler<ActionEvent> handler) {
        deleteButton.setOnAction(handler);
    }

    public void setRefreshAction(EventHandler<ActionEvent> handler) {
        refreshButton.setOnAction(handler);
    }
    
    public void bindButtonsDisabledProperty(BooleanBinding binding) {
        editButton.disableProperty().bind(binding);
        deleteButton.disableProperty().bind(binding);
    }
}