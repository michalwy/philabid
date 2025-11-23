package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.database.util.query.QueryOrder;
import com.philabid.model.Category;

import java.util.List;

public class CrudTableViewCategoryFilter extends CrudTableViewComboBoxFilter<Category> {
    public CrudTableViewCategoryFilter() {
        super("Category:", "catg.id", AppContext.getCategoryService(),
                List.of(new QueryOrder("catg", "code", QueryOrder.Direction.ASC)));
    }
}
