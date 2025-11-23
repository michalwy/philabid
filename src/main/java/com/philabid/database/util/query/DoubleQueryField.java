package com.philabid.database.util.query;

import com.philabid.model.BaseModel;

import java.sql.ResultSet;
import java.util.function.BiConsumer;

public class DoubleQueryField<T extends BaseModel<T>> extends QueryField<T, Double> {
    public DoubleQueryField(String table, String fieldName, String alias,
                            BiConsumer<T, Double> valueConsumer) {
        super(table, fieldName, alias, ResultSet::getDouble, valueConsumer, null);
    }

    public DoubleQueryField(String tableName, String fieldName, BiConsumer<T, Double> valueConsumer) {
        this(tableName, fieldName, null, valueConsumer);
    }

    public DoubleQueryField(String fieldName, BiConsumer<T, Double> valueConsumer) {
        this(null, fieldName, null, valueConsumer);
    }
}
