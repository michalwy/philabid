package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.AuctionItem;
import com.philabid.service.AuctionItemService;
import com.philabid.service.CatalogService;
import com.philabid.service.CategoryService;
import com.philabid.ui.util.CatalogNumberColumnValue;
import com.philabid.ui.util.CellValueFactoryProvider;
import javafx.application.Platform;
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

import java.io.IOException;
import java.util.Optional;

/**
 * Controller for the Auction Item management view (AuctionItemView.fxml).
 */
public class AuctionItemController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemController.class);
    private final ObservableList<AuctionItem> auctionItemList = FXCollections.observableArrayList();
    @FXML
    private TableView<AuctionItem> auctionItemTable;
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
    private AuctionItemService auctionItemService;
    private CategoryService categoryService;
    private CatalogService catalogService;
    private I18nManager i18nManager;

    @FXML
    private void initialize() {
        categoryColumn.setCellValueFactory(CellValueFactoryProvider.forCategoryInfo(
                AuctionItem::getCategoryCode, AuctionItem::getCategoryName));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));

        // Use the provider for the complex catalog number column
        catalogNumberColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogNumber(
                AuctionItem::getCatalogNumber, AuctionItem::getOrderNumber));
        catalogNumberColumn.setComparator(CatalogNumberColumnValue.SORT_COMPARATOR);

        catalogInfoColumn.setCellValueFactory(CellValueFactoryProvider.forCatalogInfo(
                AuctionItem::getCatalogName, AuctionItem::getCatalogIssueYear));

        auctionItemTable.setItems(auctionItemList);

        editButton.disableProperty().bind(auctionItemTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(auctionItemTable.getSelectionModel().selectedItemProperty().isNull());

        logger.debug("AuctionItemController initialized.");
    }

    public void setServices(AuctionItemService auctionItemService, CategoryService categoryService,
                            CatalogService catalogService, I18nManager i18nManager) {
        this.auctionItemService = auctionItemService;
        this.categoryService = categoryService;
        this.catalogService = catalogService;
        this.i18nManager = i18nManager;
    }

    public void loadAuctionItems() {
        if (auctionItemService != null) {
            auctionItemList.setAll(auctionItemService.getAllAuctionItems());
            auctionItemTable.sort();
            logger.info("Loaded {} auction items into the table.", auctionItemList.size());
        } else {
            logger.warn("AuctionItemService is not available. Cannot load data.");
        }
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
        if (result.saveClicked()) {
            auctionItemService.saveAuctionItem(newAuctionItem);
            loadAuctionItems();
        }
        if (result.addAnother()) {
            final Long lastCategoryId = newAuctionItem.getCategoryId();
            Platform.runLater(() -> {
                doHandleAddAuctionItem(lastCategoryId);
            });
        }
    }

    @FXML
    private void handleEditAuctionItem() {
        AuctionItem selected = auctionItemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit auction item button clicked for: {}", selected.getCatalogNumber());
            EditDialogResult result = showAuctionItemEditDialog(selected);
            if (result.saveClicked()) {
                auctionItemService.saveAuctionItem(selected);
                loadAuctionItems();
            }
        }
    }

    @FXML
    private void handleDeleteAuctionItem() {
        AuctionItem selected = auctionItemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete auction item button clicked for: {}", selected.getCatalogNumber());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Auction Item");
            alert.setContentText(
                    "Are you sure you want to delete the selected auction item: " + selected.getCatalogNumber() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = auctionItemService.deleteAuctionItem(selected.getId());
                if (deleted) {
                    loadAuctionItems();
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
            loader.setResources(i18nManager.getResourceBundle());

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(auctionItem.getId() == null ? "Add Auction Item" : "Edit Auction Item");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(auctionItemTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AuctionItemEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setServices(categoryService, catalogService, i18nManager);
            controller.setAuctionItem(auctionItem);

            dialogStage.showAndWait();

            return new EditDialogResult(controller.isSaveClicked(), controller.shouldAddAnother());
        } catch (IOException e) {
            logger.error("Failed to load the auction item edit dialog.", e);
            return new EditDialogResult(false, false);
        }
    }
}
