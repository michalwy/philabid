package com.philabid.database;

public class QueryExecutor {
    private final DatabaseManager databaseManager;
    private final String baseQuery;

    public QueryExecutor(DatabaseManager databaseManager, String baseQuery) {
        this.databaseManager = databaseManager;
        this.baseQuery = baseQuery;
    }
}
