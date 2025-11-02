package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.CatalogValue;
import com.philabid.ui.cell.CatalogNumberColumnValue;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.philabid.ui.util.TableViewHelpers.*;

/**
 * Controller for the Catalog Value management view (CatalogValueView.fxml).
 */
public class CatalogValueController extends FilteredCrudTableViewController<CatalogValue> {

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
        setCategoryColumn(categoryColumn, CatalogValue::getTradingItemCategoryCode,
                CatalogValue::getTradingItemCategoryName);
        setCatalogNumberColumn(catalogNumberColumn, CatalogValue::getTradingItemCatalogNumber,
                CatalogValue::getTradingItemOrderNumber);

        setCatalogColumn(catalogColumn, CatalogValue::getCatalogName, CatalogValue::getCatalogIssueYear);
        setConditionColumn(conditionColumn, CatalogValue::getConditionCode, CatalogValue::getConditionName);

        setMonetaryColumn(valueColumn, "value");
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/CatalogValueEditDialog.fxml";
    }
}
