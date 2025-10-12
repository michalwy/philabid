package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.ui.control.FilterCondition;

import java.util.Collection;
import java.util.List;

public class ArchivedAuctionController extends BaseAuctionController {
    @Override
    public List<Auction> loadAuctions(Collection<FilterCondition> filterConditions) {
        return AppContext.getAuctionService().getArchivedAuctions(filterConditions);
    }
}