package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.Catalog;
import com.philabid.model.CatalogValue;
import com.philabid.model.Category;
import com.philabid.model.Condition;
import com.philabid.service.*;
import com.philabid.ui.control.AuctionItemSelector;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;

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
    private TextField valueField;
    @FXML
    private ComboBox<CurrencyUnit> currencyComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private Stage dialogStage;
    private CatalogValue catalogValue;
    private AuctionItemService auctionItemService;
    private ConditionService conditionService;
    private CatalogService catalogService;
    private CategoryService categoryService;
    private I18nManager i18nManager;
    private CurrencyService currencyService;
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
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setServices(CurrencyService currencyService, AuctionItemService auctionItemService,
                            ConditionService conditionService,
                            CatalogService catalogService, CategoryService categoryService, I18nManager i18nManager) {
        this.currencyService = currencyService;
        this.auctionItemService = auctionItemService;
        this.conditionService = conditionService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
        this.i18nManager = i18nManager;

        auctionItemSelector.load(i18nManager.getResourceBundle());
        auctionItemSelector.setServices(auctionItemService, categoryService);

        populateComboBoxes();
    }

    public void setCatalogValue(CatalogValue catalogValue) {
        this.catalogValue = catalogValue;

        if (catalogValue.getValue() != null) {
            valueField.setText(catalogValue.getValue().getNumber().toString());
        }

        // Pre-select items in ComboBoxes if editing an existing value
        if (catalogValue.getAuctionItemId() != null) {
            auctionItemService.getAuctionItemById(catalogValue.getAuctionItemId())
                    .ifPresent(auctionItemSelector::setSelectedAuctionItem);
        }
        if (catalogValue.getConditionId() != null) {
            conditionComboBox.getItems().stream().filter(c -> c.getId().equals(catalogValue.getConditionId()))
                    .findFirst().ifPresent(conditionComboBox.getSelectionModel()::select);
        }
        if (catalogValue.getCatalogId() != null) {
            catalogComboBox.getSelectionModel()
                    .select(catalogService.getCatalogById(catalogValue.getCatalogId()).orElse(null));
        }
        if (catalogValue.getValue() != null) {
            currencyComboBox.getSelectionModel().select(catalogValue.getValue().getCurrency());
        }
    }

    private void populateComboBoxes() {
        conditionComboBox.setItems(FXCollections.observableArrayList(conditionService.getAllConditions()));
        catalogComboBox.setItems(FXCollections.observableArrayList(catalogService.getAllCatalogs()));
        currencyComboBox.setItems(FXCollections.observableArrayList(currencyService.getCurrencies()));
    }

    private void autoSelectCatalogAndCurrency(Category category) {
        if (category != null && category.getCatalogId() != null) {
            catalogService.getCatalogById(category.getCatalogId()).ifPresent(defaultCatalog -> {
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
                catalogValue.setValue(Money.of(new BigDecimal(valueField.getText()),
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
        if (valueField.getText() == null || valueField.getText().trim().isEmpty()) {
            errorMessage.append("Value cannot be empty!\n");
        } else {
            try {
                new BigDecimal(valueField.getText());
            } catch (NumberFormatException e) {
                errorMessage.append("Value must be a valid number!\n");
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(i18nManager.getString("common.invalidFields"));
            alert.setHeaderText(i18nManager.getString("common.correctInvalidFields"));
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }
    }

    public EditDialogResult getEditDialogResult() {
        return editDialogResult;
    }
}
