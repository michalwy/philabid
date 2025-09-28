package com.philabid.service;

import com.philabid.database.CurrencyRepository;
import com.philabid.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Service layer for managing currencies.
 */
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    /**
     * Retrieves all available currencies.
     *
     * @return A list of all currencies, or an empty list in case of an error.
     */
    public List<Currency> getAllCurrencies() {
        try {
            return currencyRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all currencies", e);
            return Collections.emptyList();
        }
    }
}
