package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.CatalogValue;
import com.philabid.service.*;
import com.philabid.ui.cell.MonetaryAmountCell;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the Catalog Value management view (CatalogValueView.fxml).
 */
public class CatalogValueController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueController.class);
    private final ObservableList<CatalogValue> catalogValueList = FXCollections.observableArrayList();
    @FXML
    private TableView<CatalogValue> catalogValueTable;
    @FXML
    private TableColumn<CatalogValue, String> categoryColumn;
    @FXML
    private TableColumn<CatalogValue, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<CatalogValue, String> conditionColumn;
    @FXML
    private TableColumn<CatalogValue, String> catalogColumn;
    @FXML
    private TableColumn<CatalogValue, MonetaryAmount> valueColumn;
    private CatalogValueService catalogValueService;
    private AuctionItemService auctionItemService;
    private ConditionService conditionService;
    private CatalogService catalogService;
    private CategoryService categoryService;
    private I18nManager i18nManager;
    private CurrencyService currencyService;

    @FXML
    private void initialize() {
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
        valueColumn.setCellFactory(column -> new MonetaryAmountCell<>());

        catalogValueTable.setItems(catalogValueList);
    }

    public void setServices(CurrencyService currencyService, CatalogValueService catalogValueService,
                            AuctionItemService auctionItemService,
                            ConditionService conditionService, CatalogService catalogService,
                            CategoryService categoryService, I18nManager i18nManager) {
        this.currencyService = currencyService;
        this.catalogValueService = catalogValueService;
        this.auctionItemService = auctionItemService;
        this.conditionService = conditionService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
        this.i18nManager = i18nManager;
    }

    public void loadCatalogValues() {
        if (catalogValueService != null) {
            catalogValueList.setAll(catalogValueService.getAllCatalogValues());
            catalogValueTable.sort();
            logger.info("Loaded {} catalog values into the table.", catalogValueList.size());
        } else {
            logger.warn("CatalogValueService is not available. Cannot load data.");
        }
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
            if (result.saveClicked()) {
                catalogValueService.saveCatalogValue(newCatalogValue);
                loadCatalogValues();
            }
            if (result.addAnother()) {
                Platform.runLater(() -> {
                    doHandleAddCatalogValue();
                });
            }
        }
    }

    private EditDialogResult showAuctionItemEditDialog(CatalogValue catalogValue) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CatalogValueEditDialog.fxml"));
            loader.setResources(i18nManager.getResourceBundle());

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(catalogValue.getId() == null ? "Add Catalog Value" : "Edit Catalog Value");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(catalogValueTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CatalogValueEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setServices(currencyService, auctionItemService, conditionService, catalogService,
                    categoryService, i18nManager);
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
        CatalogValue selected = catalogValueTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit catalog value button clicked for item ID: {}", selected.getAuctionItemId());
            // Logic to be implemented
        }
    }

    @FXML
    private void handleDeleteCatalogValue() {
        CatalogValue selected = catalogValueTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete catalog value button clicked for item ID: {}", selected.getAuctionItemId());
            // Logic to be implemented
        }
    }
}
