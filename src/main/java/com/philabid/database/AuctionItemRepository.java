package com.philabid.database;

import com.philabid.database.util.*;
import com.philabid.model.AuctionItem;

import java.util.Collection;
import java.util.List;

/**
 * Repository for managing AuctionItem entities in the database.
 */
public class AuctionItemRepository extends CrudRepository<AuctionItem> {
    private static final Collection<QueryField<AuctionItem, ?>> FIELDS = List.of(
            new StringQueryField<>("catalog_number", AuctionItem::setCatalogNumber).withEntityValue(
                    AuctionItem::getCatalogNumber),
            new LongQueryField<>("order_number", AuctionItem::setOrderNumber).withEntityValue(
                    AuctionItem::getOrderNumber),
            new StringQueryField<>("notes", AuctionItem::setNotes).withEntityValue(AuctionItem::getNotes),
            new LongQueryField<>("category_id", AuctionItem::setCategoryId).withEntityValue(AuctionItem::getCategoryId),
            new StringQueryField<>("catg", "name", "category_name", AuctionItem::setCategoryName),
            new StringQueryField<>("catg", "code", "category_code", AuctionItem::setCategoryCode),
            new StringQueryField<>("cat", "name", "catalog_name", AuctionItem::setCatalogName),
            new IntQueryField<>("cat", "issue_year", "catalog_issue_year", AuctionItem::setCatalogIssueYear)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("categories", "catg", "ai.category_id = catg.id"),
            new QueryLeftOuterJoin("catalogs", "cat", "catg.catalog_id = cat.id")
    );

    public AuctionItemRepository(DatabaseManager databaseManager) {
        super(databaseManager, AuctionItem.class, "auction_items", "ai");
        addFields(FIELDS);
        addJoins(JOINS);
    }
}
