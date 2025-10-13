package com.philabid.service;

import com.philabid.database.CatalogRepository;
import com.philabid.model.Catalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer for managing catalogs.
 */
public class CatalogService extends CrudService<Catalog> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    public CatalogService(CatalogRepository catalogRepository) {
        super(catalogRepository);
    }

    @Override
    protected boolean validate(Catalog catalog) {
        if (catalog.getName() == null || catalog.getName().trim().isEmpty()) {
            logger.warn("Attempted to save a catalog with an empty name.");
            return false;
        }
        return true;
    }
}
