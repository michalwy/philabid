package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Catalog;
import com.philabid.model.CatalogValue;
import com.philabid.model.Category;
import com.philabid.model.Condition;
import com.philabid.ui.control.AuctionItemSelector;
import com.philabid.ui.control.MonetaryField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;

/**
 * Controller for the catalog value edit dialog.
 */
public class CatalogValueEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueEditDialogController.class);
    @FXML
    private AuctionItemSelector auctionItemSelector;
    @FXML
    private ComboBox<Condition> conditionComboBox;
    @FXML
    private ComboBox<Catalog> catalogComboBox;
    @FXML
    private MonetaryField valueField;
    @FXML
    private ComboBox<CurrencyUnit> currencyComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private Stage dialogStage;
    private CatalogValue catalogValue;
    private EditDialogResult editDialogResult;

    @FXML
    private void initialize() {
        logger.debug("CatalogValueEditDialogController initialized.");

        Platform.runLater(() -> auctionItemSelector.requestFocus());

        catalogComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currencyComboBox.getSelectionModel().select(newVal.getCurrency());
            }
        });
        conditionComboBox.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if (wasShowing && !isNowShowing) {
                Platform.runLater(() -> valueField.requestFocus());
            }
        });

        auctionItemSelector.selectedCategoryProperty()
                .addListener((obs, oldVal, newVal) -> autoSelectCatalogAndCurrency(newVal));
        auctionItemSelector.load(AppContext.getI18nManager().getResourceBundle());

        populateComboBoxes();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCatalogValue(CatalogValue catalogValue) {
        this.catalogValue = catalogValue;

        if (catalogValue.getValue() != null) {
            valueField.setAmount(catalogValue.getValue());
        }

        // Pre-select items in ComboBoxes if editing an existing value
        if (catalogValue.getAuctionItemId() != null) {
            AppContext.getAuctionItemService().getAuctionItemById(catalogValue.getAuctionItemId())
                    .ifPresent(auctionItemSelector::setSelectedAuctionItem);
        }
        if (catalogValue.getConditionId() != null) {
            conditionComboBox.getItems().stream().filter(c -> c.getId().equals(catalogValue.getConditionId()))
                    .findFirst().ifPresent(conditionComboBox.getSelectionModel()::select);
        }
        if (catalogValue.getCatalogId() != null) {
            catalogComboBox.getSelectionModel()
                    .select(AppContext.getCatalogService().getCatalogById(catalogValue.getCatalogId()).orElse(null));
        }
        if (catalogValue.getValue() != null) {
            currencyComboBox.getSelectionModel().select(catalogValue.getValue().getCurrency());
        }
    }

    private void populateComboBoxes() {
        conditionComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getConditionService().getAllConditions()));
        catalogComboBox.setItems(FXCollections.observableArrayList(AppContext.getCatalogService().getAllCatalogs()));
        currencyComboBox.setItems(FXCollections.observableArrayList(AppContext.getCurrencyService().getCurrencies()));
    }

    private void autoSelectCatalogAndCurrency(Category category) {
        if (category != null && category.getCatalogId() != null) {
            AppContext.getCatalogService().getCatalogById(category.getCatalogId()).ifPresent(defaultCatalog -> {
                catalogComboBox.getSelectionModel().select(defaultCatalog);
                // Auto-select currency based on catalog
                if (defaultCatalog.getCurrency() != null) {
                    currencyComboBox.getItems().stream()
                            .filter(c -> c.equals(defaultCatalog.getCurrency()))
                            .findFirst()
                            .ifPresent(currencyComboBox.getSelectionModel()::select);
                }
            });
        }
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            catalogValue.setAuctionItemId(auctionItemSelector.resolveAuctionItemId());

            catalogValue.setConditionId(conditionComboBox.getSelectionModel().getSelectedItem().getId());
            catalogValue.setCatalogId(catalogComboBox.getSelectionModel().getSelectedItem().getId());
            try {
                catalogValue.setValue(Money.of(valueField.getAmount(),
                        currencyComboBox.getSelectionModel().getSelectedItem()));
            } catch (NumberFormatException e) {
                // This should be caught by isInputValid(), but as a safeguard:
                logger.error("Invalid number format for value field: {}", valueField.getText());
                return;
            }

            editDialogResult = new EditDialogResult(true, false);

            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (auctionItemSelector.getText() == null || auctionItemSelector.getText().isBlank()) {
            errorMessage.append("Catalog Number cannot be empty!\n");
        }

        // If it's a new item, a category must be selected
        if (auctionItemSelector.getSelectedAuctionItem() == null && auctionItemSelector.getSelectedCategory() == null) {
            errorMessage.append("Category must be selected for a new catalog number!\n");
        }
        if (conditionComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Condition must be selected!\n");
        }
        if (catalogComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Catalog must be selected!\n");
        }
        if (currencyComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage.append("Currency must be selected!\n");
        }
        if (valueField.isEmpty()) {
            errorMessage.append("Value cannot be empty!\n");
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(AppContext.getI18nManager().getString("common.invalidFields"));
            alert.setHeaderText(AppContext.getI18nManager().getString("common.correctInvalidFields"));
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }
    }

    public EditDialogResult getEditDialogResult() {
        return editDialogResult;
    }
}
