package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionItem;
import com.philabid.model.Catalog;
import com.philabid.model.Category;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the auction item edit dialog.
 */
public class AuctionItemEditDialogController extends CrudEditDialogController<AuctionItem> {

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

    @Override
    protected void initContent() {
        Platform.runLater(() -> {
            catalogNumberField.requestFocus();
        });

        catalogNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateOrderNumberFromCatalogNumber(newValue);
        });

        populateCategoryComboBox();
        logger.debug("AuctionItemEditDialogController initialized.");
    }

    private void populateCategoryComboBox() {
        List<Category> categories = AppContext.getCategoryService().getAll();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        logger.debug("Populated category ComboBox with {} items.", categories.size());
    }

    @Override
    public void loadEntity(AuctionItem auctionItem) {
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

    @Override
    protected void updateEntity(AuctionItem auctionItem) {
        auctionItem.setCatalogNumber(catalogNumberField.getText());
        auctionItem.setOrderNumber(Long.parseLong(orderNumberField.getText()));
        auctionItem.setNotes(notesArea.getText());

        Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            auctionItem.setCategoryId(selectedCategory.getId());
            // Set catalog info from selected category's catalog
            Catalog associatedCatalog =
                    AppContext.getCatalogService().getCatalogById(selectedCategory.getCatalogId()).orElse(null);
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
    }

    @Override
    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        ResourceBundle bundle = AppContext.getI18nManager().getResourceBundle();

        if (categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError(
                    (bundle != null ? bundle.getString("auctionItems.validation.noCategorySelected") : "No " +
                            "category selected."), categoryComboBox));
        }
        if (catalogNumberField.getText() == null || catalogNumberField.getText().trim().isEmpty()) {
            errors.add(new ValidationError(
                    (bundle != null ? bundle.getString("auctionItems.validation.noCatalogNumber") : "No valid" +
                            " catalog number."), catalogNumberField));
        }

        return errors;
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
