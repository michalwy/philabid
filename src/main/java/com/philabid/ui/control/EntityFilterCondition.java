package com.philabid.ui.control;

import com.philabid.model.BaseModel;

import java.util.Collection;
import java.util.List;

public class EntityFilterCondition<T extends BaseModel<T>> implements FilterCondition {
    private final T entity;
    private final String filterField;

    public EntityFilterCondition(String filterField, T entity) {
        this.filterField = filterField;
        this.entity = entity;
    }

    @Override
    public String getSqlText() {
        return filterField + " = ?";
    }

    @Override
    public Collection<Object> getSqlParams() {
        return List.of(entity.getId());
    }
}
