package com.philabid.database;

import com.philabid.database.util.query.*;
import com.philabid.model.TradingItem;

import java.util.Collection;
import java.util.List;

/**
 * Repository for managing TradingItem entities in the database.
 */
public class TradingItemRepository extends CrudRepository<TradingItem> {
    private static final Collection<QueryField<TradingItem, ?>> FIELDS = List.of(
            new StringQueryField<>("catalog_number", TradingItem::setCatalogNumber).withEntityValue(
                    TradingItem::getCatalogNumber),
            new LongQueryField<>("ti", "order_number", TradingItem::setOrderNumber).withEntityValue(
                    TradingItem::getOrderNumber),
            new StringQueryField<>("notes", TradingItem::setNotes).withEntityValue(TradingItem::getNotes),
            new LongQueryField<>("category_id", TradingItem::setCategoryId).withEntityValue(TradingItem::getCategoryId),
            new StringQueryField<>("catg", "name", "category_name", TradingItem::setCategoryName),
            new StringQueryField<>("catg", "code", "category_code", TradingItem::setCategoryCode),
            new LongQueryField<>("catg", "order_number", "category_order_number", TradingItem::setCategoryOrderNumber),
            new StringQueryField<>("cat", "name", "catalog_name", TradingItem::setCatalogName),
            new IntQueryField<>("cat", "issue_year", "catalog_issue_year", TradingItem::setCatalogIssueYear)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("categories", "catg", "ti.category_id = catg.id"),
            new QueryLeftOuterJoin("catalogs", "cat", "catg.catalog_id = cat.id")
    );

    public TradingItemRepository(DatabaseManager databaseManager) {
        super(databaseManager, TradingItem.class, "trading_items", "ti");
        addFields(FIELDS);
        addJoins(JOINS);
    }
}
