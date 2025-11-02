package com.philabid.model;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.MonetaryAmount;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A record representing aggregated statistics for a specific auction item and condition.
 * This is used to display data in the statistics view.
 */
public final class Valuation extends BaseModel<Valuation> {
    private final List<ValuationEntry> valuationEntries = new ArrayList<>();
    private Boolean catalogActive = false;
    private long auctionItemId;
    private String auctionItemCatalogNumber;
    private Long auctionItemOrderNumber;
    private String auctionItemCategoryName;
    private String auctionItemCategoryCode;
    private Long auctionItemCategoryId;
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

    public Long getAuctionItemCategoryId() {
        return auctionItemCategoryId;
    }

    public void setAuctionItemCategoryId(Long auctionItemCategoryId) {
        this.auctionItemCategoryId = auctionItemCategoryId;
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

    public Long getAuctionItemOrderNumber() {
        return auctionItemOrderNumber;
    }

    public void setAuctionItemOrderNumber(Long auctionItemOrderNumber) {
        this.auctionItemOrderNumber = auctionItemOrderNumber;
    }

    public Boolean isCatalogActive() {
        return catalogActive;
    }

    public void setCatalogActive(Boolean catalogActive) {
        this.catalogActive = catalogActive;
    }

    public String getAuctionItemCategoryCode() {
        return auctionItemCategoryCode;
    }

    public void setAuctionItemCategoryCode(String auctionItemCategoryCode) {
        this.auctionItemCategoryCode = auctionItemCategoryCode;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public long getAuctionItemId() {
        return auctionItemId;
    }

    public void setAuctionItemId(long auctionItemId) {
        this.auctionItemId = auctionItemId;
    }

    public String getAuctionItemCatalogNumber() {
        return auctionItemCatalogNumber;
    }

    public void setAuctionItemCatalogNumber(String auctionItemCatalogNumber) {
        this.auctionItemCatalogNumber = auctionItemCatalogNumber;
    }

    public String getAuctionItemCategoryName() {
        return auctionItemCategoryName;
    }

    public void setAuctionItemCategoryName(String auctionItemCategoryName) {
        this.auctionItemCategoryName = auctionItemCategoryName;
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
        return getAuctionItemCatalogNumber();
    }
}