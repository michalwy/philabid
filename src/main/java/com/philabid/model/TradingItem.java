package com.philabid.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an item that can be put up for auction.
 */
public class TradingItem extends BaseModel<TradingItem> {

    private final static Pattern ORDER_NUMBER_PATTERN = Pattern.compile("\\d+");
    private Long categoryId;
    private String catalogNumber;
    private Long orderNumber;
    private String notes;

    // Fields for joined data from Category and Catalog
    private String categoryName;
    private String categoryCode;
    private Long categoryOrderNumber;
    private String catalogName;
    private Integer catalogIssueYear;

    // Constructors
    public TradingItem() {
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

    public Long getCategoryOrderNumber() {
        return categoryOrderNumber;
    }

    public void setCategoryOrderNumber(Long categoryOrderNumber) {
        this.categoryOrderNumber = categoryOrderNumber;
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
}
