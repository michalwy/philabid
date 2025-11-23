package com.philabid.database.util.query;

import com.philabid.model.BaseModel;

public class InternalQueryField<T extends BaseModel<T>> extends QueryField<T, Void> {
    public InternalQueryField(String table, String fieldName, String alias) {
        super(table, fieldName, alias, (rs, name) -> null, (entity, value) -> {
        }, null);
    }

    public InternalQueryField(String tableName, String fieldName) {
        this(tableName, fieldName, null);
    }

    public InternalQueryField(String fieldName) {
        this(null, fieldName, null);
    }
}
