package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Catalog;
import com.philabid.model.Category;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for the category edit dialog.
 */
public class CategoryEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryEditDialogController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;
    @FXML
    private ComboBox<Catalog> catalogComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private Category category;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        populateCatalogComboBox();
        logger.debug("CategoryEditDialogController initialized.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCategory(Category category) {
        this.category = category;

        nameField.setText(category.getName());
        codeField.setText(category.getCode());

        if (category.getCatalogId() != null && catalogComboBox.getItems() != null) {
            catalogComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(category.getCatalogId()))
                    .findFirst()
                    .ifPresent(catalogComboBox.getSelectionModel()::select);
        }
    }

    private void populateCatalogComboBox() {
        List<Catalog> catalogs = AppContext.getCatalogService().getAllCatalogs();
        catalogComboBox.setItems(FXCollections.observableArrayList(catalogs));
        logger.debug("Populated catalog ComboBox with {} items.", catalogs.size());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            category.setName(nameField.getText());
            category.setCode(codeField.getText());

            Catalog selectedCatalog = catalogComboBox.getSelectionModel().getSelectedItem();
            if (selectedCatalog != null) {
                category.setCatalogId(selectedCatalog.getId());
            } else {
                category.setCatalogId(null);
            }

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

        if (errorMessage.isEmpty()) {
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
