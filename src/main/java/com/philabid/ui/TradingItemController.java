package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.TradingItem;
import com.philabid.ui.cell.CatalogNumberColumnValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.philabid.ui.util.TableViewHelpers.*;

public class TradingItemController extends FilteredCrudTableViewController<TradingItem> {

    private static final Logger logger = LoggerFactory.getLogger(TradingItemController.class);
    @FXML
    private TableColumn<TradingItem, String> categoryColumn;
    @FXML
    private TableColumn<TradingItem, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<TradingItem, String> catalogInfoColumn;
    @FXML
    private TableColumn<TradingItem, String> notesColumn;

    public TradingItemController() {
        super(AppContext.getTradingItemService());
    }

    @Override
    protected void initializeView() {
        setCategoryColumn(categoryColumn, TradingItem::getCategoryCode, TradingItem::getCategoryName);
        setCatalogNumberColumn(catalogNumberColumn, TradingItem::getCatalogNumber, TradingItem::getOrderNumber,
                TradingItem::getCategoryCode);

        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        setCatalogColumn(catalogInfoColumn, TradingItem::getCatalogName, TradingItem::getCatalogIssueYear);
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/TradingItemEditDialog.fxml";
    }
}
