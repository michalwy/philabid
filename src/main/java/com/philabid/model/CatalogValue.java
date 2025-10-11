package com.philabid.model;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

/**
 * Represents a catalog value for a specific auction item in a given condition.
 */
public class CatalogValue extends BaseModel<CatalogValue> {

    private Long auctionItemId;
    private Long conditionId;
    private Long catalogId;
    private MultiCurrencyMonetaryAmount value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Joined fields for display purposes
    private String auctionItemCatalogNumber;
    private Long auctionItemOrderNumber;
    private String auctionItemCategoryName;
    private String auctionItemCategoryCode;
    private String conditionName;
    private String conditionCode;
    private String catalogName;
    private Integer catalogIssueYear;

    // Constructors
    public CatalogValue() {
    }

    // Getters and Setters
    public Long getAuctionItemId() {
        return auctionItemId;
    }

    public void setAuctionItemId(Long auctionItemId) {
        this.auctionItemId = auctionItemId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAuctionItemCatalogNumber() {
        return auctionItemCatalogNumber;
    }

    public void setAuctionItemCatalogNumber(String auctionItemCatalogNumber) {
        this.auctionItemCatalogNumber = auctionItemCatalogNumber;
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

    public String getAuctionItemCategoryCode() {
        return auctionItemCategoryCode;
    }

    public void setAuctionItemCategoryCode(String auctionItemCategoryCode) {
        this.auctionItemCategoryCode = auctionItemCategoryCode;
    }

    public String getAuctionItemCategoryName() {
        return auctionItemCategoryName;
    }

    public void setAuctionItemCategoryName(String auctionItemCategoryName) {
        this.auctionItemCategoryName = auctionItemCategoryName;
    }

    public Integer getCatalogIssueYear() {
        return catalogIssueYear;
    }

    public void setCatalogIssueYear(Integer catalogIssueYear) {
        this.catalogIssueYear = catalogIssueYear;
    }

    public Long getAuctionItemOrderNumber() {
        return auctionItemOrderNumber;
    }

    public void setAuctionItemOrderNumber(Long auctionItemOrderNumber) {
        this.auctionItemOrderNumber = auctionItemOrderNumber;
    }

    @Override
    public String getDisplayName() {
        return this.getAuctionItemCatalogNumber() + " - " + this.getConditionName();
    }
}
