package com.philabid.model;

import java.time.LocalDateTime;

/**
 * Represents a category for philatelic lots, typically corresponding to a country or a specific period.
 */
public class Category extends BaseModel<Category> {

    private String name;
    private String code;
    private Long catalogId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String catalogName;
    private Integer catalogIssueYear;

    // Constructors
    public Category() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
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
        return name;
    }

    @Override
    public String getFilterField() {
        return "catg.id";
    }
}
