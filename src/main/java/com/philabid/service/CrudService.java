package com.philabid.service;

import com.philabid.database.CrudRepository;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.BaseModel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class CrudService<T extends BaseModel<T>> {
    private final CrudRepository<T> crudRepository;

    public CrudService(CrudRepository<T> crudRepository) {
        this.crudRepository = crudRepository;
    }

    public final T create() {
        return crudRepository.create();
    }

    public final Optional<T> save(T entity) {
        if (!validate(entity)) {
            return Optional.empty();
        }

        return crudRepository.save(entity);
    }

    protected abstract boolean validate(T entity);

    public final boolean delete(Long id) {
        return crudRepository.delete(id);
    }

    public final Collection<T> getAll() {
        return getAll(List.of());
    }

    public final Collection<T> getAll(Collection<FilterCondition> filterConditions) {
        return crudRepository.findAll(filterConditions);
    }

    public Optional<T> getById(Long id) {
        return crudRepository.findById(id);
    }
}