package com.philabid.ui;

import com.philabid.AppContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class PreferencesDialogController {

    private static final Logger logger = LoggerFactory.getLogger(PreferencesDialogController.class);

    @FXML
    private ComboBox<CurrencyUnit> defaultCurrencyComboBox;

    private Stage dialogStage;

    @FXML
    private void initialize() {
        // Populate the ComboBox with available currencies
        defaultCurrencyComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getCurrencyService().getCurrencies()));

        // Load and set the current default currency
        String defaultCurrencyCode =
                AppContext.getConfigurationService().getString("application.defaultCurrency", "USD");
        try {
            CurrencyUnit defaultCurrency = Monetary.getCurrency(defaultCurrencyCode);
            defaultCurrencyComboBox.getSelectionModel().select(defaultCurrency);
        } catch (Exception e) {
            logger.warn("Could not find saved default currency '{}'. Falling back.", defaultCurrencyCode);
        }

        logger.debug("PreferencesDialogController initialized.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleSave() {
        CurrencyUnit selectedCurrency = defaultCurrencyComboBox.getSelectionModel().getSelectedItem();
        if (selectedCurrency != null) {
            String newCurrencyCode = selectedCurrency.getCurrencyCode();
            AppContext.getConfigurationService().setValue("application.defaultCurrency", newCurrencyCode);
            AppContext.getConfigurationService().saveConfiguration();
            logger.info("Default currency saved as: {}", newCurrencyCode);
        }
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}