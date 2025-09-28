package com.philabid.ui;

import com.philabid.model.Catalog;
import com.philabid.model.Currency;
import com.philabid.service.CurrencyService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for the catalog edit dialog.
 */
public class CatalogEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogEditDialogController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField issueYearField;
    @FXML
    private ComboBox<Currency> currencyComboBox;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private Catalog catalog;
    private CurrencyService currencyService;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        logger.debug("CatalogEditDialogController initialized.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setServices(CurrencyService currencyService) {
        this.currencyService = currencyService;
        populateCurrencyComboBox();
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;

        nameField.setText(catalog.getName());
        if (catalog.getIssueYear() != null) {
            issueYearField.setText(catalog.getIssueYear().toString());
        }
        activeCheckBox.setSelected(catalog.isActive());

        if (catalog.getCurrencyCode() != null && currencyComboBox.getItems() != null) {
            currencyComboBox.getItems().stream()
                    .filter(c -> c.getCode().equals(catalog.getCurrencyCode()))
                    .findFirst()
                    .ifPresent(currencyComboBox.getSelectionModel()::select);
        }
    }

    private void populateCurrencyComboBox() {
        if (currencyService != null) {
            List<Currency> currencies = currencyService.getAllCurrencies();
            currencyComboBox.setItems(FXCollections.observableArrayList(currencies));
            logger.debug("Populated currency ComboBox with {} items.", currencies.size());
        } else {
            logger.warn("CurrencyService is not available. Cannot populate currency ComboBox.");
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            catalog.setName(nameField.getText());
            try {
                catalog.setIssueYear(Integer.parseInt(issueYearField.getText()));
            } catch (NumberFormatException e) {
                catalog.setIssueYear(null); // Or handle error
            }
            
            Currency selectedCurrency = currencyComboBox.getSelectionModel().getSelectedItem();
            if (selectedCurrency != null) {
                catalog.setCurrencyCode(selectedCurrency.getCode());
            }
            catalog.setActive(activeCheckBox.isSelected());

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
        if (issueYearField.getText() != null && !issueYearField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(issueYearField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Issue year must be a valid number!\n";
            }
        }
        if (currencyComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "No currency selected!\n";
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
