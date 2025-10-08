-- Backfill the new archived_catalog_value columns for existing archived auctions
-- that don't have this value set yet.
-- This ensures historical data integrity after the schema change.
UPDATE auctions
SET archived_catalog_value         = (SELECT cv.value
                                      FROM catalog_values cv
                                      WHERE cv.auction_item_id = auctions.auction_item_id
                                        AND cv.condition_id = auctions.condition_id
                                      LIMIT 1),
    archived_catalog_currency_code = (SELECT cv.currency_code
                                      FROM catalog_values cv
                                      WHERE cv.auction_item_id = auctions.auction_item_id
                                        AND cv.condition_id = auctions.condition_id
                                      LIMIT 1)
WHERE auctions.archived = 1
  AND auctions.archived_catalog_value IS NULL;