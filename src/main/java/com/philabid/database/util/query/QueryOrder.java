package com.philabid.database.util.query;

public class QueryOrder {
    String table;
    String field;
    Direction direction;

    public QueryOrder(String table, String field, Direction direction) {
        this.table = table;
        this.field = field;
        this.direction = direction;
    }

    public String getSqlText() {
        return table + "." + field + " " + direction.toString();
    }

    public enum Direction {
        ASC,
        DESC
    }
}
