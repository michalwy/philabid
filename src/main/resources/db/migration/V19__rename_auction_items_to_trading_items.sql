-- Rename auction_items table to trading_items
ALTER TABLE auction_items
    RENAME TO trading_items;

-- Rename relation fields auction_item_id to trading_item_id
ALTER TABLE auctions
    RENAME COLUMN auction_item_id TO trading_item_id;
ALTER TABLE catalog_values
    RENAME COLUMN auction_item_id TO trading_item_id;
