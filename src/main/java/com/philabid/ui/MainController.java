package com.philabid.ui;

import com.philabid.database.DatabaseManager;
import com.philabid.i18n.I18nManager;
import com.philabid.service.AuctionHouseService;
import com.philabid.service.ConfigurationService;
import com.philabid.service.CurrencyService;
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
import java.util.ResourceBundle;

/**
 * Main controller for the Philabid application window.
 * Handles the primary UI interactions and coordinates between different services.
 */
public class MainController implements Initializable {
    
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    // FXML Injected Fields from main.fxml
    @FXML private BorderPane rootPane;
    @FXML private MenuBar menuBar;
    @FXML private Menu fileMenu;
    @FXML private Menu editMenu;
    @FXML private Menu viewMenu;
    @FXML private Menu toolsMenu;
    @FXML private Menu helpMenu;
    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem aboutMenuItem;
    @FXML private MenuItem auctionHousesMenuItem;
    @FXML private TabPane mainTabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab auctionsTab;
    @FXML private Tab catalogTab;
    @FXML private Tab bidsTab;
    @FXML private StatusBar statusBar;
    @FXML private Label welcomeLabel;
    @FXML private TextArea logTextArea;

    // Services
    private DatabaseManager databaseManager;
    private I18nManager i18nManager;
    private ConfigurationService configurationService;
    private AuctionHouseService auctionHouseService;
    private CurrencyService currencyService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing MainController");
        
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
    public void setServices(DatabaseManager databaseManager, I18nManager i18nManager, 
                           ConfigurationService configurationService, AuctionHouseService auctionHouseService,
                           CurrencyService currencyService) {
        this.databaseManager = databaseManager;
        this.i18nManager = i18nManager;
        this.configurationService = configurationService;
        this.auctionHouseService = auctionHouseService;
        this.currencyService = currencyService;
        
        // Update UI with localized strings
        updateLocalizedStrings();
        
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
    }

    /**
     * Handles showing the Auction Houses view in a modal dialog.
     */
    private void handleShowAuctionHouses() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AuctionHouseView.fxml"));
            loader.setResources(i18nManager.getResourceBundle());

            Parent auctionHouseView = loader.load();

            AuctionHouseController controller = loader.getController();
            controller.setServices(auctionHouseService, currencyService, i18nManager);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(i18nManager.getString("auctionHouses.title"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(rootPane.getScene().getWindow());
            
            Scene scene = new Scene(auctionHouseView);
            dialogStage.setScene(scene);
            
            dialogStage.showAndWait();
            logger.info("Closed Auction Houses dialog.");

        } catch (IOException e) {
            logger.error("Failed to load AuctionHouseView.fxml", e);
            // Show an error alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open Auction Houses view");
            alert.setContentText("An error occurred while trying to load the view. Please check the logs.");
            alert.showAndWait();
        }
    }
    
    /**
     * Updates UI strings with localized versions.
     */
    private void updateLocalizedStrings() {
        if (i18nManager == null) return;
        
        try {
            // Update menu labels
            if (fileMenu != null) fileMenu.setText(i18nManager.getString("menu.file"));
            if (editMenu != null) editMenu.setText(i18nManager.getString("menu.edit"));
            if (viewMenu != null) viewMenu.setText(i18nManager.getString("menu.view"));
            if (toolsMenu != null) toolsMenu.setText(i18nManager.getString("menu.tools"));
            if (helpMenu != null) helpMenu.setText(i18nManager.getString("menu.help"));
            
            // Update menu items
            if (exitMenuItem != null) exitMenuItem.setText(i18nManager.getString("menu.file.exit"));
            if (aboutMenuItem != null) aboutMenuItem.setText(i18nManager.getString("menu.help.about"));
            if (auctionHousesMenuItem != null) auctionHousesMenuItem.setText(i18nManager.getString("menu.tools.auctionHouses"));
            
            // Update tab labels
            if (dashboardTab != null) dashboardTab.setText(i18nManager.getString("tab.dashboard"));
            if (auctionsTab != null) auctionsTab.setText(i18nManager.getString("tab.auctions"));
            if (catalogTab != null) catalogTab.setText(i18nManager.getString("tab.catalog"));
            if (bidsTab != null) bidsTab.setText(i18nManager.getString("tab.bids"));
            
            // Update welcome message
            if (welcomeLabel != null) {
                welcomeLabel.setText(i18nManager.getString("welcome.message"));
            }
            
        } catch (Exception e) {
            logger.warn("Error updating localized strings", e);
        }
    }
    
    /**
     * Handles application exit.
     */
    @FXML
    private void handleExit() {
        logger.info("Exit requested by user");
        Platform.exit();
    }
    
    /**
     * Handles about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(i18nManager != null ? i18nManager.getString("about.title") : "About");
        alert.setHeaderText("Philabid");
        alert.setContentText(
            "Philabid - Stamp Auction Bidding Assistant\n" +
            "Version: 1.0-SNAPSHOT\n" +
            "License: Apache 2.0\n\n" +
            "An open-source, multilingual JavaFX desktop application\n" +
            "for stamp auction bidding assistance."
        );
        alert.showAndWait();
    }
    
    /**
     * Adds a message to the log area.
     */
    private void addLogMessage(String message) {
        if (logTextArea != null) {
            Platform.runLater(() -> {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                logTextArea.appendText(String.format("[%s] %s%n", timestamp, message));
            });
        }
    }
}