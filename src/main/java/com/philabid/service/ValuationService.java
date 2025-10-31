package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.database.ValuationEntryRepository;
import com.philabid.database.util.EqualFilterCondition;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Valuation;
import com.philabid.model.ValuationEntry;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryOperators;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.math.RoundingMode;
import java.util.*;

public class ValuationService extends VirtualCrudService<Valuation> {

    private static final Logger logger = LoggerFactory.getLogger(ValuationService.class);

    private final ValuationEntryRepository valuationEntryRepository;

    public ValuationService(ValuationEntryRepository valuationEntryRepository) {
        this.valuationEntryRepository = valuationEntryRepository;
    }

    public Collection<Valuation> getAll(Collection<FilterCondition> filterConditions) {
        Map<Pair<Long, Long>, Valuation> entries = new HashMap<>();
        Map<Pair<Long, Long>, List<Double>> categoryAveragePercentageEntries = new HashMap<>();

        valuationEntryRepository.findAll(filterConditions, valuationEntry -> {
            entries.computeIfAbsent(Pair.with(valuationEntry.getAuctionItemId(), valuationEntry.getConditionId()),
                    k -> {
                        Valuation valuation = new Valuation();
                        valuation.setAuctionItemId(valuationEntry.getAuctionItemId());
                        valuation.setAuctionItemCatalogNumber(valuationEntry.getAuctionItemCatalogNumber());
                        valuation.setAuctionItemOrderNumber(valuationEntry.getAuctionItemOrderNumber());
                        valuation.setAuctionItemCategoryName(valuationEntry.getAuctionItemCategoryName());
                        valuation.setAuctionItemCategoryCode(valuationEntry.getAuctionItemCategoryCode());
                        valuation.setAuctionItemCategoryId(valuationEntry.getAuctionItemCategoryId());
                        valuation.setConditionId(valuationEntry.getConditionId());
                        valuation.setConditionName(valuationEntry.getConditionName());
                        valuation.setConditionCode(valuationEntry.getConditionCode());
                        valuation.setCatalogValue(valuationEntry.getCatalogValue());
                        valuation.setCatalogActive(valuationEntry.isCatalogActive());
                        return valuation;
                    }).addValuationEntry(valuationEntry);
        });

        valuationEntryRepository.findAll(List.of(), valuationEntry -> {
            categoryAveragePercentageEntries.computeIfAbsent(
                    Pair.with(valuationEntry.getAuctionItemCategoryId(), valuationEntry.getConditionId()),
                    k -> new ArrayList<>()).add(valuationEntry.getArchivedCatalogValuePercentage());
        });

        Map<Pair<Long, Long>, Double> categoryAveragePercentages = new HashMap<>();

        categoryAveragePercentageEntries.forEach((k, v) -> {
            v.stream()
                    .filter(Objects::nonNull)
                    .filter(p -> p > 0 && p < AppContext.getConfigurationService().getMaxPriceCatalogValueMultiplier())
                    .map(p -> Pair.with(p, 1))
                    .reduce((a, b) -> Pair.with(a.getValue0() + b.getValue0(), a.getValue1() + b.getValue1()))
                    .ifPresent(p -> categoryAveragePercentages.put(k, p.getValue0() / p.getValue1()));
        });

        return entries.values().stream().peek(v -> calculateStatistics(v, categoryAveragePercentages)).toList();
    }

    public Optional<Valuation> getForItem(Long auctionItemId, Long conditionId) {
        Collection<Valuation> valuations =
                getAll(List.of(new EqualFilterCondition<>("aiv.auction_item_id", auctionItemId),
                        new EqualFilterCondition<>("aiv.condition_id", conditionId)));

        return valuations.stream().findFirst();
    }

    private void calculateStatistics(Valuation valuation, Map<Pair<Long, Long>, Double> categoryAveragePercentages) {
        Optional<Pair<MonetaryAmount, MonetaryAmount>> catalogBoundary = getCatalogBoundary(valuation);

        valuation.getValuationEntries().stream()
                .map(ValuationEntry::getPrice)
                .filter(Objects::nonNull)
                .map(MultiCurrencyMonetaryAmount::defaultCurrencyAmount)
                .filter(m -> catalogBoundary.map(cv -> m.isGreaterThan(cv.getValue0()) && m.isLessThan(cv.getValue1()))
                        .orElse(true))
                .map(p -> new StatsReductor(p, p, p, 1))
                .reduce((a, b) -> new StatsReductor(a.sum.add(b.sum), a.min.isLessThan(b.min) ? a.min : b.min,
                        a.max.isGreaterThan(b.max) ? a.max : b.max, a.count() + 1))
                .ifPresent(stats -> {
                    valuation.setAveragePrice(MultiCurrencyMonetaryAmount.of(
                            stats.sum.divide(stats.count).with(MonetaryOperators.rounding(RoundingMode.HALF_UP, 2))));
                    valuation.setMinPrice(MultiCurrencyMonetaryAmount.of(stats.min));
                    valuation.setMaxPrice(MultiCurrencyMonetaryAmount.of(stats.max));
                    valuation.setAuctionCount(stats.count);
                });

        if (valuation.getCatalogValue() != null) {
            Optional.ofNullable(categoryAveragePercentages.get(
                            Pair.with(valuation.getAuctionItemCategoryId(), valuation.getConditionId())))
                    .ifPresent(d -> {
                        valuation.setCategoryAveragePrice(MultiCurrencyMonetaryAmount.of(
                                valuation.getCatalogValue().defaultCurrencyAmount().multiply(d)
                                        .with(MonetaryOperators.rounding(RoundingMode.HALF_UP, 2))));
                        valuation.setCategoryAveragePercentage(d);
                    });
        }
        AppContext.getPriceRecommendationService().calculateRecommendation(valuation)
                .ifPresent(valuation::setRecommendedPrice);
    }

    private Optional<Pair<MonetaryAmount, MonetaryAmount>> getCatalogBoundary(Valuation valuation) {
        return Optional.ofNullable(valuation.getCatalogValue())
                .map(MultiCurrencyMonetaryAmount::defaultCurrencyAmount)
                .map(cv -> Pair.with(Money.of(0, AppContext.getConfigurationService().getDefaultCurrency()),
                        cv.multiply(AppContext.getConfigurationService().getMaxPriceCatalogValueMultiplier())));
    }

    @Override
    public Optional<Valuation> getById(Long id) {
        return Optional.empty();
    }

    private record StatsReductor(MonetaryAmount sum, MonetaryAmount min, MonetaryAmount max, Integer count) {
    }
}