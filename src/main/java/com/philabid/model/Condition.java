package com.philabid.model;

/**
 * Represents a condition of a philatelic item (e.g., MNH, Used).
 */
public class Condition extends BaseModel<Condition> {

    private String name;
    private String code;

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

    @Override
    public String getDisplayName() {
        return name + " (" + code + ")";
    }
}
