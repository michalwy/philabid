package com.philabid.ui;

import com.philabid.model.Condition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the condition edit dialog.
 */
public class ConditionEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(ConditionEditDialogController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private Condition condition;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        logger.debug("ConditionEditDialogController initialized.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        nameField.setText(condition.getName());
        codeField.setText(condition.getCode());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            condition.setName(nameField.getText());
            condition.setCode(codeField.getText());

            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "No valid name!\n";
        }
        if (codeField.getText() == null || codeField.getText().trim().isEmpty()) {
            errorMessage += "No valid code!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}
