package com.philabid.ui.control;

import com.philabid.AppContext;
import com.philabid.model.Seller;

public class CrudTableViewSellerFilter extends CrudTableViewComboBoxFilter<Seller> {
    public CrudTableViewSellerFilter() {
        super("Seller:", "s.id", AppContext.getSellerService());
    }
}
