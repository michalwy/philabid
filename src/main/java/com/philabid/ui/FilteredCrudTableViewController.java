package com.philabid.ui;

import com.philabid.model.BaseModel;
import com.philabid.service.CrudService;
import com.philabid.ui.control.CrudTableViewCategoryFilter;
import com.philabid.ui.control.CrudTableViewConditionFilter;
import com.philabid.ui.control.CrudTableViewMultiFilter;

import java.util.Collection;
import java.util.List;

public abstract class FilteredCrudTableViewController<T extends BaseModel<T>> extends CrudTableViewController<T> {
    protected FilteredCrudTableViewController(CrudService<T> crudService) {
        super(crudService);
    }

    @Override
    protected Collection<T> loadTableItems() {
        return getCrudService().getAll(getCrudTableView().getFilterConditions());
    }

    @Override
    protected void initializeFilterToolbar() {
        getCrudTableView().addFilter(
                new CrudTableViewMultiFilter(List.of("ai.catalog_number", "catg.code")));
        getCrudTableView().addFilter(new CrudTableViewCategoryFilter());
        getCrudTableView().addFilter(new CrudTableViewConditionFilter());

        getCrudTableView().getFilterConditions().addListener((observable, oldValue, newValue) -> {
            refreshTable();
        });
    }
}
