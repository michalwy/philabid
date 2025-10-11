package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionHouse;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for the Auction House management view (AuctionHouseView.fxml).
 * Handles user interactions for creating, editing, and deleting auction houses.
 */
public class AuctionHouseController extends CrudTableViewController<AuctionHouse> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionHouseController.class);
    @FXML
    private TableColumn<AuctionHouse, String> nameColumn;
    @FXML
    private TableColumn<AuctionHouse, String> websiteColumn;
    @FXML
    private TableColumn<AuctionHouse, String> countryColumn;
    @FXML
    private TableColumn<AuctionHouse, String> currencyColumn;

    public AuctionHouseController() {
        super(AppContext.getAuctionHouseService());
    }

    @Override
    protected void initializeView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/AuctionHouseEditDialog.fxml";
    }
}
