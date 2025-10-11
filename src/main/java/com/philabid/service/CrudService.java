package com.philabid.service;

import java.util.Optional;

public interface CrudService<T> {
    T create();
    
    Optional<T> save(T item);

    boolean delete(Long id);
}
