package com.philabid.service;

import com.philabid.database.CatalogValueRepository;
import com.philabid.model.CatalogValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Catalog Values.
 */
public class CatalogValueService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueService.class);
    private final CatalogValueRepository catalogValueRepository;

    public CatalogValueService(CatalogValueRepository catalogValueRepository) {
        this.catalogValueRepository = catalogValueRepository;
    }

    public List<CatalogValue> getAllCatalogValues() {
        try {
            return catalogValueRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all catalog values", e);
            return Collections.emptyList();
        }
    }

    public Optional<CatalogValue> saveCatalogValue(CatalogValue catalogValue) {
        if (catalogValue.getAuctionItemId() == null || catalogValue.getConditionId() == null || catalogValue.getCatalogId() == null || catalogValue.getValue() == null) {
            logger.warn("Attempted to save a catalog value with missing required fields.");
            return Optional.empty();
        }

        try {
            return Optional.of(catalogValueRepository.save(catalogValue));
        } catch (SQLException e) {
            logger.error("Failed to save catalog value for item ID: {}", catalogValue.getAuctionItemId(), e);
            return Optional.empty();
        }
    }

    public boolean deleteCatalogValue(long id) {
        try {
            return catalogValueRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete catalog value with ID: {}", id, e);
            return false;
        }
    }
}
