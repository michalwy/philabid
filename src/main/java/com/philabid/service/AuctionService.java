package com.philabid.service;

import com.philabid.database.AuctionRepository;
import com.philabid.model.Auction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.philabid.AppContext;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Auctions.
 */
public class AuctionService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public List<Auction> getAllAuctions() {
        try {
            return auctionRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all auctions", e);
            return Collections.emptyList();
        }
    }

    public List<Auction> getActiveAuctions() {
        try {
            List<Auction> auctions = auctionRepository.findAllActive();
            auctions.forEach(this::enrichAuction);
            return auctions;
        } catch (SQLException e) {
            logger.error("Failed to retrieve active auctions", e);
            return Collections.emptyList();
        }
    }

    public List<Auction> getArchivedAuctions() {
        try {
            List<Auction> auctions = auctionRepository.findAllArchived();
            auctions.forEach(this::enrichAuction);
            return auctions;
        } catch (SQLException e) {
            logger.error("Failed to retrieve archived auctions", e);
            return Collections.emptyList();
        }
    }

    public Optional<Auction> saveAuction(Auction auction) {
        // Basic validation
        if (auction.getAuctionHouseId() == null || auction.getAuctionItemId() == null ||
                auction.getConditionId() == null) {
            logger.warn("Attempted to save an auction with missing required IDs.");
            return Optional.empty();
        }

        try {
            return Optional.of(auctionRepository.save(auction));
        } catch (SQLException e) {
            logger.error("Failed to save auction for item ID: {}", auction.getAuctionItemId(), e);
            return Optional.empty();
        }
    }

    public boolean deleteAuction(long id) {
        try {
            return auctionRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete auction with ID: {}", id, e);
            return false;
        }
    }

    private void enrichAuction(Auction auction) {
        // This is where we calculate derived properties after the main DB query is closed.
        // This prevents database locks by separating read and potential write (cache) operations.
        if (auction.getCurrentPrice() != null) {
            auction.setCurrentPrice(auction.getCurrentPrice().originalAmount());
        }
        if (auction.getMaxBid() != null) {
            auction.setMaxBid(auction.getMaxBid().originalAmount());
        }
        if (auction.getCatalogValue() != null) {
            auction.setCatalogValue(auction.getCatalogValue().originalAmount());
        }
        auction.setRecommendedPrice(AppContext.getPriceRecommendationService().calculateRecommendation(auction));
    }
}
