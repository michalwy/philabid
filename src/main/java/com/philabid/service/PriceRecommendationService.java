package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.model.Valuation;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import org.javamoney.moneta.function.MonetaryOperators;
import org.javatuples.Pair;

import java.math.RoundingMode;
import java.util.Optional;

public class PriceRecommendationService {
    public Optional<MultiCurrencyMonetaryAmount> calculateRecommendation(Auction auction) {
        return calculateRecommendationFromAuctionItem(auction)
                .or(() -> calculateRecommendationFromCategory(auction));
    }

    public Optional<MultiCurrencyMonetaryAmount> calculateRecommendation(Valuation valuation) {
        return Optional.ofNullable(valuation.getAveragePrice())
                .or(() -> Optional.ofNullable(valuation.getCategoryAveragePrice()));
    }

    private Optional<MultiCurrencyMonetaryAmount> calculateRecommendationFromAuctionItem(Auction auction) {
        return auction.getArchivedAuctions().stream()
                .filter(a -> a.getCurrentPrice() != null)
                .map(a -> AppContext.getExchangeRateService().exchange(a.getCurrentPrice().originalAmount(),
                        AppContext.getConfigurationService().getDefaultCurrency(),
                        a.getEndDate().toLocalDate()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(m -> Pair.with(m, 1))
                .reduce((a, b) -> Pair.with(a.getValue0().add(b.getValue0()), a.getValue1() + b.getValue1()))
                .map(p -> p.getValue0().divide(p.getValue1()))
                .map(amount -> amount.with(MonetaryOperators.rounding(RoundingMode.HALF_UP, 2)))
                .map(MultiCurrencyMonetaryAmount::of);
    }

    private Optional<MultiCurrencyMonetaryAmount> calculateRecommendationFromCategory(Auction auction) {
        if (auction.getCatalogValue() == null) {
            return Optional.empty();
        }
        return auction.getCategoryArchivedAuction().stream()
                .map(Auction::getArchivedCatalogValuePercentage)
                .filter(d -> d != null && d > 0)
                .map(p -> Pair.with(p, 1))
                .reduce((a, b) -> Pair.with(a.getValue0() + b.getValue0(), a.getValue1() + b.getValue1()))
                .map(p -> p.getValue0() / p.getValue1())
                .map(p -> auction.getCatalogValue().originalAmount().multiply(p))
                .map(amount -> amount.with(MonetaryOperators.rounding(RoundingMode.HALF_UP, 2)))
                .map(MultiCurrencyMonetaryAmount::of);
    }
}
