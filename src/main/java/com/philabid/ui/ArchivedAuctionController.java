package com.philabid.ui;

import com.philabid.AppContext;
import com.philabid.model.Auction;

import java.util.List;

public class ArchivedAuctionController extends BaseAuctionController {
    @Override
    public List<Auction> loadAuctions() {
        return AppContext.getAuctionService().getArchivedAuctions();
    }
}