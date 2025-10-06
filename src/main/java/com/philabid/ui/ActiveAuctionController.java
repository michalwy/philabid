package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.ui.cell.EndingDateCell;
import com.philabid.model.Auction;
import com.philabid.ui.cell.ThresholdMultiCurrencyMonetaryAmountCell;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
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

public class ActiveAuctionController extends BaseAuctionController {

    private static final Logger logger = LoggerFactory.getLogger(ActiveAuctionController.class);

    @FXML
    protected TableColumn<Auction, String> urlColumn;

    @FXML
    protected TableColumn<Auction, MultiCurrencyMonetaryAmount> maxBidColumn;

    @Override
    public List<Auction> loadAuctions() {
        return AppContext.getAuctionService().getActiveAuctions();
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
            row.getStyleClass().remove("winning-auction");

            if (empty || auction == null) {
                return;
            }

            if (auction.getMaxBid() != null && auction.getCurrentPrice() != null && auction.getMaxBid().originalAmount()
                    .isGreaterThanOrEqualTo(auction.getCurrentPrice().originalAmount())) {
                row.getStyleClass().add("winning-auction");
            }
        });

        endDateColumn.setCellFactory(column -> new EndingDateCell<>());

        Platform.runLater(() -> {
            endDateColumn.setSortType(TableColumn.SortType.ASCENDING);
            table.getSortOrder().setAll(List.of(endDateColumn));
        });
    }

    @Override
    protected List<MenuItem> getContextMenuItems() {
        MenuItem archiveItem = new MenuItem("Edit state...");
        archiveItem.setOnAction(event -> handleEditState());

        return List.of(archiveItem);
    }

    private void handleEditState() {
        Auction selectedAuction = table.getSelectionModel().getSelectedItem();
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
            dialogStage.initOwner(table.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            AuctionStateDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAuction(selectedAuction);

            dialogStage.showAndWait();

            EditDialogResult editDialogResult = controller.getEditDialogResult();
            if (editDialogResult != null && editDialogResult.saved()) {
                AppContext.getAuctionService().saveAuction(selectedAuction);

                final int selectedIndex = table.getSelectionModel().getSelectedIndex();
                final boolean archived = selectedAuction.isArchived();

                refreshTable();

                if (editDialogResult.editNext()) {
                    Platform.runLater(() -> {
                        int nextIndex = selectedIndex;
                        if (!archived) {
                            nextIndex++;
                        }
                        table.getSelectionModel().select(nextIndex);
                        table.scrollTo(nextIndex);
                        table.requestFocus();
                        table.getFocusModel().focus(nextIndex);
                        handleEditState();
                    });
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load Archive Auction dialog.", e);
        }
    }

    @Override
    protected void handleDoubleClick() {
        handleEditState();
    }
}