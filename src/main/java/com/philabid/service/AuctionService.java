package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.database.AuctionRepository;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Service layer for managing Auctions.
 */
public class AuctionService extends CrudService<Auction> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        super(auctionRepository);
        this.auctionRepository = auctionRepository;
    }

    public Collection<Auction> getActiveAuctions(Collection<FilterCondition> filterConditions) {
        try {
            Collection<Auction> auctions = auctionRepository.findAllActive(filterConditions);
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

    public Collection<Auction> getArchivedAuctions(Collection<FilterCondition> filterConditions) {
        Collection<Auction> auctions = auctionRepository.findAllArchived(filterConditions);
        auctions.forEach(this::enrichAuction);
        return auctions;
    }

    protected boolean validate(Auction auction) {
        if (auction.getAuctionHouseId() == null || auction.getAuctionItemId() == null ||
                auction.getConditionId() == null) {
            logger.warn("Attempted to save an auction with missing required IDs.");
            return false;
        }
        return true;
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
