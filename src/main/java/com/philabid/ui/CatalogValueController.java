package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.CatalogValue;
import com.philabid.ui.cell.MultiCurrencyMonetaryAmountCell;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for the Catalog Value management view (CatalogValueView.fxml).
 */
public class CatalogValueController extends BaseTableViewController<CatalogValue> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueController.class);
    @FXML
    private TableColumn<CatalogValue, String> categoryColumn;
    @FXML
    private TableColumn<CatalogValue, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<CatalogValue, String> conditionColumn;
    @FXML
    private TableColumn<CatalogValue, String> catalogColumn;
    @FXML
    private TableColumn<CatalogValue, MultiCurrencyMonetaryAmount> valueColumn;

    public CatalogValueController() {
        super(AppContext.getCatalogValueService());
    }

    @Override
    protected void initializeView() {
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
        valueColumn.setCellFactory(column -> new MultiCurrencyMonetaryAmountCell<>());
    }

    @Override
    protected List<CatalogValue> loadTableItems() {
        return AppContext.getCatalogValueService().getAllCatalogValues();
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/CatalogValueEditDialog.fxml";
    }
}
