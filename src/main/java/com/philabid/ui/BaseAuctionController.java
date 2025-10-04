package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.Auction;
import com.philabid.parsing.UrlParsingService;
import com.philabid.service.*;
import com.philabid.ui.cell.MonetaryAmountCell;
import com.philabid.ui.cell.RightAlignedDateCell;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * An abstract base controller containing shared logic for auction views.
 */
public abstract class BaseAuctionController extends BaseTableViewController<Auction> {

    private static final Logger logger = LoggerFactory.getLogger(BaseAuctionController.class);

    @FXML
    protected TableColumn<Auction, String> auctionHouseColumn;
    @FXML
    protected TableColumn<Auction, String> categoryColumn;
    @FXML
    protected TableColumn<Auction, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    protected TableColumn<Auction, String> conditionColumn;
    @FXML
    protected TableColumn<Auction, MonetaryAmount> currentPriceColumn;
    @FXML
    protected TableColumn<Auction, MonetaryAmount> catalogValueColumn;
    @FXML
    protected TableColumn<Auction, LocalDateTime> endDateColumn;

    protected AuctionService auctionService;
    protected AuctionHouseService auctionHouseService;
    protected AuctionItemService auctionItemService;
    protected ConditionService conditionService;
    protected CurrencyService currencyService;
    protected CategoryService categoryService;
    protected I18nManager i18nManager;
    protected UrlParsingService urlParsingService;
    protected HostServices hostServices;

    @Override
    protected void initializeView() {
        // Add double-click listener to open the edit dialog
        table.setRowFactory(tv -> {
            TableRow<Auction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    handleEditAuction();
                }
            });
            return row;
        });
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

    @Override
    protected void setupTableColumns() {
        auctionHouseColumn.setCellValueFactory(new PropertyValueFactory<>("auctionHouseName"));

        categoryColumn.setCellValueFactory(CellValueFactoryProvider.forCategoryInfo(Auction::getAuctionItemCategoryCode,
                Auction::getAuctionItemCategoryName));

        catalogNumberColumn.setCellValueFactory(
                CellValueFactoryProvider.forCatalogNumber(Auction::getAuctionItemCatalogNumber,
                        Auction::getAuctionItemOrderNumber));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);

        conditionColumn.setCellValueFactory(
                CellValueFactoryProvider.forConditionInfo(Auction::getConditionCode, Auction::getConditionName));

        // Use a reusable cell factory and add specific styling for the current price
        currentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
        currentPriceColumn.setCellFactory(column -> new MonetaryAmountCell<>((
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

        catalogValueColumn.setCellValueFactory(new PropertyValueFactory<>("catalogValue"));
        catalogValueColumn.setCellFactory(column -> new MonetaryAmountCell<>());

        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateColumn.setCellFactory(column -> new RightAlignedDateCell<>());

        customizeTableColumns();
    }

    protected void customizeTableColumns() {

    }

    public abstract List<Auction> loadAuctions();

    protected List<Auction> loadTableItems() {
        return loadAuctions();
    }

    @FXML
    protected void handleAddAuction() {
        logger.info("Add auction button clicked.");
        Auction newAuction = new Auction();
        boolean saveClicked = showAuctionEditDialog(newAuction);
        if (saveClicked) {
            auctionService.saveAuction(newAuction);
            loadAuctions();
        }
    }

    @FXML
    protected void handleEditAuction() {
        Auction selected = table.getSelectionModel().getSelectedItem();
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
    protected void handleDeleteAuction() {
        Auction selected = table.getSelectionModel().getSelectedItem();
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

    protected boolean showAuctionEditDialog(Auction auction) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AuctionEditDialog.fxml"));
            loader.setResources(i18nManager.getResourceBundle());
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(auction.getId() == null ? "Add Auction" : "Edit Auction");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(table.getScene().getWindow());
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