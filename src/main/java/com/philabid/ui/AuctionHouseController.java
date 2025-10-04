package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.AuctionHouse;
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
 * Controller for the Auction House management view (AuctionHouseView.fxml).
 * Handles user interactions for creating, editing, and deleting auction houses.
 */
public class AuctionHouseController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionHouseController.class);
    private final ObservableList<AuctionHouse> auctionHouseList = FXCollections.observableArrayList();
    @FXML
    private TableView<AuctionHouse> auctionHouseTable;
    @FXML
    private TableColumn<AuctionHouse, String> nameColumn;
    @FXML
    private TableColumn<AuctionHouse, String> websiteColumn;
    @FXML
    private TableColumn<AuctionHouse, String> countryColumn;
    @FXML
    private TableColumn<AuctionHouse, String> currencyColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));

        auctionHouseTable.setItems(auctionHouseList);

        editButton.disableProperty().bind(auctionHouseTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(auctionHouseTable.getSelectionModel().selectedItemProperty().isNull());

        loadAuctionHouses();

        logger.debug("AuctionHouseController initialized.");
    }

    private void loadAuctionHouses() {
        auctionHouseList.setAll(AppContext.getAuctionHouseService().getAllAuctionHouses());
        logger.info("Loaded {} auction houses into the table.", auctionHouseList.size());
    }

    @FXML
    private void handleAddAuctionHouse() {
        logger.info("Add button clicked. Opening edit dialog for a new auction house.");
        AuctionHouse newAuctionHouse = new AuctionHouse();
        boolean saveClicked = showAuctionHouseEditDialog(newAuctionHouse);
        if (saveClicked) {
            AppContext.getAuctionHouseService().saveAuctionHouse(newAuctionHouse);
            loadAuctionHouses(); // Refresh the table
        }
    }

    @FXML
    private void handleEditAuctionHouse() {
        AuctionHouse selectedAuctionHouse = auctionHouseTable.getSelectionModel().getSelectedItem();
        if (selectedAuctionHouse != null) {
            logger.info("Edit button clicked for: {}. Opening edit dialog.", selectedAuctionHouse.getName());
            boolean saveClicked = showAuctionHouseEditDialog(selectedAuctionHouse);
            if (saveClicked) {
                AppContext.getAuctionHouseService().saveAuctionHouse(selectedAuctionHouse);
                loadAuctionHouses(); // Refresh the table
            }
        } else {
            logger.warn("Edit button clicked, but no auction house was selected.");
        }
    }

    @FXML
    private void handleDeleteAuctionHouse() {
        AuctionHouse selected = auctionHouseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete button clicked for: {}", selected.getName());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Auction House");
            alert.setContentText(
                    "Are you sure you want to delete the selected auction house: " + selected.getName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = AppContext.getAuctionHouseService().deleteAuctionHouse(selected.getId());
                if (deleted) {
                    logger.info("Successfully deleted auction house with ID: {}", selected.getId());
                    loadAuctionHouses(); // Refresh the table
                } else {
                    logger.error("Failed to delete auction house with ID: {}", selected.getId());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.setHeaderText("Could not delete the auction house.");
                    errorAlert.setContentText("An error occurred while trying to delete the auction house. Please " +
                            "check the logs for more details.");
                    errorAlert.showAndWait();
                }
            }
        } else {
            logger.warn("Delete button clicked, but no auction house was selected.");
        }
    }

    private boolean showAuctionHouseEditDialog(AuctionHouse auctionHouse) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AuctionHouseEditDialog.fxml"));
            loader.setResources(AppContext.getI18nManager().getResourceBundle());
            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(auctionHouse.getId() == null ? "Add Auction House" : "Edit Auction House");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(auctionHouseTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            AuctionHouseEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAuctionHouse(auctionHouse);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            logger.error("Failed to load the auction house edit dialog.", e);
            return false;
        }
    }
}
