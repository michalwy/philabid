package com.philabid.database;

import com.philabid.database.util.query.*;
import com.philabid.model.Category;

import java.util.Collection;
import java.util.List;

/**
 * Repository for managing Category entities in the database.
 */
public class CategoryRepository extends CrudRepository<Category> {
    private static final Collection<QueryField<Category, ?>> FIELDS = List.of(
            new StringQueryField<>("catg", "name", Category::setName).withEntityValue(Category::getName),
            new StringQueryField<>("catg", "code", Category::setCode).withEntityValue(Category::getCode),
            new LongQueryField<>("catg", "order_number", Category::setOrderNumber).withEntityValue(
                    Category::getOrderNumber),
            new LongQueryField<>("catg", "catalog_id", Category::setCatalogId).withEntityValue(Category::getCatalogId),
            new StringQueryField<>("cat", "name", "catalog_name", Category::setCatalogName),
            new IntQueryField<>("cat", "issue_year", "catalog_issue_year", Category::setCatalogIssueYear)
    );

    private static final Collection<QueryJoin> JOINS = List.of(
            new QueryLeftOuterJoin("catalogs", "cat", "catg.catalog_id = cat.id")
    );

    public CategoryRepository(DatabaseManager databaseManager) {
        super(databaseManager, Category.class, "categories", "catg");
        addFields(FIELDS);
        addJoins(JOINS);
    }
}
