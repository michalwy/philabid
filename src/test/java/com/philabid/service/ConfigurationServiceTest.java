package com.philabid.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ConfigurationService functionality.
 */
class ConfigurationServiceTest {
    
    @TempDir
    Path tempDir;
    
    private ConfigurationService configurationService;
    
    @BeforeEach
    void setUp() throws IOException {
        // Change to temp directory for testing
        System.setProperty("user.dir", tempDir.toString());
        configurationService = new ConfigurationService();
    }
    
    @Test
    void testDefaultConfiguration() {
        // Test that default values are created
        assertEquals("en", configurationService.getString("application.locale", "default"));
        assertEquals("default", configurationService.getString("application.theme", "fallback"));
        assertEquals(1200, configurationService.getInt("application.windowWidth", 0));
        assertEquals(800, configurationService.getInt("application.windowHeight", 0));
        assertFalse(configurationService.getBoolean("application.windowMaximized", true));
    }
    
    @Test
    void testSetAndGetValues() {
        // Test setting and getting string values
        configurationService.setValue("test.stringValue", "testString");
        assertEquals("testString", configurationService.getString("test.stringValue", "default"));
        
        // Test setting and getting integer values
        configurationService.setValue("test.intValue", 42);
        assertEquals(42, configurationService.getInt("test.intValue", 0));
        
        // Test setting and getting boolean values
        configurationService.setValue("test.boolValue", true);
        assertTrue(configurationService.getBoolean("test.boolValue", false));
        
        // Test setting and getting double values
        configurationService.setValue("test.doubleValue", 3.14);
        // Note: We don't have a getDouble method, but the framework should handle it
    }
    
    @Test
    void testNestedPaths() {
        // Test nested configuration paths
        configurationService.setValue("level1.level2.level3", "deepValue");
        assertEquals("deepValue", configurationService.getString("level1.level2.level3", "default"));
    }
    
    @Test
    void testMissingKeys() {
        // Test that missing keys return default values
        assertEquals("defaultValue", configurationService.getString("missing.key", "defaultValue"));
        assertEquals(999, configurationService.getInt("missing.intKey", 999));
        assertTrue(configurationService.getBoolean("missing.boolKey", true));
    }
    
    @Test
    void testDatabaseSettings() {
        // Test database-specific settings
        assertTrue(configurationService.getBoolean("database.backupEnabled", false));
        assertEquals(24, configurationService.getInt("database.backupInterval", 0));
    }
    
    @Test
    void testAuctionSettings() {
        // Test auction-specific settings
        assertEquals("USD", configurationService.getString("auction.defaultCurrency", ""));
        assertEquals("1.0", configurationService.getString("auction.bidIncrement", "0"));
        assertEquals(30, configurationService.getInt("auction.autoRefreshInterval", 0));
    }
    
    @Test
    void testNotificationSettings() {
        // Test notification settings
        assertTrue(configurationService.getBoolean("notifications.soundEnabled", false));
        assertTrue(configurationService.getBoolean("notifications.desktopNotifications", false));
        assertFalse(configurationService.getBoolean("notifications.emailNotifications", true));
    }
    
    @Test
    void testConfigurationPersistence() {
        // Modify a value
        configurationService.setValue("test.persistence", "persistedValue");
        
        // Save configuration
        configurationService.saveConfiguration();
        
        // Create a new service instance (simulating restart)
        ConfigurationService newService = new ConfigurationService();
        
        // Verify the value persisted
        assertEquals("persistedValue", newService.getString("test.persistence", "default"));
    }
}