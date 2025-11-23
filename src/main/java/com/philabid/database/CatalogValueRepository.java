package com.philabid.database;

import com.philabid.database.util.EqualFilterCondition;
import com.philabid.database.util.query.*;
import com.philabid.model.CatalogValue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing CatalogValue entities in the database.
 */
public class CatalogValueRepository extends CrudRepository<CatalogValue> {
    private static final Collection<QueryField<CatalogValue, ?>> FIELDS = List.of(
            new LongQueryField<>("trading_item_id", CatalogValue::setTradingItemId).withEntityValue(
                    CatalogValue::getTradingItemId),
            new LongQueryField<>("condition_id", CatalogValue::setConditionId).withEntityValue(
                    CatalogValue::getConditionId),
            new LongQueryField<>("cv", "catalog_id", CatalogValue::setCatalogId).withEntityValue(
                    CatalogValue::getCatalogId),
            new CurrencyQueryField<CatalogValue>("cv", "currency_code", null).withMultiCurrencyEntityValue(
                    CatalogValue::getValue),
            new MonetaryAmountQueryField<CatalogValue>("cv", "value", "currency_code",
                    CatalogValue::setValue).withMultiCurrencyEntityValue(CatalogValue::getValue),
            new StringQueryField<>("catg", "name", "trading_item_category_name",
                    CatalogValue::setTradingItemCategoryName),
            new StringQueryField<>("catg", "code", "trading_item_category_code",
                    CatalogValue::setTradingItemCategoryCode),
            new StringQueryField<>("cat", "name", "catalog_name", CatalogValue::setCatalogName),
            new IntQueryField<>("cat", "issue_year", "catalog_issue_year", CatalogValue::setCatalogIssueYear),
            new StringQueryField<>("ti", "catalog_number", "trading_item_catalog_number",
                    CatalogValue::setTradingItemCatalogNumber),
            new LongQueryField<>("ti", "order_number", "trading_item_order_number",
                    CatalogValue::setTradingItemOrderNumber),
            new StringQueryField<>("cond", "name", "condition_name", CatalogValue::setConditionName),
            new StringQueryField<>("cond", "code", "condition_code", CatalogValue::setConditionCode)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("trading_items", "ti", "cv.trading_item_id = ti.id"),
            new QueryLeftOuterJoin("categories", "catg", "ti.category_id = catg.id"),
            new QueryLeftOuterJoin("catalogs", "cat", "cv.catalog_id = cat.id"),
            new QueryLeftOuterJoin("conditions", "cond", "cv.condition_id = cond.id")
    );

    public CatalogValueRepository(DatabaseManager databaseManager) {
        super(databaseManager, CatalogValue.class, "catalog_values", "cv");
        addFields(FIELDS);
        addJoins(JOINS);
    }

    public Optional<CatalogValue> findByTradingItemAndCondition(long tradingItemId, long conditionId) {
        return findOne(List.of(
                new EqualFilterCondition<>("cv.trading_item_id", tradingItemId),
                new EqualFilterCondition<>("cv.condition_id", conditionId)
        ));
    }
}
