package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DashboardController implements RefreshableViewController {
    private final static Logger logger = LoggerFactory.getLogger(DashboardController.class);
    @FXML
    private Label activeAuctionsLabel;
    @FXML
    private Label expiredAuctionsLabel;
    @FXML
    private Label activeBidsLabel;
    @FXML
    private Label winningBidsLabel;
    @FXML
    private Label maxBidsValueLabel;
    @FXML
    private Label currentBidsValueLabel;

    @FXML
    private void initialize() {
        refresh();
    }

    @Override
    public void refresh() {
        logger.info("Refreshing Dashboard");

        Collection<Auction> auctions = AppContext.getAuctionService().getActiveAuctions(List.of());

        activeAuctionsLabel.setText(String.valueOf(auctions.size()));
        expiredAuctionsLabel.setText(String.valueOf(auctions.stream().filter(Auction::isFinished).count()));

        Collection<Auction> activeBids = auctions.stream().filter(a -> !Objects.isNull(a.getMaxBid())).toList();
        Collection<Auction> winningBids = activeBids.stream().filter(a -> a.getMaxBid().defaultCurrencyAmount()
                .isGreaterThanOrEqualTo(a.getCurrentPrice().defaultCurrencyAmount())).toList();
        activeBidsLabel.setText(String.valueOf(activeBids.size()));
        winningBidsLabel.setText(String.valueOf(winningBids.size()));

        maxBidsValueLabel.setText(winningBids.stream()
                .map(a -> a.getMaxBid().defaultCurrencyAmount().getNumber().numberValue(BigDecimal.class))
                .reduce(BigDecimal::add)
                .map(v -> v.setScale(2, RoundingMode.HALF_UP) + " " +
                        AppContext.getConfigurationService().getDefaultCurrency().getCurrencyCode())
                .orElse(""));

        currentBidsValueLabel.setText(winningBids.stream()
                .map(a -> a.getCurrentPrice().defaultCurrencyAmount().getNumber().numberValue(BigDecimal.class))
                .reduce(BigDecimal::add)
                .map(v -> v.setScale(2, RoundingMode.HALF_UP) + " " +
                        AppContext.getConfigurationService().getDefaultCurrency().getCurrencyCode())
                .orElse(""));
    }

    @Override
    public void unload() {
    }
}
