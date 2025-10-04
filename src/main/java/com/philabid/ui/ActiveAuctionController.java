package com.philabid.ui;

import com.philabid.model.Auction;
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
import java.util.List;

public class ActiveAuctionController extends BaseAuctionController {

    private static final Logger logger = LoggerFactory.getLogger(ActiveAuctionController.class);

    @FXML
    protected TableColumn<Auction, String> urlColumn;

    @Override
    public List<Auction> loadAuctions() {
        if (auctionService != null) {
            return auctionService.getActiveAuctions();
        } else {
            return List.of();
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        setupContextMenu();
    }

    @Override
    protected void customizeTableColumns() {
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
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem archiveItem = new MenuItem("Archive...");
        archiveItem.setOnAction(event -> handleArchiveAuction());

        contextMenu.getItems().add(archiveItem);

        // Show context menu only for non-empty rows
        table.setContextMenu(contextMenu);
        contextMenu.setOnShowing(e -> {
            if (table.getSelectionModel().isEmpty()) {
                e.consume(); // Don't show the menu
            }
        });
    }

    private void handleArchiveAuction() {
        Auction selectedAuction = table.getSelectionModel().getSelectedItem();
        if (selectedAuction == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ArchiveAuctionDialog.fxml"));
            loader.setResources(i18nManager.getResourceBundle());
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Archive Auction");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(table.getScene().getWindow());
            dialogStage.setScene(new Scene(page));

            ArchiveAuctionDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAuction(selectedAuction);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                auctionService.saveAuction(selectedAuction);
                loadAuctions(); // Refresh the list
            }
        } catch (IOException e) {
            logger.error("Failed to load Archive Auction dialog.", e);
        }
    }
}