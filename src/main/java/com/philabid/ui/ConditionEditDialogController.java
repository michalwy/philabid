package com.philabid.ui;

import com.philabid.model.Condition;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for the condition edit dialog.
 */
public class ConditionEditDialogController extends CrudEditDialogController<Condition> {

    private static final Logger logger = LoggerFactory.getLogger(ConditionEditDialogController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;

    @Override
    protected void loadEntity(Condition condition) {
        nameField.setText(condition.getName());
        codeField.setText(condition.getCode());
    }

    @Override
    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.add(new ValidationError("No valid name.", nameField));
        }
        if (codeField.getText() == null || codeField.getText().trim().isEmpty()) {
            errors.add(new ValidationError("No valid code.", codeField));
        }
        return errors;
    }

    @Override
    protected void updateEntity(Condition condition) {
        condition.setName(nameField.getText());
        condition.setCode(codeField.getText());
    }
}
