package com.philabid.service;

import com.philabid.database.CatalogRepository;
import com.philabid.model.Catalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing catalogs.
 */
public class CatalogService implements CrudService<Catalog> {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);
    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public List<Catalog> getAll() {
        try {
            return catalogRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all catalogs", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Catalog create() {
        return new Catalog();
    }

    @Override
    public Optional<Catalog> save(Catalog catalog) {
        if (catalog.getName() == null || catalog.getName().trim().isEmpty()) {
            logger.warn("Attempted to save a catalog with an empty name.");
            return Optional.empty();
        }

        try {
            return Optional.of(catalogRepository.save(catalog));
        } catch (SQLException e) {
            logger.error("Failed to save catalog: {}", catalog.getName(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(Long id) {
        try {
            return catalogRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete catalog with ID: {}", id, e);
            return false;
        }
    }

    public Optional<Catalog> getCatalogById(long id) {
        try {
            return catalogRepository.findById(id);
        } catch (SQLException e) {
            logger.error("Failed to find catalog with ID: {}", id, e);
            return Optional.empty();
        }
    }
}
