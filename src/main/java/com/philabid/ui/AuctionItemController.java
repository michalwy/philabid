package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionItem;
import com.philabid.ui.util.CatalogNumberColumnValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.philabid.ui.util.TableViewHelpers.*;

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
        setCategoryColumn(categoryColumn, AuctionItem::getCategoryCode, AuctionItem::getCategoryName);
        setCatalogNumberColumn(catalogNumberColumn, AuctionItem::getCatalogNumber, AuctionItem::getOrderNumber);

        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        setCatalogColumn(catalogInfoColumn, AuctionItem::getCatalogName, AuctionItem::getCatalogIssueYear);

        logger.debug("AuctionItemController initialized.");
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/AuctionItemEditDialog.fxml";
    }
}
