package com.philabid.database.util;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetValueAccessor<R> {
    R apply(ResultSet rs, String fieldName) throws SQLException;
}