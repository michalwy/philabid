package com.philabid.ui;

import com.philabid.model.BaseModel;
import com.philabid.service.CrudService;
import com.philabid.ui.control.CrudTableViewCategoryFilter;
import com.philabid.ui.control.CrudTableViewConditionFilter;
import com.philabid.ui.control.CrudTableViewMultiFilter;

import java.util.Collection;
import java.util.List;

public abstract class FilteredCrudTableViewController<T extends BaseModel<T>> extends CrudTableViewController<T> {
    private final CrudTableViewMultiFilter multiFilter =
            new CrudTableViewMultiFilter(List.of("ti.catalog_number", "catg.code"));
    private final CrudTableViewCategoryFilter categoryFilter = new CrudTableViewCategoryFilter();
    private final CrudTableViewConditionFilter conditionFilter = new CrudTableViewConditionFilter();

    protected FilteredCrudTableViewController(CrudService<T> crudService) {
        super(crudService);
    }

    @Override
    protected Collection<T> loadTableItems() {
        return getCrudService().getAll(getCrudTableView().getFilterConditions());
    }

    @Override
    protected void initializeFilterToolbar() {
        getCrudTableView().addFilter(multiFilter);
        getCrudTableView().addFilter(categoryFilter);
        getCrudTableView().addFilter(conditionFilter);

        getCrudTableView().getFilterConditions().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
    }

    protected CrudTableViewMultiFilter getMultiFilter() {
        return multiFilter;
    }

    protected CrudTableViewCategoryFilter getCategoryFilter() {
        return categoryFilter;
    }

    protected CrudTableViewConditionFilter getConditionFilter() {
        return conditionFilter;
    }
}
