package com.philabid.model;

import com.philabid.AppContext;
import com.philabid.util.MultiCurrencyMonetaryAmount;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an auction for a specific philatelic item.
 */
public class Auction extends BaseModel<Auction> {

    private Long auctionHouseId;
    private Long sellerId;
    private Long tradingItemId;
    private Long conditionId;
    private String lotId;
    private String url;
    private MultiCurrencyMonetaryAmount currentPrice;
    private MultiCurrencyMonetaryAmount maxBid;
    private LocalDateTime endDate;
    private boolean archived;

    private String auctionHouseName;
    private CurrencyUnit auctionHouseCurrency;
    private String sellerName;
    private String sellerFullName;
    private String tradingItemCatalogNumber;
    private Long tradingItemOrderNumber;
    private String tradingItemCategoryName;
    private String tradingItemCategoryCode;
    private Long tradingItemCategoryId;
    private String conditionName;
    private String conditionCode;

    private MultiCurrencyMonetaryAmount catalogValue;
    private MultiCurrencyMonetaryAmount archivedCatalogValue;
    private Double archivedCatalogValuePercentage;
    private boolean catalogActive = false; // Default to true
    private MultiCurrencyMonetaryAmount recommendedPrice;
    private String catalogName;
    private Integer catalogIssueYear;

    private List<Auction> archivedAuctions;
    private List<Auction> categoryArchivedAuctions;
    private List<Auction> activeAuctions;

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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

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
        this.currentPrice = MultiCurrencyMonetaryAmount.of(currentPrice);
    }

    public void setRawCurrentPrice(MonetaryAmount currentPrice) {
        this.currentPrice = MultiCurrencyMonetaryAmount.of(currentPrice, null);
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getTradingItemCatalogNumber() {
        return tradingItemCatalogNumber;
    }

    public void setTradingItemCatalogNumber(String tradingItemCatalogNumber) {
        this.tradingItemCatalogNumber = tradingItemCatalogNumber;
    }

    public Long getTradingItemOrderNumber() {
        return tradingItemOrderNumber;
    }

    public void setTradingItemOrderNumber(Long tradingItemOrderNumber) {
        this.tradingItemOrderNumber = tradingItemOrderNumber;
    }

    public String getTradingItemCategoryName() {
        return tradingItemCategoryName;
    }

    public void setTradingItemCategoryName(String tradingItemCategoryName) {
        this.tradingItemCategoryName = tradingItemCategoryName;
    }

    public String getTradingItemCategoryCode() {
        return tradingItemCategoryCode;
    }

    public void setTradingItemCategoryCode(String tradingItemCategoryCode) {
        this.tradingItemCategoryCode = tradingItemCategoryCode;
    }

    public Long getTradingItemCategoryId() {
        return tradingItemCategoryId;
    }

    public void setTradingItemCategoryId(Long tradingItemCategoryId) {
        this.tradingItemCategoryId = tradingItemCategoryId;
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

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerFullName() {
        return sellerFullName;
    }

    public void setSellerFullName(String sellerFullName) {
        this.sellerFullName = sellerFullName;
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
        this.catalogValue = MultiCurrencyMonetaryAmount.of(catalogValue, null);
    }

    public MultiCurrencyMonetaryAmount getArchivedCatalogValue() {
        return archivedCatalogValue;
    }

    public void setArchivedCatalogValue(MultiCurrencyMonetaryAmount archivedCatalogValue) {
        this.archivedCatalogValue = archivedCatalogValue;
    }

    public void setRawArchivedCatalogValue(MonetaryAmount archivedCatalogValue) {
        this.archivedCatalogValue = MultiCurrencyMonetaryAmount.of(archivedCatalogValue, null);
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

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public Integer getCatalogIssueYear() {
        return catalogIssueYear;
    }

    public void setCatalogIssueYear(Integer catalogIssueYear) {
        this.catalogIssueYear = catalogIssueYear;
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

    public List<Auction> getCategoryArchivedAuctions() {
        return categoryArchivedAuctions;
    }

    public void setCategoryArchivedAuctions(List<Auction> categoryArchivedAuctions) {
        this.categoryArchivedAuctions = categoryArchivedAuctions;
    }

    public List<Auction> getActiveAuctions() {
        return activeAuctions;
    }

    public void setActiveAuctions(List<Auction> activeAuctions) {
        this.activeAuctions = activeAuctions;
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
        return this.getLotId() + ": " + this.getTradingItemCategoryName() + " - " + this.getTradingItemCatalogNumber() +
                " - " + this.getConditionName();
    }

    public boolean isWinningBid() {
        if (currentPrice == null || maxBid == null) {
            return false;
        }
        return maxBid.defaultCurrencyAmount().isGreaterThanOrEqualTo(currentPrice.defaultCurrencyAmount());
    }

    public boolean isAlreadyPurchased() {
        return archivedAuctions != null && archivedAuctions.stream().anyMatch(Auction::isWinningBid);
    }

    public boolean isOverpriced() {
        return getCurrentPrice() != null && getRecommendedPrice() != null &&
                getCurrentPrice().defaultCurrencyAmount().isGreaterThan(getRecommendedPrice().defaultCurrencyAmount());
    }

    public boolean isNextBidOverpriced() {
        if (getCurrentPrice() == null || getRecommendedPrice() == null) {
            return false;
        }
        MultiCurrencyMonetaryAmount nextBid =
                AppContext.getAuctionHouseService().getNextBid(auctionHouseId, getCurrentPrice());
        return nextBid.defaultCurrencyAmount().isGreaterThan(getRecommendedPrice().defaultCurrencyAmount());
    }

    public List<AuctionStatus> getAuctionStatuses() {
        List<AuctionStatus> statuses = new ArrayList<>();
        if (isWinningBid()) {
            statuses.add(AuctionStatus.WINNING);
        }
        if (isAlreadyPurchased()) {
            statuses.add(AuctionStatus.OWNED);
        }
        if (isFinished()) {
            statuses.add(AuctionStatus.EXPIRED);
        }
        if (isOverpriced()) {
            statuses.add(AuctionStatus.OVERPRICED);
        } else if (isNextBidOverpriced() && !isWinningBid()) {
            statuses.add(AuctionStatus.NEXT_BID_OVERPRICED);
        }
        return statuses;
    }

    public enum AuctionStatus {
        WINNING,
        EXPIRED,
        OVERPRICED,
        OUTBID,
        NEXT_BID_OVERPRICED,
        OWNED
    }
}
