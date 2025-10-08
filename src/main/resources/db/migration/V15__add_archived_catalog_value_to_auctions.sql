-- Add columns to store a snapshot of the catalog value at the time of archival.
-- This is crucial for maintaining historical data integrity.
ALTER TABLE auctions
    ADD COLUMN archived_catalog_value DECIMAL(19, 4);
ALTER TABLE auctions
    ADD COLUMN archived_catalog_currency_code VARCHAR(3);