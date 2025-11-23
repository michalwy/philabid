package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Valuation;
import com.philabid.ui.cell.CatalogNumberColumnValue;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.philabid.ui.CatalogValueEditDialogController.addOrUpdateCatalogValue;
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
        setCategoryColumn(categoryColumn, Valuation::getTradingItemCategoryCode, Valuation::getTradingItemCategoryName);
        setCatalogNumberColumn(catalogNumberColumn, Valuation::getTradingItemCatalogNumber,
                Valuation::getTradingItemOrderNumber, Valuation::getTradingItemCategoryOrderNumber);
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
                event -> handleShowValuation(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem addCatalogValueItem = new MenuItem("Add Catalog Value...");
        addCatalogValueItem.setOnAction(
                event -> handleAddOrUpdateCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem updateCatalogValueItem = new MenuItem("Update Catalog Value...");
        updateCatalogValueItem.setOnAction(
                event -> handleAddOrUpdateCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

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

    private void handleAddOrUpdateCatalogValue(Valuation valuation) {
        if (valuation == null) return;
        if (addOrUpdateCatalogValue(getTableView().getScene().getWindow(), valuation.getTradingItemId(),
                valuation.getConditionId())) {
            refreshTable();
        }
    }

    private void handleShowValuation(Valuation selectedItem) {
        ValuationDialogController.showValuationDialog(getTableView().getScene().getWindow(), selectedItem);
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return null;
    }

    @Override
    protected void handleDoubleClick() {
        handleShowValuation(getTableView().getSelectionModel().getSelectedItem());
    }
}