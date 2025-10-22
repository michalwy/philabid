package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.service.AllegroApiService;
import com.philabid.ui.control.MonetaryField;
import com.philabid.ui.control.ValuationDetails;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML
    private Button sendMaxBidButton;
    @FXML
    private Button refreshMaxBidButton;
    @FXML
    private ValuationDetails valuationDetails;

    private Stage dialogStage;
    private Auction auction;
    private EditDialogResult editDialogResult;
    private boolean initiallyArchived;

    @FXML
    private void initialize() {
        valuationDetails.selectedPrice().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                maxBid.setAmount(newValue.originalAmount());
                maxBidCurrencyLabel.setText(newValue.getOriginalCurrency().getCurrencyCode());
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;

        urlLink.setText(auction.getAuctionItemCatalogNumber() + "  (" + auction.getAuctionItemCategoryName() + ", " +
                auction.getConditionName() + ")");
        urlLink.setStyle("-fx-font-size: 16;");
        if (auction.getUrl() != null && !auction.getUrl().isBlank()) {
            urlLink.setOnAction(event -> {
                AppContext.getHostServices().showDocument(auction.getUrl());
                Platform.runLater(() -> currentPrice.requestFocus());
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

        valuationDetails.setAuctionItem(auction.getAuctionItemId(), auction.getConditionId());

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

    @FXML
    private void handleSendMaxBid() {
        AppContext.getAllegroApiService()
                .sendBid(auction.getLotId(), Money.of(maxBid.getAmount(), auction.getAuctionHouseCurrency()))
                .ifPresent(this::updateBidDetails);
    }

    @FXML
    private void handleRefreshMaxBid() {
        AppContext.getAllegroApiService().getBidDetails(auction.getLotId())
                .ifPresent(this::updateBidDetails);
    }

    private void updateBidDetails(AllegroApiService.BidDetails details) {
        currentPrice.setAmount(details.auction().currentPrice().asMultiCurrency().originalAmount());
        currencyLabel.setText(details.auction().currentPrice().currency());
        maxBid.setAmount(details.maxAmount().asMultiCurrency().originalAmount());
        maxBidCurrencyLabel.setText(details.maxAmount().currency());
    }
}