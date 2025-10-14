package com.philabid.service;

import com.philabid.database.AuctionItemRepository;
import com.philabid.model.AuctionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer for managing Auction Items.
 */
public class AuctionItemService extends AbstractCrudService<AuctionItem> {

    private static final Logger logger = LoggerFactory.getLogger(AuctionItemService.class);

    public AuctionItemService(AuctionItemRepository auctionItemRepository) {
        super(auctionItemRepository);
    }

    protected boolean validate(AuctionItem auctionItem) {
        if (auctionItem.getCatalogNumber() == null || auctionItem.getCatalogNumber().trim().isEmpty()) {
            logger.warn("Attempted to save an auction item with an empty catalog number.");
            return false;
        }
        return true;
    }
}
