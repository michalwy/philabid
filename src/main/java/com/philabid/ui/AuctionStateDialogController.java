package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.ui.control.MonetaryField;
import com.philabid.ui.control.ValuationDetails;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.javamoney.moneta.Money;

public class AuctionStateDialogController {

    private static boolean lastArchiveIfFinished = true;
    @FXML
    private Hyperlink urlLink;
    @FXML
    private Label endingLabel;
    @FXML
    private MonetaryField currentPrice;
    @FXML
    private MonetaryField maxBid;
    @FXML
    private Label maxBidCurrencyLabel;
    @FXML
    private MonetaryField startingPrice;
    @FXML
    private Label startingPriceCurrencyLabel;
    @FXML
    private Label currencyLabel;
    @FXML
    private CheckBox archiveIfFinishedCheckBox;
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

        urlLink.setText(auction.getTradingItemCatalogNumber() + "  (" + auction.getTradingItemCategoryName() + ", " +
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

        endingLabel.setText(auction.getEndDate().format(AppContext.getI18nManager().getDateTimeFormatter()));

        if (auction.getCurrentPrice() != null) {
            currentPrice.setAmount(auction.getCurrentPrice().originalAmount());
            currencyLabel.setText(auction.getCurrentPrice().getOriginalCurrency().getCurrencyCode());
        } else {
            currencyLabel.setText(auction.getCurrency().getCurrencyCode());
        }

        if (auction.getMaxBid() != null) {
            maxBid.setAmount(auction.getMaxBid().originalAmount());
            maxBidCurrencyLabel.setText(auction.getMaxBid().getOriginalCurrency().getCurrencyCode());
        } else {
            maxBidCurrencyLabel.setText(auction.getCurrency().getCurrencyCode());
        }

        if (auction.getStartingPrice() != null) {
            startingPrice.setAmount(auction.getStartingPrice().originalAmount());
            startingPriceCurrencyLabel.setText(auction.getStartingPrice().getOriginalCurrency().getCurrencyCode());
        } else {
            startingPriceCurrencyLabel.setText(auction.getCurrency().getCurrencyCode());
        }

        archiveIfFinishedCheckBox.setSelected(lastArchiveIfFinished);

        initiallyArchived = auction.isArchived();

        valuationDetails.setTradingItem(auction.getTradingItemId(), auction.getConditionId());

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
            auction.setCurrentPrice(Money.of(currentPrice.getAmount(), auction.getCurrency()));
        } else {
            auction.setCurrentPrice(null);
        }

        if (!maxBid.isEmpty()) {
            auction.setMaxBid(Money.of(maxBid.getAmount(), auction.getCurrency()));
        } else {
            auction.setMaxBid(null);
        }

        if (!startingPrice.isEmpty()) {
            auction.setStartingPrice(Money.of(startingPrice.getAmount(), auction.getCurrency()));
        } else {
            auction.setStartingPrice(null);
        }

        if (auction.isFinished() && archiveIfFinishedCheckBox.isSelected()) {
            auction.setArchived(true);
            if (!initiallyArchived && auction.getCatalogValue() != null) {
                setArchivedValues();
            }
        }
        lastArchiveIfFinished = archiveIfFinishedCheckBox.isSelected();

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