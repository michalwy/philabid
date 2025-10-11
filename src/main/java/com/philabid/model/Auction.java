package com.philabid.model;

import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents an auction for a specific philatelic item.
 */
public class Auction extends BaseModel<Auction> {

    private Long auctionHouseId;
    private Long auctionItemId;
    private Long conditionId;
    private String lotId;
    private String url;
    private MultiCurrencyMonetaryAmount currentPrice;
    private MultiCurrencyMonetaryAmount maxBid;
    private LocalDateTime endDate;
    private boolean archived;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String auctionHouseName;
    private CurrencyUnit auctionHouseCurrency;
    private String auctionItemCatalogNumber;
    private Long auctionItemOrderNumber;
    private String auctionItemCategoryName;
    private String auctionItemCategoryCode;
    private Long auctionItemCategoryId;
    private String conditionName;
    private String conditionCode;

    private MultiCurrencyMonetaryAmount catalogValue;
    private MultiCurrencyMonetaryAmount archivedCatalogValue;
    private Double archivedCatalogValuePercentage;
    private boolean catalogActive = false; // Default to true
    private MultiCurrencyMonetaryAmount recommendedPrice;

    private List<Auction> archivedAuctions;
    private List<Auction> categoryArchivedAuction;

    public Auction() {
        this.archived = false;
    }

    // Getters and Setters
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

    public MultiCurrencyMonetaryAmount getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(MonetaryAmount currentPrice) {
        this.currentPrice = currentPrice != null ? MultiCurrencyMonetaryAmount.of(currentPrice) : null;
    }

    public void setRawCurrentPrice(MonetaryAmount currentPrice) {
        this.currentPrice = currentPrice != null ? new MultiCurrencyMonetaryAmount(currentPrice, null) : null;
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

    public Long getAuctionItemCategoryId() {
        return auctionItemCategoryId;
    }

    public void setAuctionItemCategoryId(Long auctionItemCategoryId) {
        this.auctionItemCategoryId = auctionItemCategoryId;
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

    public MultiCurrencyMonetaryAmount getCatalogValue() {
        return catalogValue;
    }

    public void setCatalogValue(MultiCurrencyMonetaryAmount catalogValue) {
        this.catalogValue = catalogValue;
    }

    public void setCatalogValue(MonetaryAmount catalogValue) {
        this.catalogValue = MultiCurrencyMonetaryAmount.of(catalogValue);
    }

    public void setRawCatalogValue(MonetaryAmount catalogValue) {
        this.catalogValue = new MultiCurrencyMonetaryAmount(catalogValue, null);
    }

    public MultiCurrencyMonetaryAmount getArchivedCatalogValue() {
        return archivedCatalogValue;
    }

    public void setArchivedCatalogValue(MultiCurrencyMonetaryAmount archivedCatalogValue) {
        this.archivedCatalogValue = archivedCatalogValue;
    }

    public void setRawArchivedCatalogValue(MonetaryAmount archivedCatalogValue) {
        this.archivedCatalogValue = new MultiCurrencyMonetaryAmount(archivedCatalogValue, null);
    }

    public Double getArchivedCatalogValuePercentage() {
        return archivedCatalogValuePercentage;
    }

    public void setArchivedCatalogValuePercentage(Double archivedCatalogValuePercentage) {
        this.archivedCatalogValuePercentage = archivedCatalogValuePercentage;
    }

    public boolean isCatalogActive() {
        return catalogActive;
    }

    public void setCatalogActive(boolean catalogActive) {
        this.catalogActive = catalogActive;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public MultiCurrencyMonetaryAmount getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(MonetaryAmount maxBid) {
        this.maxBid = maxBid != null ? MultiCurrencyMonetaryAmount.of(maxBid) : null;
    }

    public void setRawMaxBid(MonetaryAmount maxBid) {
        this.maxBid = maxBid != null ? new MultiCurrencyMonetaryAmount(maxBid, null) : null;
    }

    public MultiCurrencyMonetaryAmount getRecommendedPrice() {
        return recommendedPrice;
    }

    public void setRecommendedPrice(MultiCurrencyMonetaryAmount recommendedPrice) {
        this.recommendedPrice = recommendedPrice;
    }

    public boolean isFinished() {
        return endDate.isBefore(LocalDateTime.now());
    }

    public List<Auction> getArchivedAuctions() {
        return archivedAuctions;
    }

    public void setArchivedAuctions(List<Auction> archivedAuctions) {
        this.archivedAuctions = archivedAuctions;
    }

    public List<Auction> getCategoryArchivedAuction() {
        return categoryArchivedAuction;
    }

    public void setCategoryArchivedAuction(List<Auction> categoryArchivedAuction) {
        this.categoryArchivedAuction = categoryArchivedAuction;
    }

    public CurrencyUnit getAuctionHouseCurrency() {
        return auctionHouseCurrency;
    }

    public void setAuctionHouseCurrency(CurrencyUnit auctionHouseCurrency) {
        this.auctionHouseCurrency = auctionHouseCurrency;
    }

    public void setCurrency(String currencyCode) {
        this.auctionHouseCurrency = Monetary.getCurrency(currencyCode);
    }

    @Override
    public String getDisplayName() {
        return this.getLotId() + ": " + this.getAuctionItemCategoryName() + " - " + this.getAuctionItemCatalogNumber() +
                " - " + this.getConditionName();
    }
}
