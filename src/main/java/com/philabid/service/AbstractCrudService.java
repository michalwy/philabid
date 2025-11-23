package com.philabid.service;

import com.philabid.database.CrudRepository;
import com.philabid.database.util.FilterCondition;
import com.philabid.database.util.query.QueryOrder;
import com.philabid.model.BaseModel;

import java.util.Collection;
import java.util.Optional;

public abstract class AbstractCrudService<T extends BaseModel<T>> extends CrudService<T> {
    private final CrudRepository<T> crudRepository;

    public AbstractCrudService(CrudRepository<T> crudRepository) {
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

    public final Collection<T> getAll(Collection<FilterCondition> filterConditions, Collection<QueryOrder> orders) {
        return crudRepository.findAll(filterConditions, orders);
    }

    public Optional<T> getById(Long id) {
        return crudRepository.findById(id);
    }

    protected CrudRepository<T> getCrudRepository() {
        return crudRepository;
    }
}