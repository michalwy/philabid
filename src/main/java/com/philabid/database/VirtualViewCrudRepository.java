package com.philabid.database;

import com.philabid.model.BaseModel;

public class VirtualViewCrudRepository<T extends BaseModel<T>> extends CrudRepository<T> {
    public VirtualViewCrudRepository(DatabaseManager databaseManager, Class<T> entityClass, String tableName,
                                     String tableAlias) {
        super(databaseManager, entityClass, tableName, tableAlias, false);
    }
}
