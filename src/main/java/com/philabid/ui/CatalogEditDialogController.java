package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Catalog;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for the catalog edit dialog.
 */
public class CatalogEditDialogController extends CrudEditDialogController<Catalog> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogEditDialogController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField issueYearField;
    @FXML
    private ComboBox<CurrencyUnit> currencyComboBox;
    @FXML
    private CheckBox activeCheckBox;

    @Override
    protected void initContent() {
        populateCurrencyComboBox();
        logger.debug("CatalogEditDialogController initialized.");
    }

    @Override
    protected void loadEntity(Catalog catalog) {
        nameField.setText(catalog.getName());
        if (catalog.getIssueYear() != null) {
            issueYearField.setText(catalog.getIssueYear().toString());
        }
        activeCheckBox.setSelected(catalog.isActive());

        if (catalog.getCurrency() != null && currencyComboBox.getItems() != null) {
            currencyComboBox.getItems().stream()
                    .filter(c -> c.equals(catalog.getCurrency()))
                    .findFirst()
                    .ifPresent(currencyComboBox.getSelectionModel()::select);
        }
    }

    private void populateCurrencyComboBox() {
        currencyComboBox.setItems(FXCollections.observableArrayList(AppContext.getCurrencyService().getCurrencies()));
    }

    @Override
    protected void updateEntity(Catalog catalog) {
        catalog.setName(nameField.getText());
        try {
            catalog.setIssueYear(Integer.parseInt(issueYearField.getText()));
        } catch (NumberFormatException e) {
            catalog.setIssueYear(null); // Or handle error
        }

        CurrencyUnit selectedCurrency = currencyComboBox.getSelectionModel().getSelectedItem();
        catalog.setCurrency(selectedCurrency);
        catalog.setActive(activeCheckBox.isSelected());
    }

    @Override
    protected Collection<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.add(new ValidationError("Name cannot be empty.", nameField));
        }
        if (issueYearField.getText() != null && !issueYearField.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(issueYearField.getText());
            } catch (NumberFormatException e) {
                errors.add(new ValidationError("Issue year must be a valid number.", issueYearField));
            }
        }
        if (currencyComboBox.getSelectionModel().getSelectedItem() == null) {
            errors.add(new ValidationError("Currency must be selected.", currencyComboBox));
        }

        return errors;
    }
}
