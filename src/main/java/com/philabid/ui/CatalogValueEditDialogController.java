package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.*;
import com.philabid.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller for the catalog value edit dialog.
 */
public class CatalogValueEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueEditDialogController.class);
    @FXML
    private TextField auctionItemField;
    @FXML
    private ComboBox<Category> categoryComboBox;
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
    private Map<Long, Category> categoryCache;
    private Map<Long, Catalog> catalogCache;
    private AuctionItem selectedAuctionItem;

    @FXML
    private void initialize() {
        logger.debug("CatalogValueEditDialogController initialized.");

        Platform.runLater(() -> auctionItemField.requestFocus());

        // Add a listener to the auction item selection to auto-select catalog and currency
        auctionItemField.textProperty().addListener((obs, oldVal, newVal) -> handleAuctionItemTextChange(newVal));

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                autoSelectCatalogAndCurrency(newVal);
            }
        });

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

        // Populate caches
        this.categoryCache =
                this.categoryService.getAllCategories().stream().collect(Collectors.toMap(Category::getId,
                        Function.identity()));
        this.catalogCache = this.catalogService.getAllCatalogs().stream().collect(Collectors.toMap(Catalog::getId,
                Function.identity()));

        populateComboBoxes();
    }

    public void setCatalogValue(CatalogValue catalogValue) {
        this.catalogValue = catalogValue;

        if (catalogValue.getValue() != null) {
            valueField.setText(catalogValue.getValue().getNumber().toString());
        }

        // Pre-select items in ComboBoxes if editing an existing value
        if (catalogValue.getAuctionItemId() != null) {
            auctionItemService.getAuctionItemById(catalogValue.getAuctionItemId()).ifPresent(item -> {
                this.selectedAuctionItem = item;
                auctionItemField.setText(item.getCatalogNumber());
                autoSelectCatalogAndCurrency(item);
            });
        }
        if (catalogValue.getConditionId() != null) {
            conditionComboBox.getItems().stream().filter(c -> c.getId().equals(catalogValue.getConditionId()))
                    .findFirst().ifPresent(conditionComboBox.getSelectionModel()::select);
        }
        if (catalogValue.getCatalogId() != null) {
            catalogCache.values().stream().filter(c -> c.getId().equals(catalogValue.getCatalogId()))
                    .findFirst().ifPresent(catalogComboBox.getSelectionModel()::select);
        }
        if (catalogValue.getValue() != null) {
            currencyComboBox.getSelectionModel().select(catalogValue.getValue().getCurrency());
        }
    }

    private void populateComboBoxes() {
        setupAuctionItemAutocomplete();
        categoryComboBox.setItems(FXCollections.observableArrayList(categoryService.getAllCategories()));
        conditionComboBox.setItems(FXCollections.observableArrayList(conditionService.getAllConditions()));
        catalogComboBox.setItems(FXCollections.observableArrayList(catalogService.getAllCatalogs()));
        currencyComboBox.setItems(FXCollections.observableArrayList(currencyService.getCurrencies()));
    }

    private void autoSelectCatalogAndCurrency(AuctionItem item) {
        if (item.getCategoryId() == null || categoryCache == null || catalogCache == null) return;

        Category category = categoryCache.get(item.getCategoryId());
        autoSelectCatalogAndCurrency(category);
    }

    private void autoSelectCatalogAndCurrency(Category category) {
        if (category != null && category.getCatalogId() != null) {
            Catalog defaultCatalog = catalogCache.get(category.getCatalogId());
            if (defaultCatalog != null) {
                catalogComboBox.getSelectionModel().select(defaultCatalog);
                // Auto-select currency based on catalog
                if (defaultCatalog.getCurrency() != null) {
                    currencyComboBox.getItems().stream()
                            .filter(c -> c.equals(defaultCatalog.getCurrency()))
                            .findFirst()
                            .ifPresent(currencyComboBox.getSelectionModel()::select);
                }
            }
        }
    }

    private void setupAuctionItemAutocomplete() {
        AutoCompletionBinding<AuctionItemSuggestion> binding = TextFields.bindAutoCompletion(auctionItemField,
                suggestionRequest -> {
                    String filter = suggestionRequest.getUserText().toLowerCase();
                    // Search in the original items and wrap them in our suggestion class
                    return auctionItemService.getAllAuctionItems().stream()
                            .filter(item -> item.getCatalogNumber().toLowerCase().contains(filter))
                            .map(AuctionItemSuggestion::new) // Wrap in suggestion
                            .collect(Collectors.toList());
                });

        // 3. Set the action to perform when an item is selected
        binding.setOnAutoCompleted(event -> {
            // Unwrap the original AuctionItem from our suggestion wrapper
            this.selectedAuctionItem = event.getCompletion().item();
            auctionItemField.setText(this.selectedAuctionItem.getCatalogNumber());
            categoryComboBox.getSelectionModel().select(categoryCache.get(this.selectedAuctionItem.getCategoryId()));
            autoSelectCatalogAndCurrency(this.selectedAuctionItem);
            categoryComboBox.setDisable(true);
            Platform.runLater(() -> {
                conditionComboBox.requestFocus();
                conditionComboBox.show();
            });
        });
    }

    private void handleAuctionItemTextChange(String newText) {
        // If text is cleared or doesn't match a selected item, reset the state
        if (selectedAuctionItem != null &&
                (newText == null || !newText.equals(selectedAuctionItem.getCatalogNumber()))) {
            selectedAuctionItem = null;
            // Enable category selection for a new item
            categoryComboBox.setDisable(false);
            categoryComboBox.getSelectionModel().clearSelection();
            catalogComboBox.getSelectionModel().clearSelection();
        }

        // If text is not empty and no item is selected, it's a new item
        categoryComboBox.setDisable(selectedAuctionItem != null || newText == null || newText.isBlank());
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            // Scenario 1: Existing item was selected
            if (selectedAuctionItem != null) {
                catalogValue.setAuctionItemId(selectedAuctionItem.getId());
            } else {
                // Scenario 2: New item needs to be created
                AuctionItem newAuctionItem = new AuctionItem();
                newAuctionItem.setCatalogNumber(auctionItemField.getText());
                newAuctionItem.setOrderNumber(AuctionItem.calculateOrderNumber(auctionItemField.getText()));
                newAuctionItem.setCategoryId(categoryComboBox.getSelectionModel().getSelectedItem().getId());
                auctionItemService.saveAuctionItem(newAuctionItem); // This should set the ID
                catalogValue.setAuctionItemId(newAuctionItem.getId());
                logger.info("Created new AuctionItem with ID: {}", newAuctionItem.getId());
            }

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

        if (auctionItemField.getText() == null || auctionItemField.getText().isBlank()) {
            errorMessage.append("Catalog Number cannot be empty!\n");
        }

        // If it's a new item, a category must be selected
        if (selectedAuctionItem == null && categoryComboBox.getSelectionModel().getSelectedItem() == null) {
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

    /**
     * A private wrapper class to control the text displayed in the autocompletion popup.
     * The text field will use the StringConverter, but the popup list will use this class's toString() method.
     */
    private record AuctionItemSuggestion(AuctionItem item) {

        @Override
        public String toString() {
            return item().getCategoryCode() + " " + item().getCatalogNumber();
        }
    }
}
