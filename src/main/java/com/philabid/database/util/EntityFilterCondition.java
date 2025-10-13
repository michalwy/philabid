package com.philabid.database.util;

import com.philabid.model.BaseModel;

public class EntityFilterCondition<T extends BaseModel<T>> extends EqualFilterCondition<Long> {
    public EntityFilterCondition(String filterField, T entity) {
        super(filterField, entity.getId());
    }
}
