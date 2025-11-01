package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import com.philabid.model.CatalogValue;
import com.philabid.ui.cell.EndingDateCell;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.philabid.ui.util.TableViewHelpers.*;

public class ActiveAuctionController extends BaseAuctionController {

    private static final Logger logger = LoggerFactory.getLogger(ActiveAuctionController.class);

    @FXML
    protected TableColumn<Auction, String> urlColumn;
    @FXML
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> recommendedPriceColumn;

    @Override
    public Collection<Auction> loadAuctions(Collection<FilterCondition> filterConditions) {
        return AppContext.getAuctionService().getActiveAuctions(filterConditions);
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        setUrlColumn(urlColumn, "url");

        endDateColumn.setCellFactory(column -> new EndingDateCell<>());

        setPriceWithThresholdColumn(recommendedPriceColumn, "recommendedPrice", Auction::getCatalogValue,
                Auction::getCatalogValue);

        setCatalogNumberWithWarningColumn(catalogNumberColumn, Auction::getAuctionItemCatalogNumber,
                Auction::getAuctionItemOrderNumber, t -> t.getActiveAuctions().size() > 1);

        addRowFormatter((row, auction, empty) -> {
            row.getStyleClass().remove("expired-auction");

            if (empty || auction == null) {
                return;
            }

            if (auction.isFinished()) {
                row.getStyleClass().add("expired-auction");
            }
        });

        Platform.runLater(() -> {
            endDateColumn.setSortType(TableColumn.SortType.ASCENDING);
            getTableView().getSortOrder().setAll(List.of(endDateColumn));
        });
    }

    @Override
    protected List<MenuItem> getContextMenuItems() {
        MenuItem archiveItem = new MenuItem("Edit state...");
        archiveItem.setOnAction(event -> handleEditState(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem showHistoricalAuctions = new MenuItem("Show historical auctions...");
        showHistoricalAuctions.setOnAction(
                event -> handleEditState(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem addCatalogValueItem = new MenuItem("Add Catalog Value...");
        addCatalogValueItem.setOnAction(
                event -> handleAddCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem updateCatalogValueItem = new MenuItem("Update Catalog Value...");
        updateCatalogValueItem.setOnAction(
                event -> handleUpdateCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

        return List.of(showHistoricalAuctions, archiveItem, addCatalogValueItem, updateCatalogValueItem);
    }

    @Override
    protected void onContextMenuShowing(ContextMenu contextMenu) {
        Auction selectedAuction = getTableView().getSelectionModel().getSelectedItem();
        if (selectedAuction == null) return;

        // Show/hide context menu items based on the auction's state
        contextMenu.getItems().stream()
                .filter(item -> "Add Catalog Value...".equals(item.getText()))
                .findFirst()
                .ifPresent(menuItem -> {
                    // Show this option only if catalog value is missing
                    menuItem.setVisible(selectedAuction.getCatalogValue() == null);
                });

        contextMenu.getItems().stream()
                .filter(item -> "Update Catalog Value...".equals(item.getText()))
                .findFirst()
                .ifPresent(menuItem -> {
                    // Show this option only if catalog value exists and is from an inactive catalog
                    menuItem.setVisible(
                            selectedAuction.getCatalogValue() != null && !selectedAuction.isCatalogActive());
                });
    }

    private void handleAddCatalogValue(Auction auction) {
        if (auction == null) return;

        CatalogValue newCatalogValue = new CatalogValue();
        // Pre-populate with data from the auction
        newCatalogValue.setAuctionItemId(auction.getAuctionItemId());
        newCatalogValue.setConditionId(auction.getConditionId());

        EditDialogResult result = showCatalogValueEditDialog(newCatalogValue);
        if (result != null && result.saved()) {
            refreshTable(); // Refresh to show the new value
        }
    }

    private void handleUpdateCatalogValue(Auction auction) {
        if (auction == null) return;

        // Find the existing CatalogValue to edit it
        AppContext.getCatalogValueService()
                .findByAuctionItemAndCondition(auction.getAuctionItemId(), auction.getConditionId())
                .ifPresent(catalogValueToUpdate -> {
                    // We want the user to select a new, active catalog, so we don't pre-set the old one.
                    catalogValueToUpdate.setCatalogId(null);
                    EditDialogResult result = showCatalogValueEditDialog(catalogValueToUpdate);
                    if (result != null && result.saved()) {
                        refreshTable();
                    }
                });
    }

    private void handleEditState(Auction selectedAuction) {
        if (selectedAuction == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuctionStateDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());
            VBox page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Auction State");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(getTableView().getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            AuctionStateDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAuction(selectedAuction);

            dialogStage.showAndWait();

            EditDialogResult editDialogResult = controller.getEditDialogResult();
            if (editDialogResult != null && editDialogResult.saved()) {
                AppContext.getAuctionService().save(selectedAuction);

                final int selectedIndex = getTableView().getSelectionModel().getSelectedIndex();
                final boolean archived = selectedAuction.isArchived();

                refreshTable();

                if (editDialogResult.editNext()) {
                    Platform.runLater(() -> {
                        int nextIndex = selectedIndex;
                        if (!archived) {
                            nextIndex++;
                        }
                        getTableView().getSelectionModel().select(nextIndex);
                        getTableView().requestFocus();
                        getTableView().getFocusModel().focus(nextIndex);
                        handleEditState(getTableView().getSelectionModel().getSelectedItem());
                    });
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load Archive Auction dialog.", e);
        }
    }

    private EditDialogResult showCatalogValueEditDialog(CatalogValue catalogValue) {
        return CatalogValueEditDialogController.showCatalogValueEditDialog(getTableView().getScene().getWindow(),
                catalogValue);
    }

    @Override
    protected void handleDoubleClick() {
        handleEditState(getTableView().getSelectionModel().getSelectedItem());
    }
}