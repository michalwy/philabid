package com.philabid.model;

/**
 * Represents a currency entity.
 */
public class Currency {
    private String code;
    private String name;
    private String symbol;

    // Constructors
    public Currency() {}

    public Currency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    // Getters and setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * The string representation in a ComboBox will be the currency name and code.
     */
    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
