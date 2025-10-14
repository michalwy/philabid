package com.philabid.service;

import com.philabid.database.util.FilterCondition;
import com.philabid.model.BaseModel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class CrudService<T extends BaseModel<T>> {
    public abstract T create();

    public abstract Optional<T> save(T entity);

    protected abstract boolean validate(T entity);

    public abstract boolean delete(Long id);

    public Collection<T> getAll() {
        return getAll(List.of());
    }

    public abstract Collection<T> getAll(Collection<FilterCondition> filterConditions);

    public abstract Optional<T> getById(Long id);
}
