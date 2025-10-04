package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.ui.control.MonetaryField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import org.javamoney.moneta.Money;

public class AuctionStateDialogController {

    @FXML
    private Hyperlink urlLink;
    @FXML
    private MonetaryField currentPrice;
    @FXML
    private MonetaryField maxBid;
    @FXML
    private CheckBox archivedCheckBox;

    private Stage dialogStage;
    private Auction auction;
    private EditDialogResult editDialogResult;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;

        urlLink.setText(auction.getAuctionItemCategoryCode() + " :: " + auction.getAuctionItemCatalogNumber() + " :: " +
                auction.getConditionCode());
        if (auction.getUrl() != null && !auction.getUrl().isBlank()) {
            urlLink.setOnAction(event -> {
                AppContext.getHostServices().showDocument(auction.getUrl());
            });
        } else {
            urlLink.setDisable(true);
        }

        if (auction.getCurrentPrice() != null) {
            currentPrice.setAmount(auction.getCurrentPrice());
        }

        if (auction.getMaxBid() != null) {
            maxBid.setAmount(auction.getMaxBid());
        }

        archivedCheckBox.setSelected(auction.isArchived());

        Platform.runLater(() -> {
            currentPrice.requestFocus();
        });
    }

    public EditDialogResult getEditDialogResult() {
        return editDialogResult;
    }

    @FXML
    private void handleSave() {
        if (doHandleSave()) {
            editDialogResult = new EditDialogResult(true, false);
            dialogStage.close();
        }
    }

    @FXML
    void handleSaveAndNext() {
        if (doHandleSave()) {
            editDialogResult = new EditDialogResult(true, true);
            dialogStage.close();
        }
    }

    private boolean doHandleSave() {
        if (!isInputValid()) {
            return false;
        }

        auction.setCurrentPrice(Money.of(currentPrice.getAmount(), auction.getCurrentPrice().getCurrency()));
        auction.setArchived(archivedCheckBox.isSelected());

        if (!maxBid.isEmpty()) {
            auction.setMaxBid(Money.of(maxBid.getAmount(), auction.getCurrentPrice().getCurrency()));
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        if (currentPrice.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Field");
            alert.setHeaderText("Current price cannot be empty.");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}