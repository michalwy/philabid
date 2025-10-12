package com.philabid.model;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an item that can be put up for auction.
 */
public class AuctionItem extends BaseModel<AuctionItem> {

    private final static Pattern ORDER_NUMBER_PATTERN = Pattern.compile("\\d+");
    private Long categoryId;
    private String catalogNumber;
    private Long orderNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Fields for joined data from Category and Catalog
    private String categoryName;
    private String categoryCode;
    private String catalogName;
    private Integer catalogIssueYear;

    // Constructors
    public AuctionItem() {
    }

    public static Long calculateOrderNumber(String catalogNumber) {
        if (catalogNumber == null || catalogNumber.isBlank()) {
            return 0L;
        }

        // This regex finds the first sequence of one or more digits.
        Matcher matcher = ORDER_NUMBER_PATTERN.matcher(catalogNumber);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(0));
        }

        return 0L;
    }

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCatalogNumber() {
        return catalogNumber;
    }

    public void setCatalogNumber(String catalogNumber) {
        this.catalogNumber = catalogNumber;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
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

    @Override
    public String getDisplayName() {
        return this.getCategoryName() + " - " + this.getCatalogNumber();
    }

    @Override
    public String getFilterField() {
        return "ai.id";
    }
}
