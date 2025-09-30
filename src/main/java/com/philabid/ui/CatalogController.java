package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.model.Catalog;
import com.philabid.service.CatalogService;
import com.philabid.service.CurrencyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
 * Controller for the Catalog management view (CatalogView.fxml).
 */
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
    private final ObservableList<Catalog> catalogList = FXCollections.observableArrayList();
    @FXML
    private TableView<Catalog> catalogTable;
    @FXML
    private TableColumn<Catalog, String> nameColumn;
    @FXML
    private TableColumn<Catalog, Integer> issueYearColumn;
    @FXML
    private TableColumn<Catalog, String> currencyColumn;
    @FXML
    private TableColumn<Catalog, Boolean> activeColumn;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    private CurrencyService currencyService;
    private CatalogService catalogService;
    private I18nManager i18nManager;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        issueYearColumn.setCellValueFactory(new PropertyValueFactory<>("issueYear"));
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Custom cell factory for the 'active' column to display a checkmark
        activeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item) {
                        setText("✓"); // Checkmark character
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        setAlignment(Pos.CENTER);
                    } else {
                        setText(null);
                        setStyle("");
                    }
                }
            }
        });

        catalogTable.setItems(catalogList);

        editButton.disableProperty().bind(catalogTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(catalogTable.getSelectionModel().selectedItemProperty().isNull());

        logger.debug("CatalogController initialized.");
    }

    public void setServices(CurrencyService currencyService, CatalogService catalogService, I18nManager i18nManager) {
        this.currencyService = currencyService;
        this.catalogService = catalogService;
        this.i18nManager = i18nManager;
        loadCatalogs();
    }

    private void loadCatalogs() {
        if (catalogService != null) {
            catalogList.setAll(catalogService.getAllCatalogs());
            logger.info("Loaded {} catalogs into the table.", catalogList.size());
        } else {
            logger.warn("CatalogService is not available. Cannot load data.");
        }
    }

    @FXML
    private void handleAddCatalog() {
        logger.info("Add catalog button clicked.");
        Catalog newCatalog = new Catalog();
        boolean saveClicked = showCatalogEditDialog(newCatalog);
        if (saveClicked) {
            catalogService.saveCatalog(newCatalog);
            loadCatalogs();
        }
    }

    @FXML
    private void handleEditCatalog() {
        Catalog selected = catalogTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Edit catalog button clicked for: {}", selected.getName());
            boolean saveClicked = showCatalogEditDialog(selected);
            if (saveClicked) {
                catalogService.saveCatalog(selected);
                loadCatalogs();
            }
        }
    }

    @FXML
    private void handleDeleteCatalog() {
        Catalog selected = catalogTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            logger.info("Delete catalog button clicked for: {}", selected.getName());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Catalog");
            alert.setContentText("Are you sure you want to delete the selected catalog: " + selected.getName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleted = catalogService.deleteCatalog(selected.getId());
                if (deleted) {
                    loadCatalogs();
                } else {
                    logger.error("Failed to delete catalog with ID: {}", selected.getId());
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Deletion Failed");
                    errorAlert.showAndWait();
                }
            }
        }
    }

    private boolean showCatalogEditDialog(Catalog catalog) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/CatalogEditDialog.fxml"));
            loader.setResources(i18nManager.getResourceBundle());

            GridPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(catalog.getId() == null ? "Add Catalog" : "Edit Catalog");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(catalogTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            CatalogEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setServices(currencyService);
            controller.setCatalog(catalog);

            dialogStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException e) {
            logger.error("Failed to load the catalog edit dialog.", e);
            return false;
        }
    }
}
