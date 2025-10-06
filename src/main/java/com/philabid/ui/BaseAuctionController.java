package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.ui.cell.MultiCurrencyMonetaryAmountCell;
import com.philabid.ui.cell.RightAlignedDateCell;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> currentPriceColumn;
    @FXML
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> catalogValueColumn;
    @FXML
    protected TableColumn<Auction, LocalDateTime> endDateColumn;

    @Override
    protected void initializeView() {
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
        currentPriceColumn.setCellFactory(column -> new MultiCurrencyMonetaryAmountCell<>((
                (auction, labels) -> { // This is a BiConsumer<Auction, List<Label>>
                    if (auction != null && auction.getCatalogValue() != null) {
                        MultiCurrencyMonetaryAmount currentPrice = auction.getCurrentPrice();
                        MultiCurrencyMonetaryAmount catalogValue = auction.getCatalogValue();
                        if (currentPrice != null && currentPrice.defaultCurrencyAmount()
                                .isGreaterThan(catalogValue.defaultCurrencyAmount())) {
                            labels.forEach(label -> label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"));
                        }
                    }
                })));

        catalogValueColumn.setCellValueFactory(new PropertyValueFactory<>("catalogValue"));
        catalogValueColumn.setCellFactory(column -> new MultiCurrencyMonetaryAmountCell<>());

        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateColumn.setCellFactory(column -> new RightAlignedDateCell<>());
    }

    public abstract List<Auction> loadAuctions();

    protected List<Auction> loadTableItems() {
        return loadAuctions();
    }

    @Override
    protected void handleDoubleClick() {
        handleEditAuction();
    }

    @FXML
    protected void handleAddAuction() {
        logger.info("Add auction button clicked.");
        Auction newAuction = new Auction();
        boolean saveClicked = showAuctionEditDialog(newAuction);
        if (saveClicked) {
            AppContext.getAuctionService().saveAuction(newAuction);
            refreshTable();
        }
    }

    @FXML
    protected void handleEditAuction() {
        Auction selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit auction button clicked for auction ID: {}", selected.getId());
            boolean saveClicked = showAuctionEditDialog(selected);
            if (saveClicked) {
                AppContext.getAuctionService().saveAuction(selected);
                refreshTable();
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
                boolean deleted = AppContext.getAuctionService().deleteAuction(selected.getId());
                if (deleted) {
                    refreshTable();
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
            loader.setResources(AppContext.getI18nManager().getResourceBundle());
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(auction.getId() == null ? "Add Auction" : "Edit Auction");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(table.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AuctionEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAuction(auction);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            logger.error("Failed to load the auction edit dialog.", e);
            return false;
        }
    }
}