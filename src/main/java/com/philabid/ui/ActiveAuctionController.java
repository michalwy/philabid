package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.ui.cell.MonetaryAmountCell;
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

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ActiveAuctionController extends BaseAuctionController {

    private static final Logger logger = LoggerFactory.getLogger(ActiveAuctionController.class);

    @FXML
    protected TableColumn<Auction, String> urlColumn;

    @FXML
    protected TableColumn<Auction, MonetaryAmount> maxBidColumn;

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
        maxBidColumn.setCellFactory(column -> new MonetaryAmountCell<>((
                (auction, labels) -> { // This is a BiConsumer<Auction, List<Label>>
                    if (auction != null && auction.getCatalogValue() != null) {
                        MonetaryAmount currentPrice = auction.getMaxBid();
                        MonetaryAmount catalogValue = auction.getCatalogValue();
                        if (currentPrice != null && currentPrice.getCurrency().equals(catalogValue.getCurrency()) &&
                                currentPrice.isGreaterThan(catalogValue)) {
                            labels.forEach(label -> label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"));
                        }
                    }
                })));

        addRowFormatter((row, auction, empty) -> {
            row.setStyle("");
            row.getStyleClass().remove("expired-auction");

            if (empty || auction == null) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            if (auction.getEndDate().isBefore(now)) {
                row.setStyle("-fx-opacity: 0.5;");
                row.getStyleClass().add("expired-auction");
            } else if (auction.getEndDate().isBefore(now.plusHours(24))) {
                row.setStyle("-fx-font-weight: bold;");
            }
        });

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
            if (editDialogResult != null && editDialogResult.saveClicked()) {
                AppContext.getAuctionService().saveAuction(selectedAuction);

                final int selectedIndex = table.getSelectionModel().getSelectedIndex();

                refreshTable();

                if (editDialogResult.addAnother()) {
                    Platform.runLater(() -> {
                        table.getSelectionModel().select(selectedIndex + 1);
                        table.scrollTo(selectedIndex + 1);
                        table.requestFocus();
                        table.getFocusModel().focus(selectedIndex + 1);
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