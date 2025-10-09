ALTER TABLE auctions
    DROP COLUMN archived_catalog_value_percentage;

ALTER TABLE auctions
    ADD COLUMN archived_catalog_value_percentage REAL;

UPDATE auctions
SET archived_catalog_value_percentage = 1.0 * current_price / archived_catalog_value
WHERE auctions.archived = 1
  AND auctions.archived_catalog_value IS NOT NULL
  AND auctions.archived_catalog_value != 0;