package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.CatalogValue;
import com.philabid.ui.cell.MultiCurrencyMonetaryAmountCell;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Controller for the Catalog Value management view (CatalogValueView.fxml).
 */
public class CatalogValueController extends BaseTableViewController<CatalogValue> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueController.class);
    @FXML
    private TableColumn<CatalogValue, String> categoryColumn;
    @FXML
    private TableColumn<CatalogValue, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<CatalogValue, String> conditionColumn;
    @FXML
    private TableColumn<CatalogValue, String> catalogColumn;
    @FXML
    private TableColumn<CatalogValue, MultiCurrencyMonetaryAmount> valueColumn;

    @Override
    protected void initializeView() {
        // Use the provider for a consistent "Category (CODE)" format
        categoryColumn.setCellValueFactory(CellValueFactoryProvider.forCategoryInfo(
                CatalogValue::getAuctionItemCategoryCode, CatalogValue::getAuctionItemCategoryName));

        // Use the provider for the complex catalog number column
        catalogNumberColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogNumber(
                CatalogValue::getAuctionItemCatalogNumber, CatalogValue::getAuctionItemOrderNumber));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);

        // Use the provider for a consistent "Catalog (YEAR)" format
        catalogColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogInfo(
                CatalogValue::getCatalogName, CatalogValue::getCatalogIssueYear));

        // This formatting is specific enough to remain here for now
        conditionColumn.setCellValueFactory(CellValueFactoryProvider.forConditionInfo(CatalogValue::getConditionCode,
                CatalogValue::getConditionName));

        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setCellFactory(column -> new MultiCurrencyMonetaryAmountCell<>());
    }

    @Override
    protected List<CatalogValue> loadTableItems() {
        return AppContext.getCatalogValueService().getAllCatalogValues();
    }

    @FXML
    private void handleAddCatalogValue() {
        logger.info("Add catalog value button clicked.");
        doHandleAddCatalogValue();
    }

    private void doHandleAddCatalogValue() {
        CatalogValue newCatalogValue = new CatalogValue();
        EditDialogResult result = showAuctionItemEditDialog(newCatalogValue);
        if (!Objects.isNull(result)) {
            if (result.saved()) {
                AppContext.getCatalogValueService().saveCatalogValue(newCatalogValue);
                refreshTable();
            }
            if (result.editNext()) {
                Platform.runLater(this::doHandleAddCatalogValue);
            }
        }
    }

    private EditDialogResult showAuctionItemEditDialog(CatalogValue catalogValue) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CatalogValueEditDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(catalogValue.getId() == null ? "Add Catalog Value" : "Edit Catalog Value");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(table.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CatalogValueEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCatalogValue(catalogValue);

            dialogStage.showAndWait();

            return controller.getEditDialogResult();
        } catch (IOException e) {
            logger.error("Failed to load the auction item edit dialog.", e);
            return null;
        }
    }

    @FXML
    private void handleEditCatalogValue() {
        CatalogValue selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit catalog value button clicked for item ID: {}", selected.getAuctionItemId());
            // Logic to be implemented
        }
    }

    @FXML
    private void handleDeleteCatalogValue() {
        CatalogValue selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete catalog value button clicked for item ID: {}", selected.getAuctionItemId());
            // Logic to be implemented
        }
    }

    @Override
    protected void handleDoubleClick() {
        handleEditCatalogValue();
    }
}
