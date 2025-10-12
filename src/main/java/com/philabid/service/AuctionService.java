package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.database.AuctionRepository;
import com.philabid.model.Auction;
import com.philabid.ui.control.FilterCondition;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * Service layer for managing Auctions.
 */
public class AuctionService implements CrudService<Auction> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Auction create() {
        return new Auction();
    }

    @Override
    public List<Auction> getAll(Collection<FilterCondition> filterConditions) {
        try {
            return auctionRepository.findAll(filterConditions);
        } catch (SQLException e) {
            logger.error("Failed to retrieve all auctions", e);
            return Collections.emptyList();
        }
    }

    public List<Auction> getActiveAuctions(Collection<FilterCondition> filterConditions) {
        try {
            List<Auction> auctions = auctionRepository.findAllActive(filterConditions);
            Map<Long, List<Auction>> auctionsArchiveMap = auctionRepository.findArchivedForActiveAuctions();
            Map<Pair<Long, Long>, List<Auction>> categoriesArchiveMap =
                    auctionRepository.findArchivedForActiveCategories();
            auctionsArchiveMap.values().stream().flatMap(List::stream).forEach(this::enrichAuction);
            auctions.forEach(auction -> enrichAuction(auction, auctionsArchiveMap, categoriesArchiveMap));
            return auctions;
        } catch (SQLException e) {
            logger.error("Failed to retrieve active auctions", e);
            return Collections.emptyList();
        }
    }

    public List<Auction> getArchivedAuctions(Collection<FilterCondition> filterConditions) {
        try {
            List<Auction> auctions = auctionRepository.findAllArchived(filterConditions);
            auctions.forEach(this::enrichAuction);
            return auctions;
        } catch (SQLException e) {
            logger.error("Failed to retrieve archived auctions", e);
            return Collections.emptyList();
        }
    }

    public Optional<Auction> save(Auction auction) {
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

    public boolean delete(Long id) {
        try {
            return auctionRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete auction with ID: {}", id, e);
            return false;
        }
    }

    private void enrichAuction(Auction auction) {
        enrichAuction(auction, Map.of(), Map.of());
    }

    private void enrichAuction(Auction auction, Map<Long, List<Auction>> auctionArchiveMap,
                               Map<Pair<Long, Long>, List<Auction>> categoryArchiveMap) {
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
        auction.setArchivedAuctions(auctionArchiveMap.getOrDefault(auction.getId(), List.of()));
        auction.setCategoryArchivedAuction(
                categoryArchiveMap.getOrDefault(Pair.with(auction.getAuctionItemCategoryId(), auction.getConditionId()),
                        List.of()));
        auction.setRecommendedPrice(
                AppContext.getPriceRecommendationService().calculateRecommendation(auction).orElse(null));
    }
}
