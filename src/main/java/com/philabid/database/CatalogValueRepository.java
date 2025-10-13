package com.philabid.database;

import com.philabid.database.util.*;
import com.philabid.model.CatalogValue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CatalogValue entities in the database.
 */
public class CatalogValueRepository extends CrudRepository<CatalogValue> {
    private static final Collection<QueryField<CatalogValue, ?>> FIELDS = List.of(
            new LongQueryField<>("auction_item_id", CatalogValue::setAuctionItemId).withEntityValue(
                    CatalogValue::getAuctionItemId),
            new LongQueryField<>("condition_id", CatalogValue::setConditionId).withEntityValue(
                    CatalogValue::getConditionId),
            new LongQueryField<>("cv", "catalog_id", CatalogValue::setCatalogId).withEntityValue(
                    CatalogValue::getCatalogId),
            new CurrencyQueryField<CatalogValue>("cv", "currency_code", null).withMultiCurrencyEntityValue(
                    CatalogValue::getValue),
            new MonetaryAmountQueryField<CatalogValue>("cv", "value", "currency_code",
                    CatalogValue::setValue).withMultiCurrencyEntityValue(CatalogValue::getValue),
            new StringQueryField<>("catg", "name", "auction_item_category_name",
                    CatalogValue::setAuctionItemCategoryName),
            new StringQueryField<>("catg", "code", "auction_item_category_code",
                    CatalogValue::setAuctionItemCategoryCode),
            new StringQueryField<>("cat", "name", "catalog_name", CatalogValue::setCatalogName),
            new IntQueryField<>("cat", "issue_year", "catalog_issue_year", CatalogValue::setCatalogIssueYear),
            new StringQueryField<>("ai", "catalog_number", "auction_item_catalog_number",
                    CatalogValue::setAuctionItemCatalogNumber),
            new LongQueryField<>("ai", "order_number", "auction_item_order_number",
                    CatalogValue::setAuctionItemOrderNumber),
            new StringQueryField<>("cond", "name", "condition_name", CatalogValue::setConditionName),
            new StringQueryField<>("cond", "code", "condition_code", CatalogValue::setConditionCode)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("auction_items", "ai", "cv.auction_item_id = ai.id"),
            new QueryLeftOuterJoin("categories", "catg", "ai.category_id = catg.id"),
            new QueryLeftOuterJoin("catalogs", "cat", "cv.catalog_id = cat.id"),
            new QueryLeftOuterJoin("conditions", "cond", "cv.condition_id = cond.id")
    );

    public CatalogValueRepository(DatabaseManager databaseManager) {
        super(databaseManager, CatalogValue.class, "catalog_values", "cv");
        addFields(FIELDS);
        addJoins(JOINS);
    }

    public Optional<CatalogValue> findByAuctionItemAndCondition(long auctionItemId, long conditionId) {
        return findOne(List.of(
                new EqualFilterCondition<>("cv.auction_item_id", auctionItemId),
                new EqualFilterCondition<>("cv.condition_id", conditionId)
        ));
    }
}
