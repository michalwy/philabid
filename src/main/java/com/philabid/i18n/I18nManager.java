package com.philabid.i18n;

import com.ibm.icu.util.ULocale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Manages internationalization (i18n) for the Philabid application.
 * Uses ICU4J for advanced internationalization features and standard Java ResourceBundles
 * for localized strings.
 */
public class I18nManager {
    
    private static final Logger logger = LoggerFactory.getLogger(I18nManager.class);
    private static final String BUNDLE_BASE_NAME = "messages";
    
    private ULocale currentLocale;
    private ResourceBundle resourceBundle;
    private final List<ULocale> supportedLocales;
    
    public I18nManager() {
        // Initialize supported locales
        supportedLocales = new ArrayList<>();
        supportedLocales.add(ULocale.ENGLISH);
        supportedLocales.add(ULocale.GERMAN);
        supportedLocales.add(ULocale.FRENCH);
        supportedLocales.add(ULocale.forLanguageTag("pl")); // Polish
        
        // Set default locale
        setLocale(ULocale.getDefault());
    }
    
    /**
     * Sets the current locale and loads the corresponding resource bundle.
     * 
     * @param locale the locale to set
     */
    public void setLocale(ULocale locale) {
        this.currentLocale = locale;
        
        try {
            // Convert ULocale to Java Locale for ResourceBundle
            Locale javaLocale = locale.toLocale();
            resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, javaLocale);
            
            logger.info("Locale set to: {} ({})", locale.getDisplayName(), locale.toLanguageTag());
            
        } catch (Exception e) {
            logger.warn("Failed to load resource bundle for locale {}, falling back to default", locale, e);
            
            // Fallback to English
            try {
                resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.ENGLISH);
                currentLocale = ULocale.ENGLISH;
            } catch (Exception fallbackError) {
                logger.error("Failed to load fallback resource bundle", fallbackError);
                throw new RuntimeException("Cannot initialize i18n system", fallbackError);
            }
        }
    }
    
    /**
     * Gets a localized string for the given key.
     * 
     * @param key the resource key
     * @return localized string or the key itself if not found
     */
    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            logger.warn("Missing translation for key: {}", key);
            return key; // Return key as fallback
        }
    }
    
    /**
     * Gets a localized string with parameter substitution.
     * 
     * @param key the resource key
     * @param params parameters to substitute in the string
     * @return formatted localized string
     */
    public String getString(String key, Object... params) {
        String pattern = getString(key);
        try {
            return String.format(pattern, params);
        } catch (Exception e) {
            logger.warn("Error formatting string for key: {} with params: {}", key, params, e);
            return pattern;
        }
    }
    
    /**
     * Gets the current locale.
     * 
     * @return current ULocale
     */
    public ULocale getCurrentLocale() {
        return currentLocale;
    }
    
    /**
     * Gets the list of supported locales.
     * 
     * @return list of supported ULocales
     */
    public List<ULocale> getSupportedLocales() {
        return new ArrayList<>(supportedLocales);
    }
    
    /**
     * Gets the current resource bundle.
     * 
     * @return current ResourceBundle
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }
    
    /**
     * Checks if a locale is supported.
     * 
     * @param locale the locale to check
     * @return true if supported, false otherwise
     */
    public boolean isLocaleSupported(ULocale locale) {
        return supportedLocales.contains(locale);
    }
}