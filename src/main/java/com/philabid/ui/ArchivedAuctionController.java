package com.philabid.ui;

import com.philabid.model.Auction;

import java.util.List;

public class ArchivedAuctionController extends BaseAuctionController {
    @Override
    public List<Auction> loadAuctions() {
        if (auctionService != null) {
            return auctionService.getArchivedAuctions();
        } else {
            return List.of();
        }
    }
}