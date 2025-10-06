package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionItem;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Auction Item management view (AuctionItemView.fxml).
 */
public class AuctionItemController extends BaseTableViewController<AuctionItem> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemController.class);
    @FXML
    private TableColumn<AuctionItem, String> categoryColumn;
    @FXML
    private TableColumn<AuctionItem, CatalogNumberColumnValue> catalogNumberColumn;
    @FXML
    private TableColumn<AuctionItem, String> catalogInfoColumn;
    @FXML
    private TableColumn<AuctionItem, String> notesColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @Override
    protected void initializeView() {
        categoryColumn.setCellValueFactory(CellValueFactoryProvider.forCategoryInfo(
                AuctionItem::getCategoryCode, AuctionItem::getCategoryName));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Use the provider for the complex catalog number column
        catalogNumberColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogNumber(
                AuctionItem::getCatalogNumber, AuctionItem::getOrderNumber));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);

        catalogInfoColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogInfo(
                AuctionItem::getCatalogName, AuctionItem::getCatalogIssueYear));

        editButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        logger.debug("AuctionItemController initialized.");
    }

    public List<AuctionItem> loadTableItems() {
        return AppContext.getAuctionItemService().getAllAuctionItems();
    }

    @FXML
    private void handleAddAuctionItem() {
        logger.info("Add auction item button clicked.");
        doHandleAddAuctionItem(null);
    }

    private void doHandleAddAuctionItem(Long categoryId) {
        AuctionItem newAuctionItem = new AuctionItem();
        newAuctionItem.setCategoryId(categoryId);
        EditDialogResult result = showAuctionItemEditDialog(newAuctionItem);
        if (result.saved()) {
            AppContext.getAuctionItemService().saveAuctionItem(newAuctionItem);
            refreshTable();
        }
        if (result.editNext()) {
            final Long lastCategoryId = newAuctionItem.getCategoryId();
            Platform.runLater(() -> {
                doHandleAddAuctionItem(lastCategoryId);
            });
        }
    }

    @FXML
    private void handleEditAuctionItem() {
        AuctionItem selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit auction item button clicked for: {}", selected.getCatalogNumber());
            EditDialogResult result = showAuctionItemEditDialog(selected);
            if (result.saved()) {
                AppContext.getAuctionItemService().saveAuctionItem(selected);
                refreshTable();
            }
        }
    }

    @FXML
    private void handleDeleteAuctionItem() {
        AuctionItem selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete auction item button clicked for: {}", selected.getCatalogNumber());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Auction Item");
            alert.setContentText(
                    "Are you sure you want to delete the selected auction item: " + selected.getCatalogNumber() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = AppContext.getAuctionItemService().deleteAuctionItem(selected.getId());
                if (deleted) {
                    refreshTable();
                } else {
                    logger.error("Failed to delete auction item with ID: {}", selected.getId());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText("Could not delete the auction item.");
                    errorAlert.setContentText("An error occurred while trying to delete the auction item. Please " +
                            "check the logs.");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    private EditDialogResult showAuctionItemEditDialog(AuctionItem auctionItem) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AuctionItemEditDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(auctionItem.getId() == null ? "Add Auction Item" : "Edit Auction Item");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(table.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AuctionItemEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAuctionItem(auctionItem);

            dialogStage.showAndWait();

            return new EditDialogResult(controller.isSaveClicked(), controller.shouldAddAnother());
        } catch (IOException e) {
            logger.error("Failed to load the auction item edit dialog.", e);
            return new EditDialogResult(false, false);
        }
    }

    @Override
    protected void handleDoubleClick() {
        handleEditAuctionItem();
    }
}
