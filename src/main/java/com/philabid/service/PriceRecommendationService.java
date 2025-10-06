package com.philabid.service;

import com.philabid.model.Auction;
import com.philabid.util.MultiCurrencyMonetaryAmount;

public class PriceRecommendationService {
    public MultiCurrencyMonetaryAmount calculateRecommendation(Auction auction) {
        MultiCurrencyMonetaryAmount catalogValue = auction.getCatalogValue();
        if (catalogValue != null) {
            return MultiCurrencyMonetaryAmount.of(auction.getCatalogValue().defaultCurrencyAmount()
                    .multiply(auction.getAuctionItemCategoryAverageCatalogValuePercentage()));
        } else {
            return null;
        }
    }
}
