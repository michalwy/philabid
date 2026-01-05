package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.database.AuctionRepository;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing Auctions.
 */
public class AuctionService extends AbstractCrudService<Auction> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);
    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        super(auctionRepository);
        this.auctionRepository = auctionRepository;
    }

    public Collection<Auction> getActiveAuctions(Collection<FilterCondition> filterConditions) {
        Collection<Auction> auctions = auctionRepository.findAllActive(filterConditions);
        Collection<Auction> allActiveAuctions = auctionRepository.findAllActive(List.of());
        allActiveAuctions.forEach(this::enrichAuction);
        Map<Long, List<Auction>> auctionsArchiveMap = auctionRepository.findArchivedForActiveAuctions();
        Map<Pair<Long, Long>, List<Auction>> categoriesArchiveMap =
                auctionRepository.findArchivedForActiveCategories();

        Map<Pair<Long, Long>, List<Auction>> auctionsActiveMap =
                allActiveAuctions.stream()
                        .collect(Collectors.groupingBy(a -> Pair.with(a.getTradingItemId(), a.getConditionId())));

        auctionsArchiveMap.values().stream().flatMap(List::stream).forEach(this::enrichAuction);
        auctions.forEach(
                auction -> enrichAuction(auction, auctionsArchiveMap, categoriesArchiveMap, auctionsActiveMap));
        return auctions;
    }

    public Collection<Auction> getArchivedAuctions(Collection<FilterCondition> filterConditions) {
        Collection<Auction> auctions = auctionRepository.findAllArchived(filterConditions);
        auctions.forEach(this::enrichAuction);
        return auctions;
    }

    public Collection<Auction> getArchivedAuctionsForItem(Long tradingItemId, Long conditionId) {
        Collection<Auction> auctions = auctionRepository.findArchivedByItemAndCondition(tradingItemId, conditionId);
        auctions.forEach(this::enrichAuction);
        return auctions;
    }

    public Collection<Auction> getActiveAuctionsForItem(Long tradingItemId, Long conditionId) {
        Collection<Auction> auctions = auctionRepository.findActiveByItemAndCondition(tradingItemId, conditionId);
        auctions.forEach(this::enrichAuction);
        return auctions;
    }

    public Collection<Auction> getByAuctionHouseAndLotId(Long auctionHouseId, String lotId) {
        return auctionRepository.findByAuctionHouseAndLotId(auctionHouseId, lotId);
    }

    protected boolean validate(Auction auction) {
        if (auction.getAuctionHouseId() == null || auction.getTradingItemId() == null ||
                auction.getConditionId() == null) {
            logger.warn("Attempted to save an auction with missing required IDs.");
            return false;
        }
        return true;
    }

    private void enrichAuction(Auction auction) {
        enrichAuction(auction, Map.of(), Map.of(), Map.of());
    }

    private void enrichAuction(Auction auction, Map<Long, List<Auction>> auctionArchiveMap,
                               Map<Pair<Long, Long>, List<Auction>> categoryArchiveMap,
                               Map<Pair<Long, Long>, List<Auction>> activeAuctionsMap) {
        // This is where we calculate derived properties after the main DB query is closed.
        // This prevents database locks by separating read and potential write (cache) operations.
        if (auction.getCurrentPrice() != null) {
            auction.setCurrentPrice(auction.getCurrentPrice().originalAmount());
        }
        if (auction.getStartingPrice() != null) {
            auction.setStartingPrice(auction.getStartingPrice().originalAmount());
        }
        if (auction.getMaxBid() != null) {
            auction.setMaxBid(auction.getMaxBid().originalAmount());
        }
        if (auction.getCatalogValue() != null) {
            auction.setCatalogValue(auction.getCatalogValue().originalAmount());
        }
        auction.setActiveAuctions(
                activeAuctionsMap.getOrDefault(Pair.with(auction.getTradingItemId(), auction.getConditionId()),
                        List.of()));
        auction.setArchivedAuctions(auctionArchiveMap.getOrDefault(auction.getId(), List.of()));
        auction.setCategoryArchivedAuctions(
                categoryArchiveMap.getOrDefault(Pair.with(auction.getTradingItemCategoryId(), auction.getConditionId()),
                        List.of()));
        auction.setRecommendedPrice(
                AppContext.getPriceRecommendationService().calculateRecommendation(auction).orElse(null));
    }
}
