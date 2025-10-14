package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.Condition;

public class CrudTableViewConditionFilter extends CrudTableViewComboBoxFilter<Condition> {
    public CrudTableViewConditionFilter() {
        super("Condition:", "cond.id", AppContext.getConditionService());
    }
}
