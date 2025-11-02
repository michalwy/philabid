package com.philabid.service;

import com.philabid.database.CatalogValueRepository;
import com.philabid.model.CatalogValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service layer for managing Catalog Values.
 */
public class CatalogValueService extends AbstractCrudService<CatalogValue> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogValueService.class);
    private final CatalogValueRepository catalogValueRepository;

    public CatalogValueService(CatalogValueRepository catalogValueRepository) {
        super(catalogValueRepository);
        this.catalogValueRepository = catalogValueRepository;
    }

    protected boolean validate(CatalogValue catalogValue) {
        if (catalogValue.getTradingItemId() == null || catalogValue.getConditionId() == null ||
                catalogValue.getCatalogId() == null || catalogValue.getValue() == null) {
            logger.warn("Attempted to save a catalog value with missing required fields.");
            return false;
        }
        return true;
    }

    public Optional<CatalogValue> findByTradingItemAndCondition(long tradingItemId, long conditionId) {
        return catalogValueRepository.findByTradingItemAndCondition(tradingItemId, conditionId);
    }
}
