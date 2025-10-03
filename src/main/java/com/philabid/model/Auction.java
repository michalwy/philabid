package com.philabid.model;

import javax.money.MonetaryAmount;
import java.time.LocalDateTime;

/**
 * Represents an auction for a specific philatelic item.
 */
public class Auction {

    private Long id;
    private Long auctionHouseId;
    private Long auctionItemId;
    private Long conditionId;
    private String lotId;
    private String url;
    private MonetaryAmount currentPrice;
    private LocalDateTime endDate;
    private boolean archived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String auctionHouseName;
    private String auctionItemCatalogNumber;
    private Long auctionItemOrderNumber;
    private String auctionItemCategoryName;
    private String auctionItemCategoryCode;
    private String conditionName;
    private String conditionCode;

    private MonetaryAmount catalogValue;

    public Auction() {
        this.archived = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuctionHouseId() {
        return auctionHouseId;
    }

    public void setAuctionHouseId(Long auctionHouseId) {
        this.auctionHouseId = auctionHouseId;
    }

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

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MonetaryAmount getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(MonetaryAmount currentPrice) {
        this.currentPrice = currentPrice;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
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

    public Long getAuctionItemOrderNumber() {
        return auctionItemOrderNumber;
    }

    public void setAuctionItemOrder(Long auctionItemOrderNumber) {
        this.auctionItemOrderNumber = auctionItemOrderNumber;
    }

    public String getAuctionItemCategoryName() {
        return auctionItemCategoryName;
    }

    public void setAuctionItemCategoryName(String auctionItemCategoryName) {
        this.auctionItemCategoryName = auctionItemCategoryName;
    }

    public String getAuctionItemCategoryCode() {
        return auctionItemCategoryCode;
    }

    public void setAuctionItemCategoryCode(String auctionItemCategoryCode) {
        this.auctionItemCategoryCode = auctionItemCategoryCode;
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

    public String getAuctionHouseName() {
        return auctionHouseName;
    }

    public void setAuctionHouseName(String auctionHouseName) {
        this.auctionHouseName = auctionHouseName;
    }

    public MonetaryAmount getCatalogValue() {
        return catalogValue;
    }

    public void setCatalogValue(MonetaryAmount catalogValue) {
        this.catalogValue = catalogValue;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
