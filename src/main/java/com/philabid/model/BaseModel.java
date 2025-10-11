package com.philabid.model;

public abstract class BaseModel<T extends BaseModel<T>> {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract String getDisplayName();
}
