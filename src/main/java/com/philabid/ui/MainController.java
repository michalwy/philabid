package com.philabid.ui;

import com.philabid.i18n.I18nManager;
import com.philabid.parsing.UrlParsingService;
import com.philabid.service.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Main controller for the Philabid application window.
 * Handles the primary UI interactions and coordinates between different services.
 */
public class MainController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // FXML Injected Fields from main.fxml
    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu fileMenu;
    @FXML
    private Menu editMenu;
    @FXML
    private Menu viewMenu;
    @FXML
    private Menu toolsMenu;
    @FXML
    private Menu helpMenu;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private MenuItem auctionHousesMenuItem;
    @FXML
    private MenuItem catalogsMenuItem;
    @FXML
    private MenuItem categoriesMenuItem;
    @FXML
    private MenuItem conditionsMenuItem;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab dashboardTab;
    @FXML
    private Tab activeAuctionsTab;
    @FXML
    private Tab archivedAuctionsTab;
    @FXML
    private Tab catalogTab;
    @FXML
    private Tab bidsTab;
    @FXML
    private Tab auctionItemsTab;
    @FXML
    private Tab catalogValuesTab;
    @FXML
    private StatusBar statusBar;
    @FXML
    private Label welcomeLabel;
    @FXML
    private TextArea logTextArea;

    // Injected controllers from included FXML files
    @FXML
    private AuctionItemController auctionItemViewController;
    @FXML
    private CatalogValueController catalogValueViewController;
    @FXML
    private AuctionController activeAuctionViewController;
    @FXML
    private AuctionController archivedAuctionViewController;

    // Services
    private I18nManager i18nManager;
    private AuctionHouseService auctionHouseService;
    private CatalogService catalogService;
    private CategoryService categoryService;
    private ConditionService conditionService;
    private AuctionItemService auctionItemService;
    private CatalogValueService catalogValueService;
    private CurrencyService currencyService;
    private AuctionService auctionService;
    private UrlParsingService urlParsingService;
    private HostServices hostServices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing MainController");

        // Maximize window on startup
        Platform.runLater(() -> {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            if (stage != null) {
                stage.setMaximized(true);
            }
        });

        // Initialize UI components
        setupStatusBar();
        setupMenuActions();

        // Add welcome message
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome to Philabid - Stamp Auction Bidding Assistant");
        }

        // Setup log area
        if (logTextArea != null) {
            logTextArea.setEditable(false);
            addLogMessage("Application started successfully");
        }
    }

    /**
     * Sets the application services. Called after FXML loading.
     */
    public void setServices(I18nManager i18nManager, CurrencyService currencyService,
                            AuctionHouseService auctionHouseService, CatalogService catalogService,
                            CategoryService categoryService, ConditionService conditionService,
                            AuctionItemService auctionItemService, AuctionService auctionService,
                            CatalogValueService catalogValueService, UrlParsingService urlParsingService,
                            HostServices hostServices) {
        this.i18nManager = i18nManager;
        this.currencyService = currencyService;
        this.auctionHouseService = auctionHouseService;
        this.catalogService = catalogService;
        this.categoryService = categoryService;
        this.conditionService = conditionService;
        this.auctionItemService = auctionItemService;
        this.auctionService = auctionService;
        this.catalogValueService = catalogValueService;
        this.urlParsingService = urlParsingService;
        this.hostServices = hostServices;

        // Inject services into child controllers
        if (activeAuctionViewController != null) {
            activeAuctionViewController.configure(false); // Show active
            activeAuctionViewController.setServices(this.auctionService, this.auctionHouseService,
                    this.auctionItemService,
                    this.conditionService, this.currencyService, this.i18nManager, this.categoryService,
                    this.urlParsingService, this.hostServices);
            logger.info("Services injected into ActiveAuctionController.");
        }
        if (archivedAuctionViewController != null) {
            archivedAuctionViewController.configure(true); // Show archived
            archivedAuctionViewController.setServices(this.auctionService, this.auctionHouseService,
                    this.auctionItemService,
                    this.conditionService, this.currencyService, this.i18nManager, this.categoryService,
                    this.urlParsingService, this.hostServices);
            logger.info("Services injected into ArchivedAuctionController.");
        }
        if (auctionItemViewController != null) {
            auctionItemViewController.setServices(this.auctionItemService, this.categoryService, this.catalogService,
                    this.i18nManager);
            logger.info("Services injected into AuctionItemController.");
        }
        if (catalogValueViewController != null) {
            catalogValueViewController.setServices(this.currencyService, this.catalogValueService,
                    this.auctionItemService, this.conditionService, this.catalogService, this.categoryService,
                    this.i18nManager);
            logger.info("Services injected into CatalogValueController.");
        }

        // Update UI with localized strings
        updateLocalizedStrings();
        setupTabListeners();

        logger.info("Services set for MainController");
    }

    /**
     * Sets up the status bar with initial information.
     */
    private void setupStatusBar() {
        if (statusBar != null) {
            statusBar.setText("Ready");

            // Add current time to status bar
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                statusBar.setProgress(0); // Hide progress bar
                statusBar.setText("Ready - " + currentTime);
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    /**
     * Sets up listeners to refresh data when a tab is selected.
     */
    private void setupTabListeners() {
        if (activeAuctionsTab != null && activeAuctionViewController != null) {
            activeAuctionsTab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    logger.info("Active Auctions tab selected, refreshing data.");
                    activeAuctionViewController.loadAuctions();
                }
            });
        }
        if (archivedAuctionsTab != null && archivedAuctionViewController != null) {
            archivedAuctionsTab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    logger.info("Archived Auctions tab selected, refreshing data.");
                    archivedAuctionViewController.loadAuctions();
                }
            });
        }
        if (auctionItemsTab != null && auctionItemViewController != null) {
            auctionItemsTab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    logger.info("Auction Items tab selected, refreshing data.");
                    auctionItemViewController.loadAuctionItems();
                }
            });
        }
        if (catalogValuesTab != null && catalogValueViewController != null) {
            catalogValuesTab.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    logger.info("Catalog Values tab selected, refreshing data.");
                    catalogValueViewController.loadCatalogValues();
                }
            });
        }
    }

    /**
     * Sets up menu item actions.
     */
    private void setupMenuActions() {
        if (exitMenuItem != null) {
            exitMenuItem.setOnAction(e -> handleExit());
        }

        if (aboutMenuItem != null) {
            aboutMenuItem.setOnAction(e -> handleAbout());
        }

        if (auctionHousesMenuItem != null) {
            auctionHousesMenuItem.setOnAction(e -> handleShowAuctionHouses());
        }

        if (catalogsMenuItem != null) {
            catalogsMenuItem.setOnAction(e -> handleShowCatalogs());
        }

        if (categoriesMenuItem != null) {
            categoriesMenuItem.setOnAction(e -> handleShowCategories());
        }

        if (conditionsMenuItem != null) {
            conditionsMenuItem.setOnAction(e -> handleShowConditions());
        }
    }

    private void handleShowAuctionHouses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuctionHouseView.fxml"),
                    i18nManager.getResourceBundle());
            Parent view = loader.load();
            AuctionHouseController controller = loader.getController();
            controller.setServices(currencyService, auctionHouseService, i18nManager);
            showInModalDialog(view, i18nManager.getString("auctionHouses.title"));
        } catch (IOException e) {
            showErrorDialog("Could not open Auction Houses view", e);
        }
    }

    private void handleShowCatalogs() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/CatalogView.fxml"), i18nManager.getResourceBundle());
            Parent view = loader.load();
            CatalogController controller = loader.getController();
            controller.setServices(currencyService, catalogService, i18nManager);
            showInModalDialog(view, i18nManager.getString("catalogs.title"));
        } catch (IOException e) {
            showErrorDialog("Could not open Catalogs view", e);
        }
    }

    private void handleShowCategories() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/CategoryView.fxml"), i18nManager.getResourceBundle());
            Parent view = loader.load();
            CategoryController controller = loader.getController();
            controller.setServices(categoryService, catalogService, i18nManager);
            showInModalDialog(view, i18nManager.getString("categories.title"));
        } catch (IOException e) {
            showErrorDialog("Could not open Categories view", e);
        }
    }

    private void handleShowConditions() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/ConditionView.fxml"), i18nManager.getResourceBundle());
            Parent view = loader.load();
            ConditionController controller = loader.getController();
            controller.setServices(conditionService, i18nManager);
            showInModalDialog(view, i18nManager.getString("conditions.title"));
        } catch (IOException e) {
            showErrorDialog("Could not open Conditions view", e);
        }
    }

    private void showInModalDialog(Parent view, String title) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(rootPane.getScene().getWindow());
        Scene scene = new Scene(view);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    private void showErrorDialog(String header, Exception e) {
        logger.error(header, e);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText("An error occurred. Please check the logs.");
        alert.showAndWait();
    }

    private void updateLocalizedStrings() {
        if (i18nManager == null) return;

        try {
            if (fileMenu != null) fileMenu.setText(i18nManager.getString("menu.file"));
            if (editMenu != null) editMenu.setText(i18nManager.getString("menu.edit"));
            if (viewMenu != null) viewMenu.setText(i18nManager.getString("menu.view"));
            if (toolsMenu != null) toolsMenu.setText(i18nManager.getString("menu.tools"));
            if (helpMenu != null) helpMenu.setText(i18nManager.getString("menu.help"));

            if (exitMenuItem != null) exitMenuItem.setText(i18nManager.getString("menu.file.exit"));
            if (aboutMenuItem != null) aboutMenuItem.setText(i18nManager.getString("menu.help.about"));
            if (auctionHousesMenuItem != null)
                auctionHousesMenuItem.setText(i18nManager.getString("menu.tools.auctionHouses"));
            if (catalogsMenuItem != null) catalogsMenuItem.setText(i18nManager.getString("menu.tools.catalogs"));
            if (categoriesMenuItem != null) categoriesMenuItem.setText(i18nManager.getString("menu.tools.categories"));
            if (conditionsMenuItem != null) conditionsMenuItem.setText(i18nManager.getString("menu.tools.conditions"));

            if (dashboardTab != null) dashboardTab.setText(i18nManager.getString("tab.dashboard"));
            if (activeAuctionsTab != null) activeAuctionsTab.setText(i18nManager.getString("tab.auctions.active"));
            if (archivedAuctionsTab != null)
                archivedAuctionsTab.setText(i18nManager.getString("tab.auctions.archived"));
            if (catalogTab != null) catalogTab.setText(i18nManager.getString("tab.catalog"));
            if (bidsTab != null) bidsTab.setText(i18nManager.getString("tab.bids"));
            if (auctionItemsTab != null) auctionItemsTab.setText(i18nManager.getString("tab.auctionItems"));
            if (catalogValuesTab != null) catalogValuesTab.setText(i18nManager.getString("tab.catalogValues"));

            if (welcomeLabel != null) {
                welcomeLabel.setText(i18nManager.getString("welcome.message"));
            }
        } catch (Exception e) {
            logger.warn("Error updating localized strings", e);
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(i18nManager != null ? i18nManager.getString("about.title") : "About");
        alert.setHeaderText("Philabid");
        alert.setContentText("Philabid - Stamp Auction Bidding Assistant\n" +
                "Version: 1.0-SNAPSHOT\n" +
                "License: Apache 2.0\n\n" +
                "An open-source, multilingual JavaFX desktop application\n" +
                "for stamp auction bidding assistance.");
        alert.showAndWait();
    }

    private void addLogMessage(String message) {
        if (logTextArea != null) {
            Platform.runLater(() -> {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                logTextArea.appendText(String.format("[%s] %s%n", timestamp, message));
            });
        }
    }
}