package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.philabid.ui.util.TableViewHelpers.*;

public class ValuationDetails extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(ValuationDetails.class);

    @FXML
    private Label minPriceLabel;
    @FXML
    private Label maxPriceLabel;
    @FXML
    private Label avgPriceLabel;
    @FXML
    private Label categoryAveragePercentageLabel;
    @FXML
    private Label categoryAveragePriceLabel;
    @FXML
    private Label catalogPriceLabel;
    @FXML
    private Label recommendedPriceLabel;
    @FXML
    private TableView<Auction> historyTable;
    @FXML
    private TableColumn<Auction, String> auctionHouseColumn;
    @FXML
    private TableColumn<Auction, String> urlColumn;
    @FXML
    private TableColumn<Auction, LocalDateTime> endDateColumn;
    @FXML
    private TableColumn<Auction, MultiCurrencyMonetaryAmount> finalPriceColumn;

    public ValuationDetails() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/control/ValuationDetails.fxml"));
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
        auctionHouseColumn.setCellValueFactory(new PropertyValueFactory<>("auctionHouseName"));
        auctionHouseColumn.setStyle("-fx-alignment: CENTER_LEFT;");

        setUrlColumn(urlColumn, "url");
        setDateTimeColumn(endDateColumn, "endDate");
        setMonetaryColumn(finalPriceColumn, "currentPrice");

        Platform.runLater(() -> {
            endDateColumn.setSortType(TableColumn.SortType.DESCENDING);
            historyTable.getSortOrder().setAll(List.of(endDateColumn));
        });
    }

    public void setAuctionItem(Long auctionItemId, Long conditionId) {
        historyTable.setItems(FXCollections.observableArrayList(AppContext.getAuctionService()
                .getArchivedAuctionsForItem(auctionItemId, conditionId)));

        AppContext.getValuationService().getForItem(auctionItemId, conditionId)
                .ifPresent(valuation -> {
                    setMoneyLabel(minPriceLabel, valuation.getMinPrice(), valuation.getCategoryAveragePrice(),
                            valuation.getCatalogValue());
                    setMoneyLabel(maxPriceLabel, valuation.getMaxPrice(), valuation.getCategoryAveragePrice(),
                            valuation.getCatalogValue());
                    setMoneyLabel(avgPriceLabel, valuation.getAveragePrice(), valuation.getCategoryAveragePrice(),
                            valuation.getCatalogValue());
                    setMoneyLabel(categoryAveragePriceLabel, valuation.getCategoryAveragePrice(),
                            valuation.getCatalogValue(), valuation.getCatalogValue());
                    setMoneyLabel(catalogPriceLabel, valuation.getCatalogValue(), null, null);
                    setMoneyLabel(recommendedPriceLabel, valuation.getRecommendedPrice(),
                            valuation.getCategoryAveragePrice(), valuation.getCatalogValue());
                    recommendedPriceLabel.setStyle("-fx-font-weight: bold;" + recommendedPriceLabel.getStyle());

                    if (valuation.getCategoryAveragePercentage() != null) {
                        categoryAveragePercentageLabel.setText(
                                String.format(java.util.Locale.ROOT, "%.0f%%",
                                        valuation.getCategoryAveragePercentage() * 100.0));
                        if (valuation.getCategoryAveragePercentage() > 1.0) {
                            categoryAveragePercentageLabel.setStyle("-fx-text-fill: red;");
                        }
                    } else {
                        categoryAveragePercentageLabel.setText("-");
                    }
                });
    }

    private String formatMoney(MultiCurrencyMonetaryAmount amount) {
        if (amount == null) {
            return "-";
        }
        return String.format(java.util.Locale.ROOT, "%.2f %s", amount.defaultCurrencyAmount().getNumber().numberValue(
                        BigDecimal.class),
                amount.defaultCurrencyAmount().getCurrency().getCurrencyCode());
    }

    private void setMoneyLabel(Label label, MultiCurrencyMonetaryAmount amount,
                               MultiCurrencyMonetaryAmount warningThreshold,
                               MultiCurrencyMonetaryAmount criticalThreshold) {
        label.setText(formatMoney(amount));
        if (amount == null) {
            return;
        }
        if (criticalThreshold != null &&
                amount.defaultCurrencyAmount().isGreaterThan(criticalThreshold.defaultCurrencyAmount())) {
            label.setStyle("-fx-text-fill: red;");
        } else if (warningThreshold != null &&
                amount.defaultCurrencyAmount().isGreaterThan(warningThreshold.defaultCurrencyAmount())) {
            label.setStyle("-fx-text-fill: orange;");
        }
    }
}