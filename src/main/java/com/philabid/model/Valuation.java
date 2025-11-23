package com.philabid.model;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A record representing aggregated statistics for a specific trading item and condition.
 * This is used to display data in the statistics view.
 */
public final class Valuation extends BaseModel<Valuation> {
    private final List<ValuationEntry> valuationEntries = new ArrayList<>();
    private Boolean catalogActive = false;
    private long tradingItemId;
    private String tradingItemCatalogNumber;
    private Long tradingItemOrderNumber;
    private String tradingItemCategoryName;
    private String tradingItemCategoryCode;
    private Long tradingItemCategoryOrderNumber;
    private Long tradingItemCategoryId;
    private long conditionId;
    private String conditionName;
    private String conditionCode;
    private MultiCurrencyMonetaryAmount catalogValue;
    private MultiCurrencyMonetaryAmount averagePrice;
    private MultiCurrencyMonetaryAmount medianPrice;
    private MultiCurrencyMonetaryAmount minPrice;
    private MultiCurrencyMonetaryAmount maxPrice;
    private MultiCurrencyMonetaryAmount categoryAveragePrice;
    private MultiCurrencyMonetaryAmount recommendedPrice;
    private Integer auctionCount;
    private Double categoryAveragePercentage;

    public Double getCategoryAveragePercentage() {
        return categoryAveragePercentage;
    }

    public void setCategoryAveragePercentage(Double categoryAveragePercentage) {
        this.categoryAveragePercentage = categoryAveragePercentage;
    }

    public MultiCurrencyMonetaryAmount getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(MultiCurrencyMonetaryAmount recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public Long getTradingItemCategoryId() {
        return tradingItemCategoryId;
    }

    public void setTradingItemCategoryId(Long tradingItemCategoryId) {
        this.tradingItemCategoryId = tradingItemCategoryId;
    }

    public MultiCurrencyMonetaryAmount getCategoryAveragePrice() {
        return categoryAveragePrice;
    }

    public void setCategoryAveragePrice(MultiCurrencyMonetaryAmount categoryAveragePrice) {
        this.categoryAveragePrice = categoryAveragePrice;
    }

    public MultiCurrencyMonetaryAmount getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(MultiCurrencyMonetaryAmount averagePrice) {
        this.averagePrice = averagePrice;
    }

    public MultiCurrencyMonetaryAmount getMedianPrice() {
        return medianPrice;
    }

    public void setMedianPrice(MultiCurrencyMonetaryAmount medianPrice) {
        this.medianPrice = medianPrice;
    }

    public MultiCurrencyMonetaryAmount getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(MultiCurrencyMonetaryAmount minPrice) {
        this.minPrice = minPrice;
    }

    public MultiCurrencyMonetaryAmount getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(MultiCurrencyMonetaryAmount maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getAuctionCount() {
        return auctionCount;
    }

    public void setAuctionCount(Integer auctionCount) {
        this.auctionCount = auctionCount;
    }

    public Long getTradingItemOrderNumber() {
        return tradingItemOrderNumber;
    }

    public void setTradingItemOrderNumber(Long tradingItemOrderNumber) {
        this.tradingItemOrderNumber = tradingItemOrderNumber;
    }

    public Boolean isCatalogActive() {
        return catalogActive;
    }

    public void setCatalogActive(Boolean catalogActive) {
        this.catalogActive = catalogActive;
    }

    public String getTradingItemCategoryCode() {
        return tradingItemCategoryCode;
    }

    public void setTradingItemCategoryCode(String tradingItemCategoryCode) {
        this.tradingItemCategoryCode = tradingItemCategoryCode;
    }

    public Long getTradingItemCategoryOrderNumber() {
        return tradingItemCategoryOrderNumber;
    }

    public void setTradingItemCategoryOrderNumber(Long tradingItemCategoryOrderNumber) {
        this.tradingItemCategoryOrderNumber = tradingItemCategoryOrderNumber;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public long getTradingItemId() {
        return tradingItemId;
    }

    public void setTradingItemId(long tradingItemId) {
        this.tradingItemId = tradingItemId;
    }

    public String getTradingItemCatalogNumber() {
        return tradingItemCatalogNumber;
    }

    public void setTradingItemCatalogNumber(String tradingItemCatalogNumber) {
        this.tradingItemCatalogNumber = tradingItemCatalogNumber;
    }

    public String getTradingItemCategoryName() {
        return tradingItemCategoryName;
    }

    public void setTradingItemCategoryName(String tradingItemCategoryName) {
        this.tradingItemCategoryName = tradingItemCategoryName;
    }

    public long getConditionId() {
        return conditionId;
    }

    public void setConditionId(long conditionId) {
        this.conditionId = conditionId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public MultiCurrencyMonetaryAmount getCatalogValue() {
        return catalogValue;
    }

    public void setCatalogValue(MonetaryAmount catalogValue) {
        this.catalogValue = MultiCurrencyMonetaryAmount.of(catalogValue);
    }

    public void setCatalogValue(MultiCurrencyMonetaryAmount catalogValue) {
        this.catalogValue = catalogValue;
    }

    public void addValuationEntry(ValuationEntry valuationEntry) {
        valuationEntries.add(valuationEntry);
    }

    public Collection<ValuationEntry> getValuationEntries() {
        return valuationEntries;
    }

    @Override
    public String getDisplayName() {
        return getTradingItemCatalogNumber();
    }
}