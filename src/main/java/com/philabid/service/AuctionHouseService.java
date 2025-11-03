package com.philabid.service;

import com.philabid.database.AuctionHouseRepository;
import com.philabid.model.AuctionHouse;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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

    public MultiCurrencyMonetaryAmount getNextBid(Long auctionHouseId, MultiCurrencyMonetaryAmount currentPrice) {
        long step = 1;
        BigDecimal current = currentPrice.originalAmount().getNumber().numberValue(BigDecimal.class);
        if (current.compareTo(BigDecimal.valueOf(10000)) > 0) {
            step = 250;
        } else if (current.compareTo(BigDecimal.valueOf(5000)) > 0) {
            step = 100;
        } else if (current.compareTo(BigDecimal.valueOf(2500)) > 0) {
            step = 50;
        } else if (current.compareTo(BigDecimal.valueOf(1000)) > 0) {
            step = 25;
        } else if (current.compareTo(BigDecimal.valueOf(500)) > 0) {
            step = 15;
        } else if (current.compareTo(BigDecimal.valueOf(250)) > 0) {
            step = 10;
        } else if (current.compareTo(BigDecimal.valueOf(100)) > 0) {
            step = 5;
        } else if (current.compareTo(BigDecimal.valueOf(25)) > 0) {
            step = 2;
        }

        return MultiCurrencyMonetaryAmount.of(
                currentPrice.originalAmount().add(Money.of(step, currentPrice.getOriginalCurrency())));
    }
}
