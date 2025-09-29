package com.philabid.ui;

import com.philabid.model.AuctionHouse;
import com.philabid.model.Currency;
import com.philabid.service.CurrencyService;
import javafx.application.Platform;
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
 * Controller for the auction house edit dialog.
 * Handles the logic for creating and editing an AuctionHouse entity.
 */
public class AuctionHouseEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionHouseEditDialogController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField websiteField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField countryField;
    @FXML
    private ComboBox<Currency> currencyComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private AuctionHouse auctionHouse;
    private CurrencyService currencyService;
    private boolean saveClicked = false;

    @FXML
    private void initialize() {
        Platform.runLater(() -> nameField.requestFocus());
        logger.debug("AuctionHouseEditDialogController initialized.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAuctionHouse(AuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;

        nameField.setText(auctionHouse.getName());
        websiteField.setText(auctionHouse.getWebsite());
        emailField.setText(auctionHouse.getContactEmail());
        phoneField.setText(auctionHouse.getContactPhone());
        addressField.setText(auctionHouse.getAddress());
        countryField.setText(auctionHouse.getCountry());

        // Select the current currency in the ComboBox
        if (auctionHouse.getCurrency() != null && currencyComboBox.getItems() != null) {
            currencyComboBox.getItems().stream()
                    .filter(c -> c.getCode().equals(auctionHouse.getCurrency()))
                    .findFirst()
                    .ifPresent(currencyComboBox.getSelectionModel()::select);
        }
    }

    public void setServices(CurrencyService currencyService) {
        this.currencyService = currencyService;
        populateCurrencyComboBox();
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
            auctionHouse.setName(nameField.getText());
            auctionHouse.setWebsite(websiteField.getText());
            auctionHouse.setContactEmail(emailField.getText());
            auctionHouse.setContactPhone(phoneField.getText());
            auctionHouse.setAddress(addressField.getText());
            auctionHouse.setCountry(countryField.getText());

            Currency selectedCurrency = currencyComboBox.getSelectionModel().getSelectedItem();
            if (selectedCurrency != null) {
                auctionHouse.setCurrency(selectedCurrency.getCode());
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
