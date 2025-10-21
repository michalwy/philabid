package com.philabid.ui;

import com.philabid.AppContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public class PreferencesDialogController {

    private static final Logger logger = LoggerFactory.getLogger(PreferencesDialogController.class);

    @FXML
    private ComboBox<CurrencyUnit> defaultCurrencyComboBox;
    @FXML
    private Spinner<Integer> analysisDaysSpinner;
    @FXML
    private TextField allegroClientIdField;

    private Stage dialogStage;

    @FXML
    private void initialize() {
        // Populate the ComboBox with available currencies
        defaultCurrencyComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getCurrencyService().getCurrencies()));

        // Configure the Spinner for analysis days
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3650, 365); // Min 1 day, max 10 years, default 90
        analysisDaysSpinner.setValueFactory(valueFactory);
        analysisDaysSpinner.getEditor()
                .setText(Integer.toString(AppContext.getConfigurationService().getRecommendationAnalysisDays()));

        allegroClientIdField.setText(AppContext.getConfigurationService().getString("allegro.clientId", ""));

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

        int analysisDays = analysisDaysSpinner.getValue();
        AppContext.getConfigurationService().setValue("auction.recommendationAnalysisDays", analysisDays);
        logger.info("Recommendation analysis days saved as: {}", analysisDays);

        saveAllegroKeys();

        AppContext.getConfigurationService().saveConfiguration();
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

    private void saveAllegroKeys() {
        AppContext.getConfigurationService().setValue("allegro.clientId", allegroClientIdField.getText());
        logger.info("Allegro API credentials saved.");
    }

    @FXML
    private void handleAllegroAuth() {
        saveAllegroKeys();
        AppContext.getConfigurationService().saveConfiguration();

        logger.info("Starting Allegro authorization flow...");
        AppContext.getAllegroApiService().authorize().whenComplete((success, error) -> {
            Platform.runLater(() -> {
                if (error != null || !success) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Authorization Failed");
                    alert.setHeaderText("Could not obtain authorization. Please check logs for details.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Authorization Successful");
                    alert.setHeaderText("The application is now authorized to access your Allegro account.");
                    alert.showAndWait();
                }
            });
        });
    }
}