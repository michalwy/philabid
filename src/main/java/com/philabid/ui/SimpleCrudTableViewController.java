package com.philabid.ui;

import com.philabid.model.BaseModel;
import com.philabid.service.CrudService;

public abstract class SimpleCrudTableViewController<T extends BaseModel<T>> extends CrudTableViewController<T> {
    protected SimpleCrudTableViewController(CrudService<T> crudService) {
        super(crudService);
    }
}
