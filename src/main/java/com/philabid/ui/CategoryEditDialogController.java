package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Catalog;
import com.philabid.model.Category;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Controller for the category edit dialog.
 */
public class CategoryEditDialogController extends CrudEditDialogController<Category> {

    private static final Logger logger = LoggerFactory.getLogger(CategoryEditDialogController.class);

    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;
    @FXML
    private TextField orderNumberField;
    @FXML
    private ComboBox<Catalog> catalogComboBox;

    @Override
    protected void initContent() {
        populateCatalogComboBox();

        Platform.runLater(() -> {
            orderNumberField.requestFocus();
        });

        logger.debug("CategoryEditDialogController initialized.");
    }

    @Override
    protected void loadEntity(Category category) {
        nameField.setText(category.getName());
        codeField.setText(category.getCode());
        orderNumberField.setText(category.getOrderNumber().toString());

        if (category.getCatalogId() != null && catalogComboBox.getItems() != null) {
            catalogComboBox.getItems().stream()
                    .filter(c -> c.getId().equals(category.getCatalogId()))
                    .findFirst()
                    .ifPresent(catalogComboBox.getSelectionModel()::select);
        }
    }

    private void populateCatalogComboBox() {
        Collection<Catalog> catalogs = AppContext.getCatalogService().getAll();
        catalogComboBox.setItems(FXCollections.observableArrayList(catalogs));
        logger.debug("Populated catalog ComboBox with {} items.", catalogs.size());
    }

    @Override
    protected Collection<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errors.add(new ValidationError("No valid name.", nameField));
        }
        if (codeField.getText() == null || codeField.getText().trim().isEmpty()) {
            errors.add(new ValidationError("No valid code.", codeField));
        }
        return errors;
    }

    @Override
    protected void updateEntity(Category category) {
        category.setName(nameField.getText());
        category.setCode(codeField.getText());
        category.setOrderNumber(Long.parseLong(orderNumberField.getText()));

        Catalog selectedCatalog = catalogComboBox.getSelectionModel().getSelectedItem();
        if (selectedCatalog != null) {
            category.setCatalogId(selectedCatalog.getId());
        } else {
            category.setCatalogId(null);
        }
    }
}
