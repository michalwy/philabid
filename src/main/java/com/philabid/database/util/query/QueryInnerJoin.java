package com.philabid.database.util.query;

import java.util.Collection;
import java.util.List;

public class QueryInnerJoin extends QueryJoin {
    public QueryInnerJoin(String table, String alias, String condition) {
        this(table, alias, condition, List.of());
    }

    public QueryInnerJoin(String table, String alias, String condition, Collection<Object> params) {
        super(JoinType.INNER, table, alias, condition, params);
    }
}
