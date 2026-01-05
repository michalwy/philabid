package com.philabid.ui;

import com.philabid.model.BaseModel;
import com.philabid.ui.control.CrudEditDialog;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.Collection;

/**
 * An abstract base controller for entity edit dialogs.
 * It handles common functionalities like save/cancel buttons and dialog lifecycle.
 *
 * @param <T> The type of the entity being edited.
 */
public abstract class CrudEditDialogController<T extends BaseModel<T>> {
    @FXML
    protected CrudEditDialog<T> crudEditDialog;
    private EditDialogResult result = new EditDialogResult.Builder()
            .saved(false)
            .editNext(false)
            .build();
    private Stage dialogStage;
    private T entity;

    @FXML
    protected void initialize() {
        crudEditDialog.setOnCancel(() -> {
            dialogStage.close();
        });
        crudEditDialog.setOnSave(() -> {
            if (handleSave()) {
                result = new EditDialogResult.Builder()
                        .saved(true)
                        .editNext(entity.getId() == null)
                        .build();
                dialogStage.close();
            }
        });
        initContent();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        T oldEntity = this.entity;
        this.entity = entity;
        onSetEntity(oldEntity, entity);
    }

    protected void initContent() {
    }

    protected void onSetEntity(T oldEntity, T newEntity) {
        loadEntity(newEntity);
    }

    protected abstract void loadEntity(T entity);

    protected boolean handleSave() {
        Collection<ValidationError> validationErrors = validate();
        if (!validationErrors.isEmpty()) {
            String errorMessage = String.join("\n", validationErrors.stream()
                    .map(ValidationError::message)
                    .toList());

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
        updateEntity(getEntity());
        return true;
    }

    protected abstract Collection<ValidationError> validate();

    protected abstract void updateEntity(T entity);

    public final EditDialogResult getResult() {
        return result;
    }

    protected boolean isNewEntity() {
        return getEntity().getId() == null;
    }

    protected record ValidationError(String message, Parent control) {
    }
}