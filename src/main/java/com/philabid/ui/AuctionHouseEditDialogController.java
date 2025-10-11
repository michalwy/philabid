package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionHouse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for the auction house edit dialog.
 * Handles the logic for creating and editing an AuctionHouse entity.
 */
public class AuctionHouseEditDialogController extends CrudEditDialogController<AuctionHouse> {

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
    private ComboBox<CurrencyUnit> currencyComboBox;

    @Override
    protected void initContent() {
        Platform.runLater(() -> nameField.requestFocus());
        logger.debug("AuctionHouseEditDialogController initialized.");
        populateCurrencyComboBox();
    }

    @Override
    protected void loadEntity(AuctionHouse auctionHouse) {
        nameField.setText(auctionHouse.getName());
        websiteField.setText(auctionHouse.getWebsite());
        emailField.setText(auctionHouse.getContactEmail());
        phoneField.setText(auctionHouse.getContactPhone());
        addressField.setText(auctionHouse.getAddress());
        countryField.setText(auctionHouse.getCountry());

        // Select the current currency in the ComboBox
        if (auctionHouse.getCurrency() != null && currencyComboBox.getItems() != null) {
            currencyComboBox.getSelectionModel().select(auctionHouse.getCurrency());
        }
    }

    private void populateCurrencyComboBox() {
        currencyComboBox.setItems(FXCollections.observableArrayList(AppContext.getCurrencyService().getCurrencies()));
        logger.debug("Populated currency ComboBox with {} items.", currencyComboBox.getItems().size());
    }

    @Override
    protected void updateEntity(AuctionHouse auctionHouse) {
        auctionHouse.setName(nameField.getText());
        auctionHouse.setWebsite(websiteField.getText());
        auctionHouse.setContactEmail(emailField.getText());
        auctionHouse.setContactPhone(phoneField.getText());
        auctionHouse.setAddress(addressField.getText());
        auctionHouse.setCountry(countryField.getText());

        CurrencyUnit selectedCurrency = currencyComboBox.getSelectionModel().getSelectedItem();
        auctionHouse.setCurrency(selectedCurrency);
    }

    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.add(new ValidationError("Name cannot be empty.", nameField));
        }
        if (currencyComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError("Currency must be selected.", currencyComboBox));
        }
        return errors;
    }
}
