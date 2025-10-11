package com.philabid.model;

import java.time.LocalDateTime;

/**
 * Represents a condition of a philatelic item (e.g., MNH, Used).
 */
public class Condition extends BaseModel<Condition> {

    private String name;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Condition() {
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
        return name + " (" + code + ")";
    }
}
