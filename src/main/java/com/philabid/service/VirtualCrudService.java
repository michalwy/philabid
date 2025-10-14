package com.philabid.service;

import com.philabid.model.BaseModel;

import javax.naming.OperationNotSupportedException;
import java.util.Optional;

public abstract class VirtualCrudService<T extends BaseModel<T>> extends CrudService<T> {
    public final T create() {
        throw new RuntimeException(new OperationNotSupportedException());
    }

    public final Optional<T> save(T entity) {
        throw new RuntimeException(new OperationNotSupportedException());
    }

    protected boolean validate(T entity) {
        throw new RuntimeException(new OperationNotSupportedException());
    }

    public final boolean delete(Long id) {
        throw new RuntimeException(new OperationNotSupportedException());
    }
}