package com.philabid.service;

import com.philabid.database.CatalogValueRepository;
import com.philabid.model.CatalogValue;
import com.philabid.ui.control.FilterCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Catalog Values.
 */
public class CatalogValueService implements CrudService<CatalogValue> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueService.class);
    private final CatalogValueRepository catalogValueRepository;

    public CatalogValueService(CatalogValueRepository catalogValueRepository) {
        this.catalogValueRepository = catalogValueRepository;
    }

    public CatalogValue create() {
        return new CatalogValue();
    }

    @Override
    public List<CatalogValue> getAll(Collection<FilterCondition> filterConditions) {
        try {
            return catalogValueRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all catalog values", e);
            return Collections.emptyList();
        }
    }

    public Optional<CatalogValue> save(CatalogValue catalogValue) {
        if (catalogValue.getAuctionItemId() == null || catalogValue.getConditionId() == null ||
                catalogValue.getCatalogId() == null || catalogValue.getValue() == null) {
            logger.warn("Attempted to save a catalog value with missing required fields.");
            return Optional.empty();
        }

        try {
            return Optional.of(catalogValueRepository.save(catalogValue));
        } catch (SQLException e) {
            // Check if the error is due to a unique constraint violation
            if (e.getMessage() != null && e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                logger.warn(
                        "Attempted to save a duplicate catalog value for item ID: {}, condition ID: {}, catalog ID: {}",
                        catalogValue.getAuctionItemId(), catalogValue.getConditionId(), catalogValue.getCatalogId());
            } else {
                logger.error("Failed to save catalog value for item ID: {}", catalogValue.getAuctionItemId(), e);
            }
            return Optional.empty();
        }
    }

    public boolean delete(Long id) {
        try {
            return catalogValueRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete catalog value with ID: {}", id, e);
            return false;
        }
    }

    public Optional<CatalogValue> findByAuctionItemAndCondition(long auctionItemId, long conditionId) {
        try {
            return catalogValueRepository.findByAuctionItemAndCondition(auctionItemId, conditionId);
        } catch (SQLException e) {
            logger.error("Failed to find catalog value for item ID {} and condition ID {}", auctionItemId, conditionId,
                    e);
            return Optional.empty();
        }
    }
}
