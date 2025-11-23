package com.philabid.database;

import com.philabid.database.util.query.*;
import com.philabid.model.Catalog;

import java.util.Collection;
import java.util.List;

/**
 * Repository for managing Catalog entities in the database.
 */
public class CatalogRepository extends CrudRepository<Catalog> {
    private static final Collection<QueryField<Catalog, ?>> FIELDS = List.of(
            new StringQueryField<>("name", Catalog::setName).withEntityValue(Catalog::getName),
            new IntQueryField<>("issue_year", Catalog::setIssueYear).withEntityValue(Catalog::getIssueYear),
            new CurrencyQueryField<Catalog>("currency_code", Catalog::setCurrency).withEntityValue(
                    Catalog::getCurrency),
            new BooleanQueryField<>("is_active", Catalog::setActive).withEntityValue(Catalog::isActive)
    );

    public CatalogRepository(DatabaseManager databaseManager) {
        super(databaseManager, Catalog.class, "catalogs");
        addFields(FIELDS);
    }
}
