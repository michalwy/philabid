package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.util.MultiCurrencyMonetaryAmount;

import java.util.List;
import java.util.Optional;

public class PriceRecommendationService {
    public Optional<MultiCurrencyMonetaryAmount> calculateRecommendation(Auction auction) {
        List<Auction> archivedAuctions = auction.getArchivedAuctions();
        return archivedAuctions.stream()
                .map(a -> AppContext.getExchangeRateService().exchange(a.getCurrentPrice().originalAmount(),
                        AppContext.getConfigurationService().getDefaultCurrency(),
                        a.getEndDate().toLocalDate()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce((a, b) -> a.isLessThan(b) ? a : b)
                .map(m -> AppContext.getExchangeRateService()
                        .exchange(m, auction.getCurrentPrice().getOriginalCurrency()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(MultiCurrencyMonetaryAmount::of);
    }
}
