package com.philabid.i18n;

import com.ibm.icu.util.ULocale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for I18nManager functionality.
 */
class I18nManagerTest {
    
    private I18nManager i18nManager;
    
    @BeforeEach
    void setUp() {
        i18nManager = new I18nManager();
    }
    
    @Test
    void testDefaultLocaleInitialization() {
        assertNotNull(i18nManager.getCurrentLocale());
        assertNotNull(i18nManager.getResourceBundle());
    }
    
    @Test
    void testGetString() {
        // Test getting a string that should exist
        String appTitle = i18nManager.getString("app.title");
        assertNotNull(appTitle);
        assertFalse(appTitle.isEmpty());
    }
    
    @Test
    void testGetStringWithMissingKey() {
        // Test getting a string that doesn't exist - should return the key
        String result = i18nManager.getString("nonexistent.key");
        assertEquals("nonexistent.key", result);
    }
    
    @Test
    void testSupportedLocales() {
        var supportedLocales = i18nManager.getSupportedLocales();
        assertNotNull(supportedLocales);
        assertTrue(supportedLocales.size() > 0);
        assertTrue(supportedLocales.contains(ULocale.ENGLISH));
    }
    
    @Test
    void testSetLocale() {
        ULocale originalLocale = i18nManager.getCurrentLocale();
        
        // Set to German
        i18nManager.setLocale(ULocale.GERMAN);
        assertEquals(ULocale.GERMAN, i18nManager.getCurrentLocale());
        
        // Restore original
        i18nManager.setLocale(originalLocale);
        assertEquals(originalLocale, i18nManager.getCurrentLocale());
    }
    
    @Test
    void testIsLocaleSupported() {
        assertTrue(i18nManager.isLocaleSupported(ULocale.ENGLISH));
        assertTrue(i18nManager.isLocaleSupported(ULocale.GERMAN));
        assertFalse(i18nManager.isLocaleSupported(ULocale.forLanguageTag("xx")));
    }
}