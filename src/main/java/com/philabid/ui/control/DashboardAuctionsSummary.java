package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.philabid.ui.util.TableViewHelpers.setMonetaryColumn;

public class DashboardAuctionsSummary extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(DashboardAuctionsSummary.class);

    @FXML
    private TableView<AuctionsSummaryEntry> tableView;
    @FXML
    private TableColumn<AuctionsSummaryEntry, String> nameColumn;
    @FXML
    private TableColumn<AuctionsSummaryEntry, Long> totalAuctionsColumn;
    @FXML
    private TableColumn<AuctionsSummaryEntry, Long> winningAuctionsColumn;
    @FXML
    private TableColumn<AuctionsSummaryEntry, MultiCurrencyMonetaryAmount> currentBidsColumn;
    @FXML
    private TableColumn<AuctionsSummaryEntry, MultiCurrencyMonetaryAmount> maximumBidsColumn;

    public DashboardAuctionsSummary() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/DashboardAuctionsSummary.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setResources(AppContext.getI18nManager().getResourceBundle()); // Pass the resource bundle

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalAuctionsColumn.setCellValueFactory(new PropertyValueFactory<>("totalAuctions"));
        winningAuctionsColumn.setCellValueFactory(new PropertyValueFactory<>("winningAuctions"));
        setMonetaryColumn(currentBidsColumn, "currentBids");
        setMonetaryColumn(maximumBidsColumn, "maximumBids");

        totalAuctionsColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
        winningAuctionsColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
    }

    public void setAuctions(Collection<Auction> auctions, Function<Auction, Pair<Long, String>> classifier) {
        List<AuctionsSummaryEntry> entries = auctions.stream().collect(Collectors.groupingBy(classifier,
                        Collector.of(AuctionsSummaryEntry::new,
                                (a, b) -> {
                                    a.totalAuctions += 1;
                                    if (b.isWinningBid()) {
                                        a.winningAuctions += 1;
                                        a.currentBids = a.currentBids.add(b.getCurrentPrice());
                                        a.maximumBids = a.maximumBids.add(b.getMaxBid());
                                    }
                                },
                                (a, b) -> {
                                    a.totalAuctions += b.totalAuctions;
                                    a.winningAuctions += b.winningAuctions;
                                    a.currentBids = a.currentBids.add(b.currentBids);
                                    a.maximumBids = a.maximumBids.add(b.maximumBids);
                                    return a;
                                })
                )).entrySet().stream().map(e -> {
                    e.getValue().name = e.getKey().getValue1();
                    return e.getValue();
                })
                .toList();
        tableView.getItems().setAll(entries);

        Platform.runLater(() -> {
            currentBidsColumn.setSortType(TableColumn.SortType.DESCENDING);
            tableView.getSortOrder().setAll(List.of(currentBidsColumn));
        });
    }

    public static class AuctionsSummaryEntry {
        public String name;
        public Long totalAuctions = 0L;
        public Long winningAuctions = 0L;
        public MultiCurrencyMonetaryAmount currentBids = MultiCurrencyMonetaryAmount.ZERO;
        public MultiCurrencyMonetaryAmount maximumBids = MultiCurrencyMonetaryAmount.ZERO;

        public Long getWinningAuctions() {
            return winningAuctions;
        }

        public MultiCurrencyMonetaryAmount getCurrentBids() {
            return currentBids;
        }

        public MultiCurrencyMonetaryAmount getMaximumBids() {
            return maximumBids;
        }

        public String getName() {
            return name;
        }

        public Long getTotalAuctions() {
            return totalAuctions;
        }
    }
}