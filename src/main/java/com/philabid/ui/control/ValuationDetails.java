package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.model.Valuation;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static com.philabid.ui.util.TableViewHelpers.*;

public class ValuationDetails extends VBox {

    private static final Logger logger = LoggerFactory.getLogger(ValuationDetails.class);
    private final ObjectProperty<MultiCurrencyMonetaryAmount> selectedPrice = new SimpleObjectProperty<>(null);
    @FXML
    private Label minPriceLabel;
    @FXML
    private Label minPricePercentageLabel;
    @FXML
    private Label maxPriceLabel;
    @FXML
    private Label maxPricePercentageLabel;
    @FXML
    private Label avgPriceLabel;
    @FXML
    private Label avgPricePercentageLabel;
    @FXML
    private Label medianPriceLabel;
    @FXML
    private Label medianPricePercentageLabel;
    @FXML
    private Label categoryAveragePercentageLabel;
    @FXML
    private Label categoryAveragePriceLabel;
    @FXML
    private Label catalogPriceLabel;
    @FXML
    private Label recommendedPriceLabel;
    @FXML
    private Label recommendedPricePercentageLabel;
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

    private Valuation valuation;

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

        recommendedPriceLabel.setOnMouseClicked(event -> {
            if (valuation != null) {
                selectedPrice.set(valuation.getRecommendedPrice());
            }
        });
        minPriceLabel.setOnMouseClicked(event -> {
            if (valuation != null) {
                selectedPrice.set(valuation.getMinPrice());
            }
        });
        maxPriceLabel.setOnMouseClicked(event -> {
            if (valuation != null) {
                selectedPrice.set(valuation.getMaxPrice());
            }
        });
        catalogPriceLabel.setOnMouseClicked(event -> {
            if (valuation != null) {
                selectedPrice.set(valuation.getCatalogValue());
            }
        });
        categoryAveragePriceLabel.setOnMouseClicked(event -> {
            if (valuation != null) {
                selectedPrice.set(valuation.getCategoryAveragePrice());
            }
        });
        avgPriceLabel.setOnMouseClicked(event -> {
            if (valuation != null) {
                selectedPrice.set(valuation.getAveragePrice());
            }
        });
        medianPriceLabel.setOnMouseClicked(event -> {
            if (valuation != null) {
                selectedPrice.set(valuation.getMedianPrice());
            }
        });
    }

    public void setTradingItem(Long tradingItemId, Long conditionId) {
        historyTable.setItems(FXCollections.observableArrayList(AppContext.getAuctionService()
                .getArchivedAuctionsForItem(tradingItemId, conditionId)));

        valuation = AppContext.getValuationService().getForItem(tradingItemId, conditionId).orElse(null);
        if (valuation == null) {
            return;
        }

        setMoneyLabel(minPriceLabel, valuation.getMinPrice(), valuation.getCategoryAveragePrice(),
                valuation.getCatalogValue());
        setMoneyLabel(maxPriceLabel, valuation.getMaxPrice(), valuation.getCategoryAveragePrice(),
                valuation.getCatalogValue());
        setMoneyLabel(avgPriceLabel, valuation.getAveragePrice(), valuation.getCategoryAveragePrice(),
                valuation.getCatalogValue());
        setMoneyLabel(medianPriceLabel, valuation.getMedianPrice(), valuation.getCategoryAveragePrice(),
                valuation.getCatalogValue());
        setMoneyLabel(categoryAveragePriceLabel, valuation.getCategoryAveragePrice(),
                valuation.getCatalogValue(), valuation.getCatalogValue());
        setMoneyLabel(catalogPriceLabel, valuation.getCatalogValue(), null, null);
        setMoneyLabel(recommendedPriceLabel, valuation.getRecommendedPrice(),
                valuation.getCategoryAveragePrice(), valuation.getCatalogValue());

        setPercentageLabel(categoryAveragePercentageLabel, valuation.getCategoryAveragePercentage(), null);
        setPercentageLabel(minPricePercentageLabel, valuation.getMinPrice(), valuation.getCatalogValue(),
                valuation.getCategoryAveragePercentage());
        setPercentageLabel(maxPricePercentageLabel, valuation.getMaxPrice(), valuation.getCatalogValue(),
                valuation.getCategoryAveragePercentage());
        setPercentageLabel(avgPricePercentageLabel, valuation.getAveragePrice(), valuation.getCatalogValue(),
                valuation.getCategoryAveragePercentage());
        setPercentageLabel(medianPricePercentageLabel, valuation.getMedianPrice(), valuation.getCatalogValue(),
                valuation.getCategoryAveragePercentage());
        setPercentageLabel(recommendedPricePercentageLabel, valuation.getRecommendedPrice(),
                valuation.getCatalogValue(),
                valuation.getCategoryAveragePercentage());

        recommendedPriceLabel.setStyle("-fx-font-weight: bold;" + recommendedPriceLabel.getStyle());
        recommendedPricePercentageLabel.setStyle("-fx-font-weight: bold;" + recommendedPriceLabel.getStyle());
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
        formatThresholdLabel(label, amount, warningThreshold, criticalThreshold);
    }

    private void setPercentageLabel(Label label, MultiCurrencyMonetaryAmount value, MultiCurrencyMonetaryAmount ref,
                                    Double warningPercentage) {
        if (value == null || ref == null) {
            return;
        }
        BigDecimal valueNumber = value.defaultCurrencyAmount().getNumber().numberValue(BigDecimal.class);
        BigDecimal refNumber = ref.defaultCurrencyAmount().getNumber().numberValue(BigDecimal.class);
        if (refNumber.equals(BigDecimal.ZERO)) {
            return;
        }
        Double percentage = valueNumber.divide(refNumber, 6, RoundingMode.HALF_UP).doubleValue();
        setPercentageLabel(label, percentage, warningPercentage);
    }

    private void setPercentageLabel(Label label, Double percentage, Double warningPercentage) {
        if (percentage != null) {
            label.setText(String.format(java.util.Locale.ROOT, "(%.0f%%)", percentage * 100.0));
            if (percentage > 1.0) {
                label.setStyle("-fx-text-fill: red;");
            } else if (warningPercentage != null && percentage > warningPercentage) {
                label.setStyle("-fx-text-fill: orange;");
            }
        } else {
            label.setText("");
        }
    }

    private void formatThresholdLabel(Label label, MultiCurrencyMonetaryAmount amount,
                                      MultiCurrencyMonetaryAmount warningThreshold,
                                      MultiCurrencyMonetaryAmount criticalThreshold) {
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

    public ObjectProperty<MultiCurrencyMonetaryAmount> selectedPrice() {
        return selectedPrice;
    }
}