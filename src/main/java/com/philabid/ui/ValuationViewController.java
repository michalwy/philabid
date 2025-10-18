package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.CatalogValue;
import com.philabid.model.Valuation;
import com.philabid.ui.control.CrudEditDialog;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static com.philabid.ui.util.TableViewHelpers.*;

public class ValuationViewController extends FilteredCrudTableViewController<Valuation> {

    private static final Logger logger = LoggerFactory.getLogger(ValuationViewController.class);

    @FXML
    private TableColumn<Valuation, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<Valuation, String> categoryColumn;
    @FXML
    private TableColumn<Valuation, String> conditionColumn;
    @FXML
    private TableColumn<Valuation, MultiCurrencyMonetaryAmount> recommendedPrice;
    @FXML
    private TableColumn<Valuation, MultiCurrencyMonetaryAmount> catalogValueColumn;
    @FXML
    private TableColumn<Valuation, MultiCurrencyMonetaryAmount> averagePriceColumn;
    @FXML
    private TableColumn<Valuation, MultiCurrencyMonetaryAmount> minPriceColumn;
    @FXML
    private TableColumn<Valuation, MultiCurrencyMonetaryAmount> maxPriceColumn;
    @FXML
    private TableColumn<Valuation, MultiCurrencyMonetaryAmount> categoryAveragePrice;
    @FXML
    private TableColumn<Valuation, Integer> auctionCountColumn;

    public ValuationViewController() {
        super(AppContext.getValuationService());
    }

    @Override
    public void initializeView() {
        setCategoryColumn(categoryColumn, Valuation::getAuctionItemCategoryCode, Valuation::getAuctionItemCategoryName);
        setCatalogNumberColumn(catalogNumberColumn, Valuation::getAuctionItemCatalogNumber,
                Valuation::getAuctionItemOrderNumber);
        setConditionColumn(conditionColumn, Valuation::getConditionCode, Valuation::getConditionName);
        setCatalogValueColumn(catalogValueColumn, "catalogValue", Valuation::getCatalogValue,
                Valuation::isCatalogActive);

        setPriceWithThresholdColumn(recommendedPrice, "recommendedPrice", Valuation::getCategoryAveragePrice,
                Valuation::getCatalogValue);
        setPriceWithThresholdColumn(minPriceColumn, "minPrice", Valuation::getCategoryAveragePrice,
                Valuation::getCatalogValue);
        setPriceWithThresholdColumn(averagePriceColumn, "averagePrice", Valuation::getCategoryAveragePrice,
                Valuation::getCatalogValue);
        setPriceWithThresholdColumn(maxPriceColumn, "maxPrice", Valuation::getCategoryAveragePrice,
                Valuation::getCatalogValue);
        setPriceWithThresholdColumn(categoryAveragePrice, "categoryAveragePrice", Valuation::getCatalogValue,
                Valuation::getCatalogValue);

        auctionCountColumn.setCellValueFactory(new PropertyValueFactory<>("auctionCount"));
        auctionCountColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
    }

    @Override
    protected List<MenuItem> getContextMenuItems() {
        MenuItem showHistoricalAuctions = new MenuItem("Show historical auctions...");
        showHistoricalAuctions.setOnAction(
                event -> handleShowHistoricalAuctions(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem addCatalogValueItem = new MenuItem("Add Catalog Value...");
        addCatalogValueItem.setOnAction(
                event -> handleAddCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem updateCatalogValueItem = new MenuItem("Update Catalog Value...");
        updateCatalogValueItem.setOnAction(
                event -> handleUpdateCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

        return List.of(showHistoricalAuctions, addCatalogValueItem, updateCatalogValueItem);
    }

    @Override
    protected void onContextMenuShowing(ContextMenu contextMenu) {
        Valuation selected = getTableView().getSelectionModel().getSelectedItem();
        if (selected == null) return;

        contextMenu.getItems().stream()
                .filter(item -> "Add Catalog Value...".equals(item.getText()))
                .findFirst()
                .ifPresent(menuItem -> menuItem.setVisible(selected.getCatalogValue() == null));

        contextMenu.getItems().stream()
                .filter(item -> "Update Catalog Value...".equals(item.getText()))
                .findFirst()
                .ifPresent(menuItem -> menuItem.setVisible(
                        selected.getCatalogValue() != null && !selected.isCatalogActive()));
    }

    private void handleAddCatalogValue(Valuation valuation) {
        if (valuation == null) return;

        CatalogValue newCatalogValue = new CatalogValue();
        // Pre-populate with data from the auction
        newCatalogValue.setAuctionItemId(valuation.getAuctionItemId());
        newCatalogValue.setConditionId(valuation.getConditionId());

        EditDialogResult result = showCatalogValueEditDialog(newCatalogValue);
        if (result != null && result.saved()) {
            refreshTable(); // Refresh to show the new value
        }
    }

    private void handleUpdateCatalogValue(Valuation valuation) {
        if (valuation == null) return;

        // Find the existing CatalogValue to edit it
        AppContext.getCatalogValueService()
                .findByAuctionItemAndCondition(valuation.getAuctionItemId(), valuation.getConditionId())
                .ifPresent(catalogValueToUpdate -> {
                    // We want the user to select a new, active catalog, so we don't pre-set the old one.
                    catalogValueToUpdate.setCatalogId(null);
                    EditDialogResult result = showCatalogValueEditDialog(catalogValueToUpdate);
                    if (result != null && result.saved()) {
                        refreshTable();
                    }
                });
    }

    private EditDialogResult showCatalogValueEditDialog(CatalogValue catalogValue) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CatalogValueEditDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());

            CrudEditDialog<CatalogValue> page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Catalog Value");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(getTableView().getScene().getWindow());
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

    private void handleShowHistoricalAuctions(Valuation selectedItem) {
        if (selectedItem == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HistoricalAuctionsDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Historical Auctions");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(getTableView().getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            HistoricalAuctionsDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAuctionItem(selectedItem.getAuctionItemId(), selectedItem.getConditionId());

            dialogStage.showAndWait();
        } catch (IOException e) {
            logger.error("Failed to load Archive Auction dialog.", e);
        }
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return null;
    }

    @Override
    protected void handleDoubleClick() {
        handleShowHistoricalAuctions(getTableView().getSelectionModel().getSelectedItem());
    }
}