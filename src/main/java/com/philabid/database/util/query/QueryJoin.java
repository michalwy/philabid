package com.philabid.database.util.query;

import java.util.Collection;
import java.util.List;

public class QueryJoin {

    private final JoinType type;
    private final String table;
    private final String alias;
    private final String condition;
    private final Collection<Object> params;

    public QueryJoin(JoinType type, String table, String alias, String condition) {
        this(type, table, alias, condition, List.of());
    }

    public QueryJoin(JoinType type, String table, String alias, String condition, Collection<Object> params) {
        this.type = type;
        this.table = table;
        this.alias = alias;
        this.condition = condition;
        this.params = params;
    }

    public String getSqlText() {
        return type.getType() + " JOIN " + table + (alias != null ? (" AS " + alias) : "") + " ON " + condition;
    }

    public Collection<Object> getParams() {
        return params;
    }

    public enum JoinType {
        LEFT_OUTER("LEFT OUTER"),
        INNER("INNER");

        private final String type;

        JoinType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
