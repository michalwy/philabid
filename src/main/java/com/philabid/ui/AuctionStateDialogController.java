package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.ui.control.MonetaryField;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
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
    private Label maxBidCurrencyLabel;
    @FXML
    private Label currencyLabel;
    @FXML
    private CheckBox archivedCheckBox;

    private Stage dialogStage;
    private Auction auction;
    private EditDialogResult editDialogResult;
    private boolean initiallyArchived;

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
            currentPrice.setAmount(auction.getCurrentPrice().originalAmount());
            currencyLabel.setText(auction.getCurrentPrice().getOriginalCurrency().getCurrencyCode());
        } else {
            currencyLabel.setText(auction.getAuctionHouseCurrency().getCurrencyCode());
        }

        if (auction.getMaxBid() != null) {
            maxBid.setAmount(auction.getMaxBid().originalAmount());
            maxBidCurrencyLabel.setText(auction.getMaxBid().getOriginalCurrency().getCurrencyCode());
        } else {
            maxBidCurrencyLabel.setText(auction.getAuctionHouseCurrency().getCurrencyCode());
        }

        archivedCheckBox.setSelected(auction.isArchived() || auction.isFinished());

        initiallyArchived = auction.isArchived();

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

        if (!currentPrice.isEmpty()) {
            MultiCurrencyMonetaryAmount oldCurrentPrice = auction.getCurrentPrice();
            auction.setCurrentPrice(
                    Money.of(currentPrice.getAmount(),
                            oldCurrentPrice != null ? oldCurrentPrice.getOriginalCurrency() :
                                    auction.getAuctionHouseCurrency()));
        } else {
            auction.setCurrentPrice(null);
        }

        if (!maxBid.isEmpty()) {
            MultiCurrencyMonetaryAmount oldMaxBid = auction.getMaxBid();
            auction.setMaxBid(Money.of(maxBid.getAmount(),
                    oldMaxBid != null ? oldMaxBid.getOriginalCurrency() : auction.getAuctionHouseCurrency()));
        } else {
            auction.setMaxBid(null);
        }

        if (!initiallyArchived && archivedCheckBox.isSelected() && auction.getCatalogValue() != null) {
            setArchivedValues();
        }

        auction.setArchived(archivedCheckBox.isSelected());

        return true;
    }

    private void setArchivedValues() {
        auction.setArchivedCatalogValue(auction.getCatalogValue());

        if (auction.getCatalogValue() != null && auction.getCurrentPrice() != null) {
            Double catalogValue = auction.getCatalogValue().defaultCurrencyAmount().getNumber().doubleValue();
            Double currentPrice = auction.getCurrentPrice().defaultCurrencyAmount().getNumber().doubleValue();
            auction.setArchivedCatalogValuePercentage(currentPrice / catalogValue);
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        return true;
    }
}