package com.philabid.service;

import com.philabid.database.AuctionHouseRepository;
import com.philabid.model.AuctionHouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service layer for managing auction houses.
 * Encapsulates business logic and separates it from the UI and database layers.
 */
public class AuctionHouseService extends AbstractCrudService<AuctionHouse> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionHouseService.class);
    private final AuctionHouseRepository auctionHouseRepository;

    public AuctionHouseService(AuctionHouseRepository auctionHouseRepository) {
        super(auctionHouseRepository);
        this.auctionHouseRepository = auctionHouseRepository;
    }

    /**
     * Saves an auction house (creates a new one or updates an existing one).
     * You could add validation logic here.
     *
     * @param auctionHouse The auction house to save.
     * @return An Optional containing the saved auction house, or empty on failure.
     */
    @Override
    protected boolean validate(AuctionHouse auctionHouse) {
        if (auctionHouse.getName() == null || auctionHouse.getName().trim().isEmpty()) {
            logger.warn("Attempted to save an auction house with an empty name.");
            return false;
        }
        return true;
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
