package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import com.philabid.model.CatalogValue;
import com.philabid.ui.cell.EndingDateCell;
import com.philabid.ui.cell.ThresholdMultiCurrencyMonetaryAmountCell;
import com.philabid.ui.control.CrudEditDialog;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ActiveAuctionController extends BaseAuctionController {

    private static final Logger logger = LoggerFactory.getLogger(ActiveAuctionController.class);

    @FXML
    protected TableColumn<Auction, String> urlColumn;

    @FXML
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> maxBidColumn;

    @Override
    public Collection<Auction> loadAuctions(Collection<FilterCondition> filterConditions) {
        return AppContext.getAuctionService().getActiveAuctions(filterConditions);
    }

    @Override
    protected void initializeView() {
        super.initializeView();
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.setCellFactory(column -> new TableCell<>() {
            private final Hyperlink link = new Hyperlink();

            {
                link.setOnAction(event -> {
                    if (!link.getText().isEmpty()) {
                        AppContext.getHostServices().showDocument(link.getText());
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

        maxBidColumn.setCellValueFactory(new PropertyValueFactory<>("maxBid"));
        maxBidColumn.setCellFactory(column -> new ThresholdMultiCurrencyMonetaryAmountCell<>(
                Auction::getRecommendedPrice,
                Auction::getCatalogValue));

        addRowFormatter((row, auction, empty) -> {
            row.setStyle("");
            // Always remove classes first to handle row reuse
            row.getStyleClass().remove("winning-auction");
            row.getStyleClass().remove("expired-auction");

            if (empty || auction == null) {
                return;
            }

            if (auction.isFinished()) {
                row.getStyleClass().add("expired-auction");
            }

            if (auction.getMaxBid() != null && auction.getCurrentPrice() != null && auction.getMaxBid().originalAmount()
                    .isGreaterThanOrEqualTo(auction.getCurrentPrice().originalAmount())) {
                row.getStyleClass().add("winning-auction");
            }
        });

        endDateColumn.setCellFactory(column -> new EndingDateCell<>());

        Platform.runLater(() -> {
            endDateColumn.setSortType(TableColumn.SortType.ASCENDING);
            getTableView().getSortOrder().setAll(List.of(endDateColumn));
        });
    }

    @Override
    protected List<MenuItem> getContextMenuItems() {
        MenuItem archiveItem = new MenuItem("Edit state...");
        archiveItem.setOnAction(event -> handleEditState(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem addCatalogValueItem = new MenuItem("Add Catalog Value...");
        addCatalogValueItem.setOnAction(
                event -> handleAddCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

        MenuItem updateCatalogValueItem = new MenuItem("Update Catalog Value...");
        updateCatalogValueItem.setOnAction(
                event -> handleUpdateCatalogValue(getTableView().getSelectionModel().getSelectedItem()));

        return List.of(archiveItem, addCatalogValueItem, updateCatalogValueItem);
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
            GridPane page = loader.load();

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
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CatalogValueEditDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());

            CrudEditDialog<CatalogValue> page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Catalog Value");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(getTableView().getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CatalogValueEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(catalogValue);

            dialogStage.showAndWait();

            return controller.getEditDialogResult();
        } catch (IOException e) {
            logger.error("Failed to load the catalog value edit dialog.", e);
            return null;
        }
    }

    @Override
    protected void handleDoubleClick() {
        handleEditState(getTableView().getSelectionModel().getSelectedItem());
    }
}