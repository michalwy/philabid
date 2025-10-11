package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.ui.cell.CatalogValueCell;
import com.philabid.ui.cell.RightAlignedDateCell;
import com.philabid.ui.cell.ThresholdMultiCurrencyMonetaryAmountCell;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An abstract base controller containing shared logic for auction views.
 */
public abstract class BaseAuctionController extends CrudTableViewController<Auction> {

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

        categoryColumn.setCellValueFactory(CellValueFactoryProvider.forCategoryInfo(Auction::getAuctionItemCategoryCode,
                Auction::getAuctionItemCategoryName));

        catalogNumberColumn.setCellValueFactory(
                CellValueFactoryProvider.forCatalogNumber(Auction::getAuctionItemCatalogNumber,
                        Auction::getAuctionItemOrderNumber));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);

        conditionColumn.setCellValueFactory(
                CellValueFactoryProvider.forConditionInfo(Auction::getConditionCode, Auction::getConditionName));

        // Use a reusable cell factory and add specific styling for the current price
        currentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        currentPriceColumn.setCellFactory(
                column -> new ThresholdMultiCurrencyMonetaryAmountCell<>(Auction::getRecommendedPrice,
                        Auction::getCatalogValue));

        // For now, recommended price is the same as catalog value
        recommendedPriceColumn.setCellValueFactory(new PropertyValueFactory<>("recommendedPrice"));
        recommendedPriceColumn.setCellFactory(
                column -> new ThresholdMultiCurrencyMonetaryAmountCell<>(Auction::getCatalogValue,
                        Auction::getCatalogValue));

        catalogValueColumn.setCellValueFactory(new PropertyValueFactory<>("catalogValue"));
        catalogValueColumn.setCellFactory(column -> new CatalogValueCell());

        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateColumn.setCellFactory(column -> new RightAlignedDateCell<>());
    }

    public abstract List<Auction> loadAuctions();

    protected List<Auction> loadTableItems() {
        return loadAuctions();
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/AuctionEditDialog.fxml";
    }
}