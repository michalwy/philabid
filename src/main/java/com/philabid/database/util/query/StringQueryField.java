package com.philabid.database.util.query;

import com.philabid.model.BaseModel;

import java.sql.ResultSet;
import java.util.function.BiConsumer;

public class StringQueryField<T extends BaseModel<T>> extends QueryField<T, String> {
    public StringQueryField(String table, String fieldName, String alias,
                            BiConsumer<T, String> valueConsumer) {
        super(table, fieldName, alias, ResultSet::getString, valueConsumer, null);
    }

    public StringQueryField(String table, String fieldName, BiConsumer<T, String> valueConsumer) {
        this(table, fieldName, null, valueConsumer);
    }

    public StringQueryField(String fieldName, BiConsumer<T, String> valueConsumer) {
        this(null, fieldName, null, valueConsumer);
    }
}
