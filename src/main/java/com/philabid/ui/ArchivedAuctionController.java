package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Auction;

import java.util.Collection;

public class ArchivedAuctionController extends BaseAuctionController {
    @Override
    public Collection<Auction> loadAuctions(Collection<FilterCondition> filterConditions) {
        return AppContext.getAuctionService().getArchivedAuctions(filterConditions);
    }
}