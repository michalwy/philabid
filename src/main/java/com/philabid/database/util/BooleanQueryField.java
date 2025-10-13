package com.philabid.database.util;

import com.philabid.model.BaseModel;

import java.util.function.BiConsumer;

public class BooleanQueryField<T extends BaseModel<T>> extends QueryField<T, Boolean> {
    public BooleanQueryField(String table, String fieldName, String alias,
                             BiConsumer<T, Boolean> valueConsumer) {
        super(table, fieldName, alias, (rs, name) -> rs.getInt(name) == 1, valueConsumer, null);
    }

    public BooleanQueryField(String table, String fieldName, BiConsumer<T, Boolean> valueConsumer) {
        this(table, fieldName, null, valueConsumer);
    }

    public BooleanQueryField(String fieldName, BiConsumer<T, Boolean> valueConsumer) {
        this(null, fieldName, null, valueConsumer);
    }
}
