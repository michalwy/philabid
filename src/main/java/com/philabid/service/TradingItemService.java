package com.philabid.service;

import com.philabid.database.TradingItemRepository;
import com.philabid.model.TradingItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service layer for managing Trading Items.
 */
public class TradingItemService extends AbstractCrudService<TradingItem> {

    private static final Logger logger = LoggerFactory.getLogger(TradingItemService.class);

    public TradingItemService(TradingItemRepository tradingItemRepository) {
        super(tradingItemRepository);
    }

    protected boolean validate(TradingItem tradingItem) {
        if (tradingItem.getCatalogNumber() == null || tradingItem.getCatalogNumber().trim().isEmpty()) {
            logger.warn("Attempted to save an trading item with an empty catalog number.");
            return false;
        }
        return true;
    }
}
