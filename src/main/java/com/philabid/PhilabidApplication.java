package com.philabid;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Main application class for Philabid - a stamp auction bidding assistance application.
 * This is an open-source, multilingual JavaFX desktop application that runs locally
 * without requiring external services.
 */
public class PhilabidApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PhilabidApplication.class);

    public static void main(String[] args) {
        logger.info("Launching Philabid application with args: {}", java.util.Arrays.toString(args));
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        logger.info("Starting Philabid application UI...");

        AppContext.get().init(getHostServices());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/main.fxml"));
        fxmlLoader.setResources(AppContext.getI18nManager().getResourceBundle());

        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/css/application.css")).toExternalForm());

        primaryStage.setTitle(AppContext.getI18nManager().getString("app.title"));
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

        super.stop();
        logger.info("Application shutdown completed");
    }
}