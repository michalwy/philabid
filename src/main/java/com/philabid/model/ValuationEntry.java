package com.philabid.model;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.MonetaryAmount;

/**
 * A record representing aggregated statistics for a specific trading item and condition.
 * This is used to display data in the statistics view.
 */
public final class ValuationEntry extends BaseModel<ValuationEntry> {
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
    private Boolean catalogActive = false;
    private MultiCurrencyMonetaryAmount price;
    private Double archivedCatalogValuePercentage;

    public Double getArchivedCatalogValuePercentage() {
        return archivedCatalogValuePercentage;
    }

    public void setArchivedCatalogValuePercentage(Double archivedCatalogValuePercentage) {
        this.archivedCatalogValuePercentage = archivedCatalogValuePercentage;
    }

    public Long getTradingItemCategoryId() {
        return tradingItemCategoryId;
    }

    public void setTradingItemCategoryId(Long tradingItemCategoryId) {
        this.tradingItemCategoryId = tradingItemCategoryId;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
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

    public Long getTradingItemOrderNumber() {
        return tradingItemOrderNumber;
    }

    public void setTradingItemOrderNumber(Long tradingItemOrderNumber) {
        this.tradingItemOrderNumber = tradingItemOrderNumber;
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

    public MultiCurrencyMonetaryAmount getPrice() {
        return price;
    }

    public void setPrice(MultiCurrencyMonetaryAmount price) {
        this.price = price;
    }

    public void setPrice(MonetaryAmount price) {
        this.price = MultiCurrencyMonetaryAmount.of(price);
    }

    public Boolean isCatalogActive() {
        return catalogActive;
    }

    public void setCatalogActive(Boolean catalogActive) {
        this.catalogActive = catalogActive;
    }

    @Override
    public String getDisplayName() {
        return getTradingItemCatalogNumber();
    }
}