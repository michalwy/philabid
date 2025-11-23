package com.philabid.database;

import com.philabid.database.util.query.*;
import com.philabid.model.ValuationEntry;

import java.util.Collection;
import java.util.List;

public class ValuationEntryRepository extends VirtualViewCrudRepository<ValuationEntry> {
    private static final Collection<QueryField<ValuationEntry, ?>> FIELDS = List.of(
            new StringQueryField<>("ti", "catalog_number", "trading_item_catalog_number",
                    ValuationEntry::setTradingItemCatalogNumber),
            new LongQueryField<>("ti", "order_number", "trading_item_order_number",
                    ValuationEntry::setTradingItemOrderNumber),
            new StringQueryField<>("cond", "name", "condition_name", ValuationEntry::setConditionName),
            new StringQueryField<>("cond", "code", "condition_code", ValuationEntry::setConditionCode),
            new LongQueryField<>("tiv", "trading_item_id", ValuationEntry::setTradingItemId),
            new LongQueryField<>("tiv", "condition_id", ValuationEntry::setConditionId),
            new StringQueryField<>("catg", "name", "trading_item_category_name",
                    ValuationEntry::setTradingItemCategoryName),
            new StringQueryField<>("catg", "code", "trading_item_category_code",
                    ValuationEntry::setTradingItemCategoryCode),
            new LongQueryField<>("catg", "id", "trading_item_category_id", ValuationEntry::setTradingItemCategoryId),
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
                    "tiv.trading_item_id = a.trading_item_id AND tiv.condition_id = a.condition_id AND a.archived = 1" +
                            " AND a.current_price IS NOT NULL"),
            new QueryInnerJoin("trading_items", "ti", "ti.id = tiv.trading_item_id"),
            new QueryLeftOuterJoin("catalog_values", "cv",
                    "tiv.trading_item_id = cv.trading_item_id AND tiv.condition_id = cv.condition_id"),
            new QueryLeftOuterJoin("categories", "catg", "ti.category_id = catg.id"),
            new QueryLeftOuterJoin("catalogs", "cat", "cv.catalog_id = cat.id"),
            new QueryLeftOuterJoin("conditions", "cond", "tiv.condition_id = cond.id")
    );

    public ValuationEntryRepository(DatabaseManager databaseManager) {
        super(databaseManager, ValuationEntry.class, """
                (SELECT a_i.trading_item_id, a_i.condition_id
                      FROM auctions a_i
                      UNION
                      SELECT cv_i.trading_item_id, cv_i.condition_id
                      FROM catalog_values cv_i)""", "tiv");
        addFields(FIELDS);
        addJoins(JOINS);
    }
}