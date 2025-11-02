package com.philabid.ui;

import com.philabid.AppContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Main controller for the Philabid application window.
 * Handles the primary UI interactions and coordinates between different services.
 */
public class MainController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private final Map<Tab, RefreshableViewController> tabControllerMap = new HashMap<>();
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
    private MenuItem preferencesMenuItem;
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
    private Tab valuationTab;
    @FXML
    private Tab tradingItemsTab;
    @FXML
    private Tab catalogValuesTab;
    @FXML
    private StatusBar statusBar;
    // Injected controllers from included FXML files
    @FXML
    private TradingItemController tradingItemViewController;
    @FXML
    private CatalogValueController catalogValueViewController;
    @FXML
    private ActiveAuctionController activeAuctionViewController;
    @FXML
    private ArchivedAuctionController archivedAuctionViewController;
    @FXML
    private ValuationViewController valuationViewController;
    @FXML
    private DashboardController dashboardViewController;

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

        initializeTabControllerMap();
        updateLocalizedStrings();
        setupTabListeners();
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
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                RefreshableViewController newController = tabControllerMap.get(newTab);
                if (newController != null) {
                    logger.info("'{}' tab selected, refreshing data.", newTab.getText());
                    newController.refresh();
                }

                RefreshableViewController oldController = tabControllerMap.get(oldTab);
                if (oldController != null) {
                    oldController.unload();
                }
            }
        });
    }

    private void initializeTabControllerMap() {
        tabControllerMap.put(activeAuctionsTab, activeAuctionViewController);
        tabControllerMap.put(archivedAuctionsTab, archivedAuctionViewController);
        tabControllerMap.put(valuationTab, valuationViewController);
        tabControllerMap.put(tradingItemsTab, tradingItemViewController);
        tabControllerMap.put(catalogValuesTab, catalogValueViewController);
        tabControllerMap.put(dashboardTab, dashboardViewController);
    }

    /**
     * Sets up menu item actions.
     */
    private void setupMenuActions() {
        if (exitMenuItem != null) {
            exitMenuItem.setOnAction(e -> handleExit());
        }

        if (preferencesMenuItem != null) {
            preferencesMenuItem.setOnAction(e -> handleShowPreferences());
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

    private void handleShowPreferences() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PreferencesDialog.fxml"),
                    AppContext.getI18nManager().getResourceBundle());
            Parent view = loader.load();
            PreferencesDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            showInModalDialog(view, AppContext.getI18nManager().getString("preferences.title"), dialogStage);
        } catch (IOException e) {
            showErrorDialog("Could not open Preferences view", e);
        }
    }

    private void handleShowAuctionHouses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuctionHouseView.fxml"),
                    AppContext.getI18nManager().getResourceBundle());
            Parent view = loader.load();
            showInModalDialog(view, AppContext.getI18nManager().getString("auctionHouses.title"), new Stage());
        } catch (IOException e) {
            showErrorDialog("Could not open Auction Houses view", e);
        }
    }

    private void handleShowCatalogs() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/CatalogView.fxml"),
                            AppContext.getI18nManager().getResourceBundle());
            Parent view = loader.load();
            showInModalDialog(view, AppContext.getI18nManager().getString("catalogs.title"), new Stage());
        } catch (IOException e) {
            showErrorDialog("Could not open Catalogs view", e);
        }
    }

    private void handleShowCategories() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/CategoryView.fxml"),
                            AppContext.getI18nManager().getResourceBundle());
            Parent view = loader.load();
            showInModalDialog(view, AppContext.getI18nManager().getString("categories.title"), new Stage());
        } catch (IOException e) {
            showErrorDialog("Could not open Categories view", e);
        }
    }

    private void handleShowConditions() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/fxml/ConditionView.fxml"),
                            AppContext.getI18nManager().getResourceBundle());
            Parent view = loader.load();
            showInModalDialog(view, AppContext.getI18nManager().getString("conditions.title"), new Stage());
        } catch (IOException e) {
            showErrorDialog("Could not open Conditions view", e);
        }
    }

    private void showInModalDialog(Parent view, String title, Stage dialogStage) {
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
        try {
            fileMenu.setText(AppContext.getI18nManager().getString("menu.file"));
            editMenu.setText(AppContext.getI18nManager().getString("menu.edit"));
            viewMenu.setText(AppContext.getI18nManager().getString("menu.view"));
            toolsMenu.setText(AppContext.getI18nManager().getString("menu.tools"));
            helpMenu.setText(AppContext.getI18nManager().getString("menu.help"));

            exitMenuItem.setText(AppContext.getI18nManager().getString("menu.file.exit"));
            aboutMenuItem.setText(AppContext.getI18nManager().getString("menu.help.about"));
            auctionHousesMenuItem.setText(AppContext.getI18nManager().getString("menu.tools.auctionHouses"));
            catalogsMenuItem.setText(AppContext.getI18nManager().getString("menu.tools.catalogs"));
            categoriesMenuItem.setText(AppContext.getI18nManager().getString("menu.tools.categories"));
            conditionsMenuItem.setText(AppContext.getI18nManager().getString("menu.tools.conditions"));

            dashboardTab.setText(AppContext.getI18nManager().getString("tab.dashboard"));
            activeAuctionsTab.setText(AppContext.getI18nManager().getString("tab.auctions.active"));
            archivedAuctionsTab.setText(AppContext.getI18nManager().getString("tab.auctions.archived"));
            valuationTab.setText(AppContext.getI18nManager().getString("tab.valuation"));
            tradingItemsTab.setText(AppContext.getI18nManager().getString("tab.tradingItems"));
            catalogValuesTab.setText(AppContext.getI18nManager().getString("tab.catalogValues"));
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
        alert.setTitle(AppContext.getI18nManager().getString("about.title"));
        alert.setHeaderText("Philabid");
        alert.setContentText("Philabid - Stamp Auction Bidding Assistant\n" +
                "Version: 1.0-SNAPSHOT\n" +
                "License: Apache 2.0\n\n" +
                "An open-source, multilingual JavaFX desktop application\n" +
                "for stamp auction bidding assistance.");
        alert.showAndWait();
    }
}