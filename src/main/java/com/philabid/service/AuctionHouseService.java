package com.philabid.service;

import com.philabid.database.AuctionHouseRepository;
import com.philabid.model.AuctionHouse;
import com.philabid.ui.control.FilterCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing auction houses.
 * Encapsulates business logic and separates it from the UI and database layers.
 */
public class AuctionHouseService implements CrudService<AuctionHouse> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionHouseService.class);
    private final AuctionHouseRepository auctionHouseRepository;

    public AuctionHouseService(AuctionHouseRepository auctionHouseRepository) {
        this.auctionHouseRepository = auctionHouseRepository;
    }

    /**
     * Retrieves all auction houses.
     *
     * @return A list of all auction houses, or an empty list in case of an error.
     */
    @Override
    public List<AuctionHouse> getAll(Collection<FilterCondition> filterConditions) {
        try {
            return auctionHouseRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all auction houses", e);
            // In a real application, you might show an error to the user.
            return Collections.emptyList();
        }
    }

    /**
     * Finds an auction house by its ID.
     *
     * @param id The ID of the auction house.
     * @return An Optional containing the auction house, or empty if not found or on error.
     */
    public Optional<AuctionHouse> getAuctionHouseById(long id) {
        try {
            return auctionHouseRepository.findById(id);
        } catch (SQLException e) {
            logger.error("Failed to find auction house with ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public AuctionHouse create() {
        return new AuctionHouse();
    }

    /**
     * Saves an auction house (creates a new one or updates an existing one).
     * You could add validation logic here.
     *
     * @param auctionHouse The auction house to save.
     * @return An Optional containing the saved auction house, or empty on failure.
     */
    @Override
    public Optional<AuctionHouse> save(AuctionHouse auctionHouse) {
        // Example of business logic: ensure name is not null or empty.
        if (auctionHouse.getName() == null || auctionHouse.getName().trim().isEmpty()) {
            logger.warn("Attempted to save an auction house with an empty name.");
            return Optional.empty();
        }

        try {
            return Optional.of(auctionHouseRepository.save(auctionHouse));
        } catch (SQLException e) {
            logger.error("Failed to save auction house: {}", auctionHouse.getName(), e);
            return Optional.empty();
        }
    }

    /**
     * Deletes an auction house by its ID.
     *
     * @param id The ID of the auction house to delete.
     * @return true if deletion was successful, false otherwise.
     */
    @Override
    public boolean delete(Long id) {
        try {
            return auctionHouseRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete auction house with ID: {}", id, e);
            return false;
        }
    }

    /**
     * Finds an auction house by its name (case-insensitive).
     *
     * @param name The name of the auction house.
     * @return An Optional containing the auction house, or empty if not found or on error.
     */
    public Optional<AuctionHouse> findByName(String name) {
        try {
            return auctionHouseRepository.findByName(name);
        } catch (SQLException e) {
            logger.error("Failed to find auction house with name: {}", name, e);
            return Optional.empty();
        }
    }
}
