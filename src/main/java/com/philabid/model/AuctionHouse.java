package com.philabid.model;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

/**
 * Represents an auction house entity.
 */
public class AuctionHouse extends BaseModel<AuctionHouse> {
    private String name;
    private String website;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String country;
    private CurrencyUnit currency;

    // Constructors
    public AuctionHouse() {
    }

    public AuctionHouse(String name) {
        this.name = name;
        this.currency = Monetary.getCurrency("USD");
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    @Override
    public String getDisplayName() {
        return name;
    }
}