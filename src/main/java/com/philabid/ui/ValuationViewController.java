package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Valuation;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class ValuationViewController extends FilteredCrudTableViewController<Valuation> {

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
    protected String getDialogFXMLResourcePath() {
        return null;
    }
}