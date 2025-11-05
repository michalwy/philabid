package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionHouse;
import com.philabid.model.Seller;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class SellerController extends ModalCrudTableViewController<Seller> {
    @FXML
    private TableColumn<AuctionHouse, String> nameColumn;
    @FXML
    private TableColumn<AuctionHouse, String> fullNameColumn;

    public SellerController() {
        super(AppContext.getSellerService());
    }

    @Override
    protected void initializeView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
    }

    @Override
    protected String getDialogFXMLResourcePath() {
        return "/fxml/SellerEditDialog.fxml";
    }
}
