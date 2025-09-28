package com.philabid;

import com.philabid.database.AuctionHouseRepository;
import com.philabid.database.CurrencyRepository;
import com.philabid.database.DatabaseManager;
import com.philabid.i18n.I18nManager;
import com.philabid.service.AuctionHouseService;
import com.philabid.service.ConfigurationService;
import com.philabid.service.CurrencyService;
import com.philabid.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * Main application class for Philabid - a stamp auction bidding assistance application.
 * This is an open-source, multilingual JavaFX desktop application that runs locally
 * without requiring external services.
 */
public class PhilabidApplication extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(PhilabidApplication.class);
    
    private DatabaseManager databaseManager;
    private I18nManager i18nManager;
    private ConfigurationService configurationService;
    private AuctionHouseService auctionHouseService;
    private CurrencyService currencyService;
    
    @Override
    public void init() throws Exception {
        super.init();
        logger.info("Initializing Philabid application...");
        
        // Initialize core managers and services
        i18nManager = new I18nManager();
        databaseManager = new DatabaseManager();
        
        // Initialize repositories
        AuctionHouseRepository auctionHouseRepository = new AuctionHouseRepository(databaseManager);
        CurrencyRepository currencyRepository = new CurrencyRepository(databaseManager);
        
        // Initialize services
        configurationService = new ConfigurationService();
        auctionHouseService = new AuctionHouseService(auctionHouseRepository);
        currencyService = new CurrencyService(currencyRepository);
        
        // Initialize database
        databaseManager.initialize();
        
        logger.info("Application initialization completed successfully");
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        logger.info("Starting Philabid application UI...");
        
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        fxmlLoader.setResources(i18nManager.getResourceBundle());
        
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(
            getClass().getResource("/css/application.css")).toExternalForm());
        
        MainController controller = fxmlLoader.getController();
        controller.setServices(databaseManager, i18nManager, configurationService, auctionHouseService, currencyService);
        
        primaryStage.setTitle(i18nManager.getString("app.title"));
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Set application icon
        try {
            Image icon = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/philabid-icon.png")));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            logger.warn("Could not load application icon: {}", e.getMessage());
        }
        
        primaryStage.show();
        logger.info("Application UI started successfully");
    }
    
    @Override
    public void stop() throws Exception {
        logger.info("Shutting down Philabid application...");
        
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
        
        super.stop();
        logger.info("Application shutdown completed");
    }
    
    public static void main(String[] args) {
        logger.info("Launching Philabid application with args: {}", java.util.Arrays.toString(args));
        launch(args);
    }
}