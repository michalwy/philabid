package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.Auction;
import com.philabid.model.AuctionStatus;
import com.philabid.parsing.UrlParsingService;
import com.philabid.service.*;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import com.philabid.ui.util.MonetaryColumnValue;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Auction management view (AuctionView.fxml).
 */
public class AuctionController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);
    private final ObservableList<Auction> auctionList = FXCollections.observableArrayList();
    @FXML
    private TableView<Auction> auctionTable;
    @FXML
    private TableColumn<Auction, String> auctionHouseColumn;
    @FXML
    private TableColumn<Auction, String> categoryColumn;
    @FXML
    private TableColumn<Auction, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<Auction, String> urlColumn;
    @FXML
    private TableColumn<Auction, String> conditionColumn;
    @FXML
    private TableColumn<Auction, MonetaryColumnValue> currentPriceColumn;
    @FXML
    private TableColumn<Auction, MonetaryColumnValue> catalogValueColumn;
    @FXML
    private TableColumn<Auction, LocalDateTime> endDateColumn;
    // The status column is still here, but we removed the logic for calculating it dynamically for now.
    @FXML
    private TableColumn<Auction, AuctionStatus> statusColumn;
    private AuctionService auctionService;
    private AuctionHouseService auctionHouseService;
    private AuctionItemService auctionItemService;
    private ConditionService conditionService;
    private CurrencyService currencyService;
    private CategoryService categoryService;
    private I18nManager i18nManager;
    private UrlParsingService urlParsingService;
    private HostServices hostServices;

    private boolean showArchived = false;

    @FXML
    private void initialize() {
        setupTableColumns();
        auctionTable.setItems(auctionList);

        // Add double-click listener to open the edit dialog
        auctionTable.setRowFactory(tv -> {
            TableRow<Auction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    handleEditAuction();
                }
            });
            return row;
        });

        logger.debug("AuctionController initialized.");
    }

    public void configure(boolean showArchived) {
        this.showArchived = showArchived;
    }

    public void setServices(AuctionService auctionService, AuctionHouseService auctionHouseService,
                            AuctionItemService auctionItemService, ConditionService conditionService,
                            CurrencyService currencyService, I18nManager i18nManager, CategoryService categoryService,
                            UrlParsingService urlParsingService, HostServices hostServices) {
        this.auctionService = auctionService;
        this.auctionHouseService = auctionHouseService;
        this.auctionItemService = auctionItemService;
        this.conditionService = conditionService;
        this.currencyService = currencyService;
        this.categoryService = categoryService;
        this.i18nManager = i18nManager;
        this.urlParsingService = urlParsingService;
        this.hostServices = hostServices;
    }

    private void setupTableColumns() {
        auctionHouseColumn.setCellValueFactory(new PropertyValueFactory<>("auctionHouseName"));

        categoryColumn.setCellValueFactory(CellValueFactoryProvider.forCategoryInfo(Auction::getAuctionItemCategoryCode,
                Auction::getAuctionItemCategoryName));

        catalogNumberColumn.setCellValueFactory(
                CellValueFactoryProvider.forCatalogNumber(Auction::getAuctionItemCatalogNumber,
                        Auction::getAuctionItemOrderNumber));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);

        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.setCellFactory(column -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setOnAction(event -> {
                    if (hostServices != null && !link.getText().isEmpty()) {
                        hostServices.showDocument(link.getText());
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : link);
                link.setText(item);
            }
        });

        conditionColumn.setCellValueFactory(
                CellValueFactoryProvider.forConditionInfo(Auction::getConditionCode, Auction::getConditionName));

        currentPriceColumn.setCellValueFactory(CellValueFactoryProvider.forValueWithCurrency(Auction::getCurrentPrice));
        currentPriceColumn.setComparator(MonetaryColumnValue.SORT_COMPARATOR);
        // Use a reusable cell factory and add specific styling for the current price
        currentPriceColumn.setCellFactory(CellValueFactoryProvider.forMonetaryValue((
                (auction, labels) -> { // This is a BiConsumer<Auction, List<Label>>
                    if (auction != null && auction.getCatalogValue() != null) {
                        MonetaryAmount currentPrice = auction.getCurrentPrice();
                        MonetaryAmount catalogValue = auction.getCatalogValue();
                        if (currentPrice != null && currentPrice.getCurrency().equals(catalogValue.getCurrency()) &&
                                currentPrice.isGreaterThan(catalogValue)) {
                            labels.forEach(label -> label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"));
                        }
                    }
                })));

        catalogValueColumn.setCellValueFactory(CellValueFactoryProvider.forValueWithCurrency(Auction::getCatalogValue));
        catalogValueColumn.setComparator(MonetaryColumnValue.SORT_COMPARATOR);
        // Use the same reusable cell factory for the catalog value, without special styling
        catalogValueColumn.setCellFactory(CellValueFactoryProvider.forMonetaryValue(null));

        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Formatter for the end date column
        endDateColumn.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
    }

    public void loadAuctions() {
        if (auctionService != null) {
            List<Auction> auctionsToLoad = showArchived
                    ? auctionService.getArchivedAuctions()
                    : auctionService.getActiveAuctions();
            auctionList.setAll(auctionsToLoad);
            auctionTable.sort();
            logger.info("Loaded {} auctions into the table.", auctionList.size());
        } else {
            logger.warn("AuctionService is not available. Cannot load data.");
        }
    }

    @FXML
    private void handleAddAuction() {
        logger.info("Add auction button clicked.");
        Auction newAuction = new Auction();
        boolean saveClicked = showAuctionEditDialog(newAuction);
        if (saveClicked) {
            auctionService.saveAuction(newAuction);
            loadAuctions();
        }
    }

    @FXML
    private void handleEditAuction() {
        Auction selected = auctionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit auction button clicked for auction ID: {}", selected.getId());
            boolean saveClicked = showAuctionEditDialog(selected);
            if (saveClicked) {
                auctionService.saveAuction(selected);
                loadAuctions();
            }
        }
    }

    @FXML
    private void handleDeleteAuction() {
        Auction selected = auctionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete auction button clicked for auction ID: {}", selected.getId());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Auction");
            alert.setContentText("Are you sure you want to delete the selected auction?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = auctionService.deleteAuction(selected.getId());
                if (deleted) {
                    loadAuctions();
                } else {
                    logger.error("Failed to delete auction with ID: {}", selected.getId());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    private boolean showAuctionEditDialog(Auction auction) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AuctionEditDialog.fxml"));
            loader.setResources(i18nManager.getResourceBundle());
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(auction.getId() == null ? "Add Auction" : "Edit Auction");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(auctionTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AuctionEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setServices(auctionHouseService, auctionItemService, conditionService, categoryService,
                    currencyService, i18nManager, urlParsingService);
            controller.setAuction(auction);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            logger.error("Failed to load the auction edit dialog.", e);
            return false;
        }
    }
}
