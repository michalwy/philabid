package com.philabid.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.time.LocalDateTime;

/**
 * Represents a stamp catalog entity.
 */
public class Catalog extends BaseModel<Catalog> {

    private String name;
    private Integer issueYear;
    private CurrencyUnit currency;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Catalog() {
        this.active = true; // Default to active
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIssueYear() {
        return issueYear;
    }

    public void setIssueYear(Integer issueYear) {
        this.issueYear = issueYear;
    }

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyUnit currency) {
        this.currency = currency;
    }

    public void setCurrency(String currencyCode) {
        this.currency = Monetary.getCurrency(currencyCode);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
    
    @Override
    public String getDisplayName() {
        return name + " (" + issueYear + ")";
    }
}
