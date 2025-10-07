package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.AuctionItem;
import com.philabid.model.Category;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * A custom control for selecting an existing AuctionItem or defining a new one.
 * It encapsulates a TextField with autocompletion and a ComboBox for category selection.
 */
public class AuctionItemSelector extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemSelector.class);
    private final ObjectProperty<AuctionItem> selectedAuctionItem = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Category> selectedCategory = new SimpleObjectProperty<>(null);
    @FXML
    private TextField auctionItemField;
    @FXML
    private ComboBox<Category> categoryComboBox;

    public AuctionItemSelector() {
    }

    public void load(ResourceBundle resources) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/AuctionItemSelector.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(resources); // Pass the resource bundle

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        auctionItemField.textProperty().addListener((obs, oldVal, newVal) -> handleAuctionItemTextChange(newVal));

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCategory.set(newVal);
        });

        populateComboBoxes();
        setupAuctionItemAutocomplete();
    }

    public String getText() {
        return auctionItemField.getText();
    }

    public void setText(String text) {
        auctionItemField.setText(text);
    }

    public AuctionItem getSelectedAuctionItem() {
        return selectedAuctionItem.get();
    }

    public void setSelectedAuctionItem(AuctionItem auctionItem) {
        this.selectedAuctionItem.set(auctionItem);
        if (auctionItem != null) {
            auctionItemField.setText(auctionItem.getCatalogNumber());
            autoSelectCategory(auctionItem);
            categoryComboBox.setDisable(true);
        }
    }

    public ObjectProperty<AuctionItem> selectedAuctionItemProperty() {
        return selectedAuctionItem;
    }

    public ObjectProperty<Category> selectedCategoryProperty() {
        return selectedCategory;
    }

    public Category getSelectedCategory() {
        return categoryComboBox.getSelectionModel().getSelectedItem();
    }

    public void requestFocus() {
        Platform.runLater(() -> auctionItemField.requestFocus());
    }

    private void populateComboBoxes() {
        categoryComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getCategoryService().getAllCategories()));
    }

    private void setupAuctionItemAutocomplete() {
        AutoCompletionBinding<AuctionItemSuggestion> binding = TextFields.bindAutoCompletion(auctionItemField,
                suggestionRequest -> {
                    String filter = suggestionRequest.getUserText().toLowerCase();
                    List<AuctionItem> allItems = AppContext.getAuctionItemService().getAllAuctionItems();
                    return allItems.stream()
                            .filter(item -> item.getCatalogNumber().toLowerCase().contains(filter) ||
                                    (item.getCategoryName() != null &&
                                            item.getCategoryName().toLowerCase().contains(filter)))
                            .map(AuctionItemSuggestion::new)
                            .collect(Collectors.toList());
                });

        binding.setOnAutoCompleted(event -> {
            this.selectedAuctionItem.set(event.getCompletion().item());
            auctionItemField.setText(this.selectedAuctionItem.get().getCatalogNumber());
            autoSelectCategory(this.selectedAuctionItem.get());
            categoryComboBox.setDisable(true);
        });
    }

    private void autoSelectCategory(AuctionItem auctionItem) {
        if (auctionItem != null && auctionItem.getCategoryId() != null) {
            AppContext.getCategoryService().getCategoryById(auctionItem.getCategoryId())
                    .ifPresentOrElse(category -> {
                                categoryComboBox.getSelectionModel().select(category);
                                selectedCategory.set(category);
                            },
                            () -> categoryComboBox.getSelectionModel().select(null));
        }
    }

    private void handleAuctionItemTextChange(String newText) {
        if (selectedAuctionItem.get() != null &&
                (newText == null || !newText.equals(selectedAuctionItem.get().getCatalogNumber()))) {
            selectedAuctionItem.set(null);
            categoryComboBox.setDisable(false);
        }
        categoryComboBox.setDisable(selectedAuctionItem.get() != null || newText == null || newText.isBlank());
    }

    /**
     * Resolves the AuctionItem ID. If an item was selected, its ID is returned.
     * If a new item was defined, it is created in the database and its new ID is returned.
     *
     * @return The ID of the existing or newly created AuctionItem.
     */
    public Long resolveAuctionItemId() {
        if (selectedAuctionItem.get() != null) {
            return selectedAuctionItem.get().getId();
        } else {
            AuctionItem newAuctionItem = new AuctionItem();
            newAuctionItem.setCatalogNumber(auctionItemField.getText());
            newAuctionItem.setOrderNumber(AuctionItem.calculateOrderNumber(auctionItemField.getText()));
            newAuctionItem.setCategoryId(categoryComboBox.getSelectionModel().getSelectedItem().getId());
            AppContext.getAuctionItemService().saveAuctionItem(newAuctionItem);
            logger.info("Created new AuctionItem with ID: {}", newAuctionItem.getId());
            return newAuctionItem.getId();
        }
    }
}