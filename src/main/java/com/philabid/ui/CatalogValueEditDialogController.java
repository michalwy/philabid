package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Catalog;
import com.philabid.model.CatalogValue;
import com.philabid.model.Category;
import com.philabid.model.Condition;
import com.philabid.ui.control.CrudEditDialog;
import com.philabid.ui.control.MonetaryField;
import com.philabid.ui.control.TradingItemSelector;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the catalog value edit dialog.
 */
public class CatalogValueEditDialogController extends CrudEditDialogController<CatalogValue> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueEditDialogController.class);
    @FXML
    private TradingItemSelector tradingItemSelector;
    @FXML
    private ComboBox<Condition> conditionComboBox;
    @FXML
    private ComboBox<Catalog> catalogComboBox;
    @FXML
    private MonetaryField valueField;
    @FXML
    private ComboBox<CurrencyUnit> currencyComboBox;
    private EditDialogResult editDialogResult;

    public static EditDialogResult showCatalogValueEditDialog(Window owner, CatalogValue catalogValue) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(CatalogValueEditDialogController.class.getResource("/fxml/CatalogValueEditDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());

            CrudEditDialog<CatalogValue> page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Catalog Value");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CatalogValueEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(catalogValue);

            dialogStage.showAndWait();

            return controller.getEditDialogResult();
        } catch (IOException e) {
            logger.error("Failed to load the catalog value edit dialog.", e);
            return null;
        }
    }

    @Override
    protected void initContent() {
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

        tradingItemSelector.selectedCategoryProperty()
                .addListener((obs, oldVal, newVal) -> autoSelectCatalogAndCurrency(newVal));

        populateComboBoxes();
    }

    @Override
    protected void loadEntity(CatalogValue catalogValue) {
        if (catalogValue.getValue() != null) {
            valueField.setAmount(catalogValue.getValue().originalAmount());
        }

        // Pre-select items in ComboBoxes if editing an existing value
        if (catalogValue.getTradingItemId() != null) {
            AppContext.getTradingItemService().getById(catalogValue.getTradingItemId())
                    .ifPresent(tradingItemSelector::setSelectedTradingItem);
        }
        if (catalogValue.getConditionId() != null) {
            conditionComboBox.getItems().stream().filter(c -> c.getId().equals(catalogValue.getConditionId()))
                    .findFirst().ifPresent(conditionComboBox.getSelectionModel()::select);
        }
        if (catalogValue.getCatalogId() != null) {
            catalogComboBox.getSelectionModel()
                    .select(AppContext.getCatalogService().getById(catalogValue.getCatalogId()).orElse(null));
        }
        if (catalogValue.getValue() != null) {
            currencyComboBox.getSelectionModel().select(catalogValue.getValue().getOriginalCurrency());
        }

        if (catalogComboBox.getSelectionModel().getSelectedItem() != null) {
            if (conditionComboBox.getSelectionModel().getSelectedItem() != null) {
                Platform.runLater(() -> valueField.requestFocus());
            } else {
                Platform.runLater(() -> {
                    conditionComboBox.requestFocus();
                    conditionComboBox.show();
                });
            }
        } else {
            Platform.runLater(() -> tradingItemSelector.requestFocus());
        }
    }

    private void populateComboBoxes() {
        conditionComboBox.setItems(
                FXCollections.observableArrayList(AppContext.getConditionService().getAll()));
        catalogComboBox.setItems(FXCollections.observableArrayList(AppContext.getCatalogService().getAll()));
        currencyComboBox.setItems(FXCollections.observableArrayList(AppContext.getCurrencyService().getCurrencies()));
    }

    private void autoSelectCatalogAndCurrency(Category category) {
        if (category != null && category.getCatalogId() != null) {
            AppContext.getCatalogService().getById(category.getCatalogId()).ifPresent(defaultCatalog -> {
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

    @Override
    protected void updateEntity(CatalogValue catalogValue) {
        catalogValue.setTradingItemId(tradingItemSelector.resolveTradingItemId());
        catalogValue.setConditionId(conditionComboBox.getSelectionModel().getSelectedItem().getId());
        catalogValue.setCatalogId(catalogComboBox.getSelectionModel().getSelectedItem().getId());
        try {
            catalogValue.setValue(Money.of(valueField.getAmount(),
                    currencyComboBox.getSelectionModel().getSelectedItem()));
        } catch (NumberFormatException e) {
            logger.error("Invalid number format for value field: {}", valueField.getText());
            return;
        }

        Optional<CatalogValue> savedValue = AppContext.getCatalogValueService().save(catalogValue);

        if (savedValue.isPresent()) {
            editDialogResult = new EditDialogResult(true, false);
            //dialogStage.close();
        } else {
            // Save failed, likely due to a duplicate entry
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.initOwner(dialogStage);
            alert.setTitle(AppContext.getI18nManager().getString("common.error.title"));
            alert.setHeaderText("Duplicate Entry");
            alert.setContentText("A catalog value for this item and condition already exists. Please " +
                    "edit the existing entry instead of creating a new one.");
            alert.showAndWait();
        }
    }

    @Override
    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (tradingItemSelector.getText() == null || tradingItemSelector.getText().isBlank()) {
            errors.add(new ValidationError("Catalog Number cannot be empty.", tradingItemSelector));
        }

        // If it's a new item, a category must be selected
        if (tradingItemSelector.getSelectedTradingItem() == null && tradingItemSelector.getSelectedCategory() == null) {
            errors.add(new ValidationError("Category must be selected for a new catalog number.", tradingItemSelector));
        }
        if (conditionComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError("Condition must be selected.", conditionComboBox));
        }
        if (catalogComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError("Catalog must be selected.", catalogComboBox));
        }
        if (currencyComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError("Currency must be selected.", currencyComboBox));
        }
        if (valueField.isEmpty()) {
            errors.add(new ValidationError("Value cannot be empty.", valueField));
        }
        return errors;
    }

    public EditDialogResult getEditDialogResult() {
        return editDialogResult;
    }
}
