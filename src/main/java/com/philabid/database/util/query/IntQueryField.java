package com.philabid.database.util.query;

import com.philabid.model.BaseModel;

import java.sql.ResultSet;
import java.util.function.BiConsumer;

public class IntQueryField<T extends BaseModel<T>> extends QueryField<T, Integer> {
    public IntQueryField(String table, String fieldName, String alias,
                         BiConsumer<T, Integer> valueConsumer) {
        super(table, fieldName, alias, ResultSet::getInt, valueConsumer, null);
    }

    public IntQueryField(String tableName, String fieldName, BiConsumer<T, Integer> valueConsumer) {
        this(tableName, fieldName, null, valueConsumer);
    }

    public IntQueryField(String fieldName, BiConsumer<T, Integer> valueConsumer) {
        this(null, fieldName, null, valueConsumer);
    }
}
