package com.philabid.database;

import com.philabid.database.util.*;
import com.philabid.model.ValuationEntry;

import java.util.Collection;
import java.util.List;

public class ValuationEntryRepository extends VirtualViewCrudRepository<ValuationEntry> {
    private static final Collection<QueryField<ValuationEntry, ?>> FIELDS = List.of(
            new StringQueryField<>("ai", "catalog_number", "auction_item_catalog_number",
                    ValuationEntry::setAuctionItemCatalogNumber),
            new LongQueryField<>("ai", "order_number", "auction_item_order_number",
                    ValuationEntry::setAuctionItemOrderNumber),
            new StringQueryField<>("cond", "name", "condition_name", ValuationEntry::setConditionName),
            new StringQueryField<>("cond", "code", "condition_code", ValuationEntry::setConditionCode),
            new LongQueryField<>("aiv", "auction_item_id", ValuationEntry::setAuctionItemId),
            new LongQueryField<>("aiv", "condition_id", ValuationEntry::setConditionId),
            new StringQueryField<>("catg", "name", "auction_item_category_name",
                    ValuationEntry::setAuctionItemCategoryName),
            new StringQueryField<>("catg", "code", "auction_item_category_code",
                    ValuationEntry::setAuctionItemCategoryCode),
            new LongQueryField<>("catg", "id", "auction_item_category_id", ValuationEntry::setAuctionItemCategoryId),
            new CurrencyQueryField<>("cv", "currency_code", "catalog_currency_code", null),
            new MonetaryAmountQueryField<>("cv", "value", "catalog_value", "catalog_currency_code",
                    ValuationEntry::setCatalogValue),
            new BooleanQueryField<>("cat", "is_active", ValuationEntry::setCatalogActive),
            new CurrencyQueryField<>("a", "currency_code", "price_currency_code", null),
            new MonetaryAmountQueryField<>("a", "current_price", "price_currency_code", ValuationEntry::setPrice),
            new DoubleQueryField<>("a", "archived_catalog_value_percentage",
                    ValuationEntry::setArchivedCatalogValuePercentage)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("auctions", "a",
                    "aiv.auction_item_id = a.auction_item_id AND aiv.condition_id = a.condition_id AND a.archived = 1" +
                            " AND a.current_price IS NOT NULL"),
            new QueryInnerJoin("auction_items", "ai", "ai.id = aiv.auction_item_id"),
            new QueryLeftOuterJoin("catalog_values", "cv",
                    "aiv.auction_item_id = cv.auction_item_id AND aiv.condition_id = cv.condition_id"),
            new QueryLeftOuterJoin("categories", "catg", "ai.category_id = catg.id"),
            new QueryLeftOuterJoin("catalogs", "cat", "cv.catalog_id = cat.id"),
            new QueryLeftOuterJoin("conditions", "cond", "aiv.condition_id = cond.id")
    );

    public ValuationEntryRepository(DatabaseManager databaseManager) {
        super(databaseManager, ValuationEntry.class, """
                (SELECT a_i.auction_item_id, a_i.condition_id
                      FROM auctions a_i
                      UNION
                      SELECT cv_i.auction_item_id, cv_i.condition_id
                      FROM catalog_values cv_i)""", "aiv");
        addFields(FIELDS);
        addJoins(JOINS);
    }
}