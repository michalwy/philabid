package com.philabid.database.util.query;

import com.philabid.model.BaseModel;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

public class TimestampQueryField<T extends BaseModel<T>> extends QueryField<T, LocalDateTime> {
    public TimestampQueryField(String table, String fieldName, String alias,
                               BiConsumer<T, LocalDateTime> valueConsumer) {
        super(table, fieldName, alias, (r, name) -> r.getTimestamp(name).toLocalDateTime(), valueConsumer,
                (stmt, index, dt) -> {
                    stmt.setTimestamp(index, Timestamp.valueOf(dt));
                });
    }

    public TimestampQueryField(String table, String fieldName, BiConsumer<T, LocalDateTime> valueConsumer) {
        this(table, fieldName, null, valueConsumer);
    }

    public TimestampQueryField(String fieldName, BiConsumer<T, LocalDateTime> valueConsumer) {
        this(null, fieldName, null, valueConsumer);
    }
}
