package com.philabid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages application configuration for Philabid.
 * Handles loading and saving configuration from/to JSON files.
 */
public class ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);
    private static final String CONFIG_FILE = "philabid-config.json";

    private final ObjectMapper objectMapper;
    private ObjectNode configuration;

    public ConfigurationService() {
        this.objectMapper = new ObjectMapper();
        loadConfiguration();
    }

    /**
     * Loads configuration from file or creates default configuration.
     */
    private void loadConfiguration() {
        Path configPath = Paths.get(CONFIG_FILE);

        try {
            if (Files.exists(configPath)) {
                configuration = (ObjectNode) objectMapper.readTree(configPath.toFile());
                logger.info("Configuration loaded from: {}", CONFIG_FILE);
            } else {
                createDefaultConfiguration();
                saveConfiguration();
                logger.info("Default configuration created and saved to: {}", CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.warn("Failed to load configuration, using defaults", e);
            createDefaultConfiguration();
        }
    }

    /**
     * Creates default configuration.
     */
    private void createDefaultConfiguration() {
        configuration = objectMapper.createObjectNode();

        // Application settings
        ObjectNode appSettings = configuration.putObject("application");
        appSettings.put("locale", "en");
        appSettings.put("theme", "default");
        appSettings.put("windowWidth", 1200);
        appSettings.put("windowHeight", 800);
        appSettings.put("windowMaximized", false);

        // Database settings
        ObjectNode dbSettings = configuration.putObject("database");
        dbSettings.put("backupEnabled", true);
        dbSettings.put("backupInterval", 24); // hours

        // Auction settings
        ObjectNode auctionSettings = configuration.putObject("auction");
        auctionSettings.put("defaultCurrency", "USD");
        auctionSettings.put("recommendationAnalysisDays", 90);
        auctionSettings.put("bidIncrement", 1.0);
        auctionSettings.put("autoRefreshInterval", 30); // seconds

        // Notification settings
        ObjectNode notificationSettings = configuration.putObject("notifications");
        notificationSettings.put("soundEnabled", true);
        notificationSettings.put("desktopNotifications", true);
        notificationSettings.put("emailNotifications", false);
    }

    /**
     * Saves configuration to file.
     */
    public void saveConfiguration() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(Paths.get(CONFIG_FILE).toFile(), configuration);
            logger.info("Configuration saved to: {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Failed to save configuration", e);
        }
    }

    /**
     * Gets a string configuration value.
     *
     * @param path         dot-separated path to the configuration value
     * @param defaultValue default value if not found
     * @return configuration value or default
     */
    public String getString(String path, String defaultValue) {
        try {
            String[] parts = path.split("\\.");
            ObjectNode current = configuration;

            for (int i = 0; i < parts.length - 1; i++) {
                current = (ObjectNode) current.get(parts[i]);
                if (current == null) return defaultValue;
            }

            return current.path(parts[parts.length - 1]).asText(defaultValue);
        } catch (Exception e) {
            logger.warn("Error getting configuration value for path: {}", path, e);
            return defaultValue;
        }
    }

    /**
     * Gets an integer configuration value.
     *
     * @param path         dot-separated path to the configuration value
     * @param defaultValue default value if not found
     * @return configuration value or default
     */
    public int getInt(String path, int defaultValue) {
        try {
            String[] parts = path.split("\\.");
            ObjectNode current = configuration;

            for (int i = 0; i < parts.length - 1; i++) {
                current = (ObjectNode) current.get(parts[i]);
                if (current == null) return defaultValue;
            }

            return current.path(parts[parts.length - 1]).asInt(defaultValue);
        } catch (Exception e) {
            logger.warn("Error getting configuration value for path: {}", path, e);
            return defaultValue;
        }
    }

    /**
     * Gets a boolean configuration value.
     *
     * @param path         dot-separated path to the configuration value
     * @param defaultValue default value if not found
     * @return configuration value or default
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        try {
            String[] parts = path.split("\\.");
            ObjectNode current = configuration;

            for (int i = 0; i < parts.length - 1; i++) {
                current = (ObjectNode) current.get(parts[i]);
                if (current == null) return defaultValue;
            }

            return current.path(parts[parts.length - 1]).asBoolean(defaultValue);
        } catch (Exception e) {
            logger.warn("Error getting configuration value for path: {}", path, e);
            return defaultValue;
        }
    }

    /**
     * Sets a configuration value.
     *
     * @param path  dot-separated path to the configuration value
     * @param value value to set
     */
    public void setValue(String path, Object value) {
        try {
            String[] parts = path.split("\\.");
            ObjectNode current = configuration;

            // Navigate to parent node
            for (int i = 0; i < parts.length - 1; i++) {
                ObjectNode next = (ObjectNode) current.get(parts[i]);
                if (next == null) {
                    next = current.putObject(parts[i]);
                }
                current = next;
            }

            // Set the value
            String key = parts[parts.length - 1];
            if (value instanceof String) {
                current.put(key, (String) value);
            } else if (value instanceof Integer) {
                current.put(key, (Integer) value);
            } else if (value instanceof Boolean) {
                current.put(key, (Boolean) value);
            } else if (value instanceof Double) {
                current.put(key, (Double) value);
            }
        } catch (Exception e) {
            logger.error("Error setting configuration value for path: {}", path, e);
        }
    }

    public CurrencyUnit getDefaultCurrency() {
        String defaultCurrencyCode = getString("application.defaultCurrency", "PLN");
        return Monetary.getCurrency(defaultCurrencyCode);
    }

    public int getRecommendationAnalysisDays() {
        return getInt("auction.recommendationAnalysisDays", 90);
    }
}