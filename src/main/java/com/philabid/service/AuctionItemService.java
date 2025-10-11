package com.philabid.service;

import com.philabid.database.AuctionItemRepository;
import com.philabid.model.AuctionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Auction Items.
 */
public class AuctionItemService implements CrudService<AuctionItem> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemService.class);
    private final AuctionItemRepository auctionItemRepository;

    public AuctionItemService(AuctionItemRepository auctionItemRepository) {
        this.auctionItemRepository = auctionItemRepository;
    }

    public AuctionItem create() {
        return new AuctionItem();
    }

    @Override
    public List<AuctionItem> getAll() {
        try {
            return auctionItemRepository.findAll();
        } catch (SQLException e) {
            logger.error("Failed to retrieve all auction items", e);
            return Collections.emptyList();
        }
    }

    public Optional<AuctionItem> save(AuctionItem auctionItem) {
        if (auctionItem.getCatalogNumber() == null || auctionItem.getCatalogNumber().trim().isEmpty()) {
            logger.warn("Attempted to save an auction item with an empty catalog number.");
            return Optional.empty();
        }

        try {
            return Optional.of(auctionItemRepository.save(auctionItem));
        } catch (SQLException e) {
            logger.error("Failed to save auction item: {}", auctionItem.getCatalogNumber(), e);
            return Optional.empty();
        }
    }

    public boolean delete(Long id) {
        try {
            return auctionItemRepository.deleteById(id);
        } catch (SQLException e) {
            logger.error("Failed to delete auction item with ID: {}", id, e);
            return false;
        }
    }

    public Optional<AuctionItem> getAuctionItemById(Long id) {
        try {
            return auctionItemRepository.findById(id);
        } catch (SQLException e) {
            logger.error("Failed to retrieve auction item with ID: {}", id, e);
            return Optional.empty();
        }
    }
}
