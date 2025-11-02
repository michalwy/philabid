package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import com.philabid.ui.cell.CatalogNumberColumnValue;
import com.philabid.ui.cell.ThresholdMultiCurrencyMonetaryAmountCell;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.philabid.ui.util.TableViewHelpers.*;

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
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> maxBidColumn;
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

        maxBidColumn.setCellValueFactory(new PropertyValueFactory<>("maxBid"));
        maxBidColumn.setCellFactory(column -> new ThresholdMultiCurrencyMonetaryAmountCell<>(
                Auction::getRecommendedPrice,
                Auction::getCatalogValue));

        addRowFormatter((row, auction, empty) -> {
            row.getStyleClass().remove("winning-auction");
            row.getStyleClass().remove("overpriced-auction");

            if (empty || auction == null) {
                return;
            }

            if (auction.getMaxBid() != null && auction.getCurrentPrice() != null && auction.getMaxBid().originalAmount()
                    .isGreaterThanOrEqualTo(auction.getCurrentPrice().originalAmount())) {
                row.getStyleClass().add("winning-auction");
            } else if (auction.getCurrentPrice() != null && auction.getRecommendedPrice() != null &&
                    auction.getCurrentPrice().defaultCurrencyAmount()
                            .isGreaterThan(auction.getRecommendedPrice().defaultCurrencyAmount())) {
                row.getStyleClass().add("overpriced-auction");
            }
        });

        setCategoryColumn(categoryColumn, Auction::getTradingItemCategoryCode, Auction::getTradingItemCategoryName);
        setCatalogNumberColumn(catalogNumberColumn, Auction::getTradingItemCatalogNumber,
                Auction::getTradingItemOrderNumber);

        setConditionColumn(conditionColumn, Auction::getConditionCode, Auction::getConditionName);

        setPriceWithThresholdColumn(currentPriceColumn, "currentPrice", Auction::getRecommendedPrice,
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