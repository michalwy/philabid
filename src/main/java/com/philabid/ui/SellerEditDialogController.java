package com.philabid.ui;

import com.philabid.model.Seller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SellerEditDialogController extends CrudEditDialogController<Seller> {
    @FXML
    private TextField nameField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;

    @Override
    protected void initContent() {
        Platform.runLater(() -> nameField.requestFocus());
    }

    @Override
    protected void loadEntity(Seller seller) {
        nameField.setText(seller.getName());
        fullNameField.setText(seller.getFullName());
        emailField.setText(seller.getContactEmail());
        phoneField.setText(seller.getContactPhone());
    }

    @Override
    protected void updateEntity(Seller seller) {
        seller.setName(nameField.getText());
        seller.setFullName(fullNameField.getText());
        seller.setContactEmail(emailField.getText());
        seller.setContactPhone(phoneField.getText());
    }

    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.add(new ValidationError("Name cannot be empty.", nameField));
        }
        return errors;
    }
}
