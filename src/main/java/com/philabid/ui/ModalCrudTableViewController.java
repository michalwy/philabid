package com.philabid.ui;

import com.philabid.model.BaseModel;
import com.philabid.service.AbstractCrudService;

public abstract class ModalCrudTableViewController<T extends BaseModel<T>> extends SimpleCrudTableViewController<T> {
    protected ModalCrudTableViewController(AbstractCrudService<T> crudService) {
        super(crudService);
    }

    @Override
    protected void postInitialize() {
        refresh();
    }
}
