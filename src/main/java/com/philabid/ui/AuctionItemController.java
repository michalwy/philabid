package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionItem;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Auction Item management view (AuctionItemView.fxml).
 */
public class AuctionItemController extends FilteredCrudTableViewController<AuctionItem> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemController.class);
    @FXML
    private TableColumn<AuctionItem, String> categoryColumn;
    @FXML
    private TableColumn<AuctionItem, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<AuctionItem, String> catalogInfoColumn;
    @FXML
    private TableColumn<AuctionItem, String> notesColumn;

    public AuctionItemController() {
        super(AppContext.getAuctionItemService());
    }

    @Override
    protected void initializeView() {
        categoryColumn.setCellValueFactory(CellValueFactoryProvider.forCategoryInfo(
                AuctionItem::getCategoryCode, AuctionItem::getCategoryName));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Use the provider for the complex catalog number column
        catalogNumberColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogNumber(
                AuctionItem::getCatalogNumber, AuctionItem::getOrderNumber));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);

        catalogInfoColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogInfo(
                AuctionItem::getCatalogName, AuctionItem::getCatalogIssueYear));

        logger.debug("AuctionItemController initialized.");
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/AuctionItemEditDialog.fxml";
    }
}
