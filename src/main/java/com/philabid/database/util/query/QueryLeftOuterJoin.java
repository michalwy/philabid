package com.philabid.database.util.query;

import java.util.Collection;
import java.util.List;

public class QueryLeftOuterJoin extends QueryJoin {
    public QueryLeftOuterJoin(String table, String alias, String condition) {
        this(table, alias, condition, List.of());
    }

    public QueryLeftOuterJoin(String table, String alias, String condition, Collection<Object> params) {
        super(JoinType.LEFT_OUTER, table, alias, condition, params);
    }
}
