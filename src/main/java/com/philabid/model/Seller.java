package com.philabid.model;

/**
 * Represents an auction house entity.
 */
public class Seller extends BaseModel<Seller> {
    private String name;
    private String fullName;
    private String contactEmail;
    private String contactPhone;

    public Seller() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String website) {
        this.fullName = fullName;
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

    @Override
    public String getDisplayName() {
        return name + (fullName != null && !fullName.isBlank() ? (" (" + fullName + ")") : "");
    }
}