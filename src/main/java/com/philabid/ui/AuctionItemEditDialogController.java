package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.AuctionItem;
import com.philabid.model.Catalog;
import com.philabid.model.Category;
import com.philabid.service.CatalogService;
import com.philabid.service.CategoryService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the auction item edit dialog.
 */
public class AuctionItemEditDialogController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemEditDialogController.class);
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextField catalogNumberField;
    @FXML
    private TextField orderNumberField;
    @FXML
    private TextArea notesArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button saveAndAddAnotherButton;
    @FXML
    private Button cancelButton;
    private Stage dialogStage;
    private AuctionItem auctionItem;
    private CategoryService categoryService;
    private CatalogService catalogService;
    private I18nManager i18nManager;
    private boolean saveClicked = false;
    private boolean addAnother = false;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            catalogNumberField.requestFocus();
        });

        catalogNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateOrderNumberFromCatalogNumber(newValue);
        });

        logger.debug("AuctionItemEditDialogController initialized.");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setServices(CategoryService categoryService, CatalogService catalogService, I18nManager i18nManager) {
        this.categoryService = categoryService;
        this.catalogService = catalogService;
        this.i18nManager = i18nManager;
        populateCategoryComboBox();
    }

    public void setAuctionItem(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;

        catalogNumberField.setText(auctionItem.getCatalogNumber());
        orderNumberField.setText(String.valueOf(auctionItem.getOrderNumber()));
        notesArea.setText(auctionItem.getNotes());

        if (auctionItem.getCategoryId() != null && categoryComboBox.getItems() != null) {
            categoryComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(auctionItem.getCategoryId()))
                    .findFirst()
                    .ifPresent(categoryComboBox.getSelectionModel()::select);
        }
    }

    private void populateCategoryComboBox() {
        if (categoryService != null) {
            List<Category> categories = categoryService.getAllCategories();
            categoryComboBox.setItems(FXCollections.observableArrayList(categories));
            logger.debug("Populated category ComboBox with {} items.", categories.size());
        } else {
            logger.warn("CategoryService is not available. Cannot populate category ComboBox.");
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private boolean handleSaveCommon() {
        if (!isInputValid()) {
            return false;
        }
        auctionItem.setCatalogNumber(catalogNumberField.getText());
        auctionItem.setOrderNumber(Long.parseLong(orderNumberField.getText()));
        auctionItem.setNotes(notesArea.getText());

        Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            auctionItem.setCategoryId(selectedCategory.getId());
            // Set catalog info from selected category's catalog
            Catalog associatedCatalog =
                    catalogService.getCatalogById(selectedCategory.getCatalogId()).orElse(null);
            if (associatedCatalog != null) {
                auctionItem.setCatalogName(associatedCatalog.getName());
                auctionItem.setCatalogIssueYear(associatedCatalog.getIssueYear());
            } else {
                auctionItem.setCatalogName(null);
                auctionItem.setCatalogIssueYear(null);
            }
        } else {
            auctionItem.setCategoryId(null);
            auctionItem.setCatalogName(null);
            auctionItem.setCatalogIssueYear(null);
        }

        return true;
    }

    @FXML
    private void handleSave() {
        if (handleSaveCommon()) {
            saveClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleSaveAndAddAnother() {
        if (handleSaveCommon()) {
            saveClicked = true;
            addAnother = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        ResourceBundle bundle = i18nManager != null ? i18nManager.getResourceBundle() : null;

        if (categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage += (bundle != null ? bundle.getString("auctionItems.validation.noCategorySelected") : "No " +
                    "category selected!") + "\n";
        }
        if (catalogNumberField.getText() == null || catalogNumberField.getText().trim().isEmpty()) {
            errorMessage += (bundle != null ? bundle.getString("auctionItems.validation.noCatalogNumber") : "No valid" +
                    " catalog number!") + "\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(bundle != null ? bundle.getString("common.error.title") : "Error");
            alert.setHeaderText(bundle != null ? bundle.getString("common.error.header") : "Please correct invalid " +
                    "fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    public boolean shouldAddAnother() {
        return addAnother;
    }

    /**
     * Extracts the first sequence of digits from the catalog number and sets it as the order number.
     * This is useful for sorting. For example, "Mi. 456a" -> "456".
     *
     * @param catalogNumber The text from the catalog number field.
     */
    private void updateOrderNumberFromCatalogNumber(String catalogNumber) {
        orderNumberField.setText(AuctionItem.calculateOrderNumber(catalogNumber).toString());
    }
}
