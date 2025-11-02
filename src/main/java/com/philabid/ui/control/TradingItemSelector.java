package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.Category;
import com.philabid.model.TradingItem;
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
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A custom control for selecting an existing TradingItem or defining a new one.
 * It encapsulates a TextField with autocompletion and a ComboBox for category selection.
 */
public class TradingItemSelector extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(TradingItemSelector.class);
    private final ObjectProperty<TradingItem> selectedTradingItem = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Category> selectedCategory = new SimpleObjectProperty<>(null);
    private final Collection<TradingItem> allItems = AppContext.getTradingItemService().getAll();
    @FXML
    private TextField tradingItemField;
    @FXML
    private ComboBox<Category> categoryComboBox;

    public TradingItemSelector() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/TradingItemSelector.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(AppContext.getI18nManager().getResourceBundle()); // Pass the resource bundle

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        tradingItemField.textProperty().addListener((obs, oldVal, newVal) -> handleTradingItemTextChange(newVal));

        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCategory.set(newVal);
        });

        populateComboBoxes();
        setupTradingItemAutocomplete();
    }

    public String getText() {
        return tradingItemField.getText();
    }

    public void setText(String text) {
        tradingItemField.setText(text);
    }

    public TradingItem getSelectedTradingItem() {
        return selectedTradingItem.get();
    }

    public void setSelectedTradingItem(TradingItem tradingItem) {
        this.selectedTradingItem.set(tradingItem);
        if (tradingItem != null) {
            tradingItemField.setText(tradingItem.getCatalogNumber());
            autoSelectCategory(tradingItem);
            categoryComboBox.setDisable(true);
        }
    }

    public ObjectProperty<TradingItem> selectedTradingItemProperty() {
        return selectedTradingItem;
    }

    public ObjectProperty<Category> selectedCategoryProperty() {
        return selectedCategory;
    }

    public Category getSelectedCategory() {
        return categoryComboBox.getSelectionModel().getSelectedItem();
    }

    public void requestFocus() {
        Platform.runLater(() -> tradingItemField.requestFocus());
    }

    private void populateComboBoxes() {
        categoryComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getCategoryService().getAll()));
    }

    private void setupTradingItemAutocomplete() {
        AutoCompletionBinding<TradingItemSuggestion> binding = TextFields.bindAutoCompletion(tradingItemField,
                suggestionRequest -> {
                    String filter = suggestionRequest.getUserText().toLowerCase();
                    Stream<TradingItem> items = allItems.stream();

                    String[] tokens = filter.trim().toLowerCase().split("\\s+");
                    for (String token : tokens) {
                        items = items.filter(item -> item.getCatalogNumber().toLowerCase().contains(token) ||
                                item.getCategoryCode().toLowerCase().contains(token) ||
                                item.getCategoryName().toLowerCase().contains(token));
                    }

                    return items
                            .map(TradingItemSuggestion::new)
                            .collect(Collectors.toList());
                });

        binding.setOnAutoCompleted(event -> {
            this.selectedTradingItem.set(event.getCompletion().item());
            tradingItemField.setText(this.selectedTradingItem.get().getCatalogNumber());
            autoSelectCategory(this.selectedTradingItem.get());
            categoryComboBox.setDisable(true);
        });
    }

    private void autoSelectCategory(TradingItem tradingItem) {
        if (tradingItem != null && tradingItem.getCategoryId() != null) {
            AppContext.getCategoryService().getById(tradingItem.getCategoryId())
                    .ifPresentOrElse(category -> {
                                categoryComboBox.getSelectionModel().select(category);
                                selectedCategory.set(category);
                            },
                            () -> categoryComboBox.getSelectionModel().select(null));
        }
    }

    private void handleTradingItemTextChange(String newText) {
        if (selectedTradingItem.get() != null &&
                (newText == null || !newText.equals(selectedTradingItem.get().getCatalogNumber()))) {
            selectedTradingItem.set(null);
            categoryComboBox.setDisable(false);
        }
        categoryComboBox.setDisable(selectedTradingItem.get() != null || newText == null || newText.isBlank());
    }

    public Long resolveTradingItemId() {
        if (selectedTradingItem.get() != null) {
            return selectedTradingItem.get().getId();
        } else {
            TradingItem newTradingItem = new TradingItem();
            newTradingItem.setCatalogNumber(tradingItemField.getText());
            newTradingItem.setOrderNumber(TradingItem.calculateOrderNumber(tradingItemField.getText()));
            newTradingItem.setCategoryId(categoryComboBox.getSelectionModel().getSelectedItem().getId());
            AppContext.getTradingItemService().save(newTradingItem);
            logger.info("Created new TradingItem with ID: {}", newTradingItem.getId());
            return newTradingItem.getId();
        }
    }
}