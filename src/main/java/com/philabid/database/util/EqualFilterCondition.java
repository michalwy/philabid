package com.philabid.database.util;

import java.util.Collection;
import java.util.List;

public class EqualFilterCondition<V> implements FilterCondition {
    private final String filterField;
    private final V filterValue;

    public EqualFilterCondition(String filterField, V filterValue) {
        this.filterField = filterField;
        this.filterValue = filterValue;
    }

    @Override
    public String getSqlText() {
        return filterField + " = ? COLLATE NOCASE";
    }

    @Override
    public Collection<Object> getSqlParams() {
        return List.of(filterValue);
    }
}
