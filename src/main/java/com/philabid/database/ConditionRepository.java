package com.philabid.database;

import com.philabid.database.util.QueryField;
import com.philabid.database.util.StringQueryField;
import com.philabid.model.Condition;

import java.util.Collection;
import java.util.List;

/**
 * Repository for managing Condition entities in the database.
 */
public class ConditionRepository extends CrudRepository<Condition> {
    private static final Collection<QueryField<Condition, ?>> FIELDS = List.of(
            new StringQueryField<>("name", Condition::setName).withEntityValue(Condition::getName),
            new StringQueryField<>("code", Condition::setCode).withEntityValue(Condition::getCode)
    );

    public ConditionRepository(DatabaseManager databaseManager) {
        super(databaseManager, Condition.class, "conditions");
        addFields(FIELDS);
    }
}
