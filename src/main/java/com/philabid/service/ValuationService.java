package com.philabid.service;

import com.philabid.AppContext;
import com.philabid.database.ValuationEntryRepository;
import com.philabid.database.util.FilterCondition;
import com.philabid.model.Valuation;
import com.philabid.model.ValuationEntry;
import com.philabid.util.MultiCurrencyMonetaryAmount;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
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
            categoryAveragePercentageEntries.computeIfAbsent(
                    Pair.with(valuationEntry.getAuctionItemCategoryId(), valuationEntry.getConditionId()),
                    k -> new ArrayList<>()).add(valuationEntry.getArchivedCatalogValuePercentage());
        });

        Map<Pair<Long, Long>, Double> categoryAveragePercentages = new HashMap<>();

        categoryAveragePercentageEntries.forEach((k, v) -> {
            v.stream()
                    .filter(Objects::nonNull)
                    .filter(p -> p > 0)
                    .map(p -> Pair.with(p, 1))
                    .reduce((a, b) -> Pair.with(a.getValue0() + b.getValue0(), a.getValue1() + b.getValue1()))
                    .ifPresent(p -> categoryAveragePercentages.put(k, p.getValue0() / p.getValue1()));
        });

        return entries.values().stream().peek(v -> calculateStatistics(v, categoryAveragePercentages)).toList();
    }

    private void calculateStatistics(Valuation valuation, Map<Pair<Long, Long>, Double> categoryAveragePercentages) {
        CurrencyUnit defaultCurrency = AppContext.getConfigurationService().getDefaultCurrency();
        valuation.getValuationEntries().stream()
                .map(ValuationEntry::getPrice)
                .filter(Objects::nonNull)
                .map(MultiCurrencyMonetaryAmount::defaultCurrencyAmount)
                .map(p -> new StatsReductor(p, p, p, 1))
                .reduce((a, b) -> new StatsReductor(a.sum.add(b.sum), a.min.isLessThan(b.min) ? a.min : b.min,
                        a.max.isGreaterThan(b.max) ? a.max : b.max, a.count() + 1))
                .ifPresent(stats -> {
                    valuation.setAveragePrice(MultiCurrencyMonetaryAmount.of(stats.sum.divide(stats.count)));
                    valuation.setMinPrice(MultiCurrencyMonetaryAmount.of(stats.min));
                    valuation.setMaxPrice(MultiCurrencyMonetaryAmount.of(stats.max));
                    valuation.setAuctionCount(stats.count);
                    if (valuation.getCatalogValue() != null) {
                        Optional.ofNullable(categoryAveragePercentages.get(
                                        Pair.with(valuation.getAuctionItemCategoryId(), valuation.getConditionId())))
                                .ifPresent(d -> valuation.setCategoryAveragePrice(MultiCurrencyMonetaryAmount.of(
                                        valuation.getCatalogValue().defaultCurrencyAmount().multiply(d))));
                    }
                });
    }

    @Override
    public Optional<Valuation> getById(Long id) {
        return Optional.empty();
    }

    private record StatsReductor(MonetaryAmount sum, MonetaryAmount min, MonetaryAmount max, Integer count) {
    }
}