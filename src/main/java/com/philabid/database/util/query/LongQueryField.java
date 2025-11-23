package com.philabid.database.util.query;

import com.philabid.model.BaseModel;

import java.sql.ResultSet;
import java.util.function.BiConsumer;

public class LongQueryField<T extends BaseModel<T>> extends QueryField<T, Long> {
    public LongQueryField(String table, String fieldName, String alias,
                          BiConsumer<T, Long> valueConsumer) {
        super(table, fieldName, alias, ResultSet::getLong, valueConsumer, null);
    }

    public LongQueryField(String tableName, String fieldName, BiConsumer<T, Long> valueConsumer) {
        this(tableName, fieldName, null, valueConsumer);
    }

    public LongQueryField(String fieldName, BiConsumer<T, Long> valueConsumer) {
        this(null, fieldName, null, valueConsumer);
    }
}
