package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * An abstract base controller containing shared logic for auction views.
 */
public abstract class BaseAuctionController extends FilteredCrudTableViewController<Auction> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAuctionController.class);

    @FXML
    protected TableColumn<Auction, String> auctionHouseColumn;
    @FXML
    protected TableColumn<Auction, String> categoryColumn;
    @FXML
    protected TableColumn<Auction, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    protected TableColumn<Auction, String> conditionColumn;
    @FXML
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> currentPriceColumn;
    @FXML
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> recommendedPriceColumn;
    @FXML
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> catalogValueColumn;
    @FXML
    protected TableColumn<Auction, LocalDateTime> endDateColumn;

    public BaseAuctionController() {
        super(AppContext.getAuctionService());
    }

    @Override
    protected void initializeView() {
        auctionHouseColumn.setCellValueFactory(new PropertyValueFactory<>("auctionHouseName"));

        setCategoryColumn(categoryColumn, Auction::getAuctionItemCategoryCode, Auction::getAuctionItemCategoryName);
        setCatalogNumberColumn(catalogNumberColumn, Auction::getAuctionItemCatalogNumber,
                Auction::getAuctionItemOrderNumber);

        setConditionColumn(conditionColumn, Auction::getConditionCode, Auction::getConditionName);

        setPriceWithThresholdColumn(currentPriceColumn, "currentPrice", Auction::getRecommendedPrice,
                Auction::getCatalogValue);
        setPriceWithThresholdColumn(recommendedPriceColumn, "recommendedPrice", Auction::getCatalogValue,
                Auction::getCatalogValue);

        setCatalogValueColumn(catalogValueColumn, "catalogValue", Auction::getCatalogValue, Auction::isCatalogActive);

        setDateTimeColumn(endDateColumn, "endDate");
    }

    public abstract Collection<Auction> loadAuctions(Collection<FilterCondition> filterConditions);

    protected Collection<Auction> loadTableItems() {
        return loadAuctions(getCrudTableView().getFilterConditions());
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/AuctionEditDialog.fxml";
    }
}