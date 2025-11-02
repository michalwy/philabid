package com.philabid.model;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.MonetaryAmount;

/**
 * Represents a catalog value for a specific trading item in a given condition.
 */
public class CatalogValue extends BaseModel<CatalogValue> {
    private Long tradingItemId;
    private Long conditionId;
    private Long catalogId;
    private MultiCurrencyMonetaryAmount value;

    // Joined fields for display purposes
    private String tradingItemCatalogNumber;
    private Long tradingItemOrderNumber;
    private String tradingItemCategoryName;
    private String tradingItemCategoryCode;
    private String conditionName;
    private String conditionCode;
    private String catalogName;
    private Integer catalogIssueYear;

    // Constructors
    public CatalogValue() {
    }

    // Getters and Setters
    public Long getTradingItemId() {
        return tradingItemId;
    }

    public void setTradingItemId(Long tradingItemId) {
        this.tradingItemId = tradingItemId;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public MultiCurrencyMonetaryAmount getValue() {
        return value;
    }

    public void setValue(MultiCurrencyMonetaryAmount value) {
        this.value = value;
    }

    public void setValue(MonetaryAmount value) {
        this.value = MultiCurrencyMonetaryAmount.of(value);
    }

    public String getTradingItemCatalogNumber() {
        return tradingItemCatalogNumber;
    }

    public void setTradingItemCatalogNumber(String tradingItemCatalogNumber) {
        this.tradingItemCatalogNumber = tradingItemCatalogNumber;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getTradingItemCategoryCode() {
        return tradingItemCategoryCode;
    }

    public void setTradingItemCategoryCode(String tradingItemCategoryCode) {
        this.tradingItemCategoryCode = tradingItemCategoryCode;
    }

    public String getTradingItemCategoryName() {
        return tradingItemCategoryName;
    }

    public void setTradingItemCategoryName(String tradingItemCategoryName) {
        this.tradingItemCategoryName = tradingItemCategoryName;
    }

    public Integer getCatalogIssueYear() {
        return catalogIssueYear;
    }

    public void setCatalogIssueYear(Integer catalogIssueYear) {
        this.catalogIssueYear = catalogIssueYear;
    }

    public Long getTradingItemOrderNumber() {
        return tradingItemOrderNumber;
    }

    public void setTradingItemOrderNumber(Long tradingItemOrderNumber) {
        this.tradingItemOrderNumber = tradingItemOrderNumber;
    }

    @Override
    public String getDisplayName() {
        return this.getTradingItemCatalogNumber() + " - " + this.getConditionName();
    }
}
