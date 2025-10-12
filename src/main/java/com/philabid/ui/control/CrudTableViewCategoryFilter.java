package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.Category;

public class CrudTableViewCategoryFilter extends CrudTableViewComboBoxFilter<Category> {
    public CrudTableViewCategoryFilter() {
        super("Category:", "catg.id", AppContext.getCategoryService());
    }
}
