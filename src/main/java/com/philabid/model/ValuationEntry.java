package com.philabid.model;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.MonetaryAmount;

/**
 * A record representing aggregated statistics for a specific auction item and condition.
 * This is used to display data in the statistics view.
 */
public final class ValuationEntry extends BaseModel<ValuationEntry> {
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
    private Boolean catalogActive = false;
    private MultiCurrencyMonetaryAmount price;
    private Double archivedCatalogValuePercentage;

    public Double getArchivedCatalogValuePercentage() {
        return archivedCatalogValuePercentage;
    }

    public void setArchivedCatalogValuePercentage(Double archivedCatalogValuePercentage) {
        this.archivedCatalogValuePercentage = archivedCatalogValuePercentage;
    }

    public Long getAuctionItemCategoryId() {
        return auctionItemCategoryId;
    }

    public void setAuctionItemCategoryId(Long auctionItemCategoryId) {
        this.auctionItemCategoryId = auctionItemCategoryId;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getAuctionItemCategoryCode() {
        return auctionItemCategoryCode;
    }

    public void setAuctionItemCategoryCode(String auctionItemCategoryCode) {
        this.auctionItemCategoryCode = auctionItemCategoryCode;
    }

    public Long getAuctionItemOrderNumber() {
        return auctionItemOrderNumber;
    }

    public void setAuctionItemOrderNumber(Long auctionItemOrderNumber) {
        this.auctionItemOrderNumber = auctionItemOrderNumber;
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
        return getAuctionItemCatalogNumber();
    }
}