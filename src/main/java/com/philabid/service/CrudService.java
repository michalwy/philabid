package com.philabid.service;

import com.philabid.ui.control.FilterCondition;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CrudService<T> {
    T create();

    Optional<T> save(T item);

    boolean delete(Long id);

    List<T> getAll(Collection<FilterCondition> filterConditions);

    default List<T> getAll() {
        return getAll(List.of());
    }
}
