package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.BaseModel;
import javafx.beans.DefaultProperty;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * A custom component that acts as a base for all edit dialogs.
 * It provides a consistent layout with a save/cancel button bar at the bottom.
 * The specific form content can be nested inside this component in FXML.
 */
@DefaultProperty("center")
public class BaseEditDialog<T extends BaseModel<T>> extends BorderPane {
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    public BaseEditDialog() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/BaseEditDialog.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(AppContext.getI18nManager().getResourceBundle());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setOnSave(Runnable action) {
        saveButton.setOnAction(event -> action.run());
    }

    public void setOnCancel(Runnable action) {
        cancelButton.setOnAction(event -> action.run());
    }

    public void bindSaveButtonDisabled(BooleanBinding binding) {
        saveButton.disableProperty().bind(binding);
    }
}