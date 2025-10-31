package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.model.Auction;
import com.philabid.model.Valuation;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryOperators;
import org.javatuples.Pair;

import javax.money.MonetaryAmount;
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
        Optional<Pair<MonetaryAmount, MonetaryAmount>> catalogBoundary = getCatalogBoundary(auction);
        return auction.getArchivedAuctions().stream()
                .filter(a -> a.getCurrentPrice() != null)
                .map(a -> AppContext.getExchangeRateService().exchange(a.getCurrentPrice().originalAmount(),
                        AppContext.getConfigurationService().getDefaultCurrency(),
                        a.getEndDate().toLocalDate()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(m -> catalogBoundary.map(cv -> m.isGreaterThan(cv.getValue0()) && m.isLessThan(cv.getValue1()))
                        .orElse(true))
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
        return auction.getCategoryArchivedAuctions().stream()
                .map(Auction::getArchivedCatalogValuePercentage)
                .filter(d -> d != null && d > 0 &&
                        d < AppContext.getConfigurationService().getMaxPriceCatalogValueMultiplier())
                .map(p -> Pair.with(p, 1))
                .reduce((a, b) -> Pair.with(a.getValue0() + b.getValue0(), a.getValue1() + b.getValue1()))
                .map(p -> p.getValue0() / p.getValue1())
                .map(p -> auction.getCatalogValue().originalAmount().multiply(p))
                .map(amount -> amount.with(MonetaryOperators.rounding(RoundingMode.HALF_UP, 2)))
                .map(MultiCurrencyMonetaryAmount::of);
    }

    private Optional<Pair<MonetaryAmount, MonetaryAmount>> getCatalogBoundary(Auction auction) {
        return Optional.ofNullable(auction.getCatalogValue())
                .map(MultiCurrencyMonetaryAmount::defaultCurrencyAmount)
                .map(cv -> Pair.with(Money.of(0, AppContext.getConfigurationService().getDefaultCurrency()),
                        cv.multiply(AppContext.getConfigurationService().getMaxPriceCatalogValueMultiplier())));
    }
}
