package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.model.CatalogValue;
import com.philabid.ui.cell.CatalogNumberColumnValue;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.philabid.ui.util.TableViewHelpers.*;

public class DashboardController implements RefreshableViewController {
    private final static Logger logger = LoggerFactory.getLogger(DashboardController.class);
    protected final ObservableList<Auction> catalogValueNeededTableItems = FXCollections.observableArrayList();

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
    private TableView<Auction> catalogValueNeededTable;
    @FXML
    private TableColumn<Auction, String> catalogValueNeededCategoryColumn;
    @FXML
    private TableColumn<Auction, CatalogNumberColumnValue> catalogValueNeededCatalogNumberColumn;
    @FXML
    private TableColumn<Auction, String> catalogValueNeededConditionColumn;
    @FXML
    private TableColumn<Auction, String> catalogValueNeededCatalogColumn;
    @FXML
    private TableColumn<Auction, MultiCurrencyMonetaryAmount> catalogValueNeededCatalogValueColumn;

    @FXML
    private void initialize() {
        setupCatalogValueNeededTable();
        refresh();
    }

    private void setupCatalogValueNeededTable() {
        setCategoryColumn(catalogValueNeededCategoryColumn, Auction::getAuctionItemCategoryCode,
                Auction::getAuctionItemCategoryName);
        setCatalogNumberColumn(catalogValueNeededCatalogNumberColumn, Auction::getAuctionItemCatalogNumber,
                Auction::getAuctionItemOrderNumber);
        setConditionColumn(catalogValueNeededConditionColumn, Auction::getConditionCode, Auction::getConditionName);
        setCatalogColumn(catalogValueNeededCatalogColumn, Auction::getCatalogName, Auction::getCatalogIssueYear);
        setCatalogValueColumn(catalogValueNeededCatalogValueColumn, "catalogValue", Auction::getCatalogValue,
                Auction::isCatalogActive);

        MenuItem updateCatalogValueItem = new MenuItem("Set catalog value...");
        updateCatalogValueItem.setOnAction(event -> {
        });
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().setAll(List.of(updateCatalogValueItem));
        catalogValueNeededTable.setContextMenu(contextMenu);
        contextMenu.setOnShowing(e -> {
            if (catalogValueNeededTable.getSelectionModel().isEmpty()) {
                e.consume(); // Don't show the menu
            }
        });

        catalogValueNeededTable.setRowFactory(param -> {
            TableRow<Auction> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    handleCatalogValueNeededTableDoubleClick();
                }
            });

            return row;
        });

        catalogValueNeededTable.setItems(catalogValueNeededTableItems);
    }

    private void handleCatalogValueNeededTableDoubleClick() {
        Auction auction = catalogValueNeededTable.getSelectionModel().getSelectedItem();
        if (auction == null) return;

        CatalogValue catalogValue = AppContext.getCatalogValueService()
                .findByAuctionItemAndCondition(auction.getAuctionItemId(), auction.getConditionId())
                .orElse(new CatalogValue());

        catalogValue.setAuctionItemId(auction.getAuctionItemId());
        catalogValue.setConditionId(auction.getConditionId());
        catalogValue.setCatalogId(null);

        EditDialogResult result = CatalogValueEditDialogController.showCatalogValueEditDialog(
                catalogValueNeededTable.getScene().getWindow(),
                catalogValue);

        if (result != null && result.saved()) {

            final int selectedIndex = catalogValueNeededTable.getSelectionModel().getSelectedIndex();

            refreshTables();

            Platform.runLater(() -> {
                catalogValueNeededTable.getSelectionModel().select(selectedIndex);
                catalogValueNeededTable.requestFocus();
                catalogValueNeededTable.getFocusModel().focus(selectedIndex);
                handleCatalogValueNeededTableDoubleClick();
            });
        }
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

        refreshTables();
    }

    public void refreshTables() {
        Collection<Auction> auctions = AppContext.getAuctionService().getActiveAuctions(List.of());

        catalogValueNeededTableItems.setAll(auctions.stream().filter(a -> !a.isCatalogActive()).toList());
    }

    @Override
    public void unload() {
        catalogValueNeededTableItems.clear();
    }
}
