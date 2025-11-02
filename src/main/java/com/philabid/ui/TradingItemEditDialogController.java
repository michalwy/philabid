package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Catalog;
import com.philabid.model.Category;
import com.philabid.model.TradingItem;
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

public class TradingItemEditDialogController extends CrudEditDialogController<TradingItem> {

    private static final Logger logger = LoggerFactory.getLogger(TradingItemEditDialogController.class);
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
    }

    private void populateCategoryComboBox() {
        Collection<Category> categories = AppContext.getCategoryService().getAll();
        categoryComboBox.setItems(FXCollections.observableArrayList(categories));
        logger.debug("Populated category ComboBox with {} items.", categories.size());
    }

    @Override
    public void loadEntity(TradingItem tradingItem) {
        catalogNumberField.setText(tradingItem.getCatalogNumber());
        orderNumberField.setText(String.valueOf(tradingItem.getOrderNumber()));
        notesArea.setText(tradingItem.getNotes());

        if (tradingItem.getCategoryId() != null && categoryComboBox.getItems() != null) {
            categoryComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(tradingItem.getCategoryId()))
                    .findFirst()
                    .ifPresent(categoryComboBox.getSelectionModel()::select);
        }
    }

    @Override
    protected void updateEntity(TradingItem tradingItem) {
        tradingItem.setCatalogNumber(catalogNumberField.getText());
        tradingItem.setOrderNumber(Long.parseLong(orderNumberField.getText()));
        tradingItem.setNotes(notesArea.getText());

        Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            tradingItem.setCategoryId(selectedCategory.getId());
            // Set catalog info from selected category's catalog
            Catalog associatedCatalog =
                    AppContext.getCatalogService().getById(selectedCategory.getCatalogId()).orElse(null);
            if (associatedCatalog != null) {
                tradingItem.setCatalogName(associatedCatalog.getName());
                tradingItem.setCatalogIssueYear(associatedCatalog.getIssueYear());
            } else {
                tradingItem.setCatalogName(null);
                tradingItem.setCatalogIssueYear(null);
            }
        } else {
            tradingItem.setCategoryId(null);
            tradingItem.setCatalogName(null);
            tradingItem.setCatalogIssueYear(null);
        }
    }

    @Override
    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        ResourceBundle bundle = AppContext.getI18nManager().getResourceBundle();

        if (categoryComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError(
                    (bundle != null ? bundle.getString("tradingItems.validation.noCategorySelected") : "No " +
                            "category selected."), categoryComboBox));
        }
        if (catalogNumberField.getText() == null || catalogNumberField.getText().trim().isEmpty()) {
            errors.add(new ValidationError(
                    (bundle != null ? bundle.getString("tradingItems.validation.noCatalogNumber") : "No valid" +
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
        orderNumberField.setText(TradingItem.calculateOrderNumber(catalogNumber).toString());
    }
}
