package com.philabid.ui;

import com.philabid.model.BaseModel;
import com.philabid.service.AbstractCrudService;

public abstract class SimpleCrudTableViewController<T extends BaseModel<T>> extends CrudTableViewController<T> {
    protected SimpleCrudTableViewController(AbstractCrudService<T> crudService) {
        super(crudService);
    }
}
